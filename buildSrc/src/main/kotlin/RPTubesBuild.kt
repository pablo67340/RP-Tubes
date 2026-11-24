import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

/** One Stonecutter node: a Minecraft version built for one loader. */
class RPTubesTarget(
    val minecraft: String,
    val loader: String,
    private val atLeast: (String) -> Boolean,
    private val property: (String) -> String?,
) {
    fun atLeast(version: String): Boolean = atLeast.invoke(version)

    fun prop(key: String): String = property(key) ?: error("$minecraft-$loader is missing $key in stonecutter.properties.toml")

    fun propOrNull(key: String): String? = property(key)

    val forgeLike: Boolean get() = loader != "fabric"

    /** The Java release Mojang ships with this Minecraft version. */
    val java: Int
        get() = when {
            atLeast("26.1") -> 25
            atLeast("1.20.5") -> 21
            else -> 17
        }

    val modId: String get() = prop("mod.id")
    val modVersion: String get() = prop("mod.version")

    /** The JEI plugin only exists for the versions JEI was released on when it was written. */
    val hasJei: Boolean get() = propOrNull("deps.jei") != null

    /** CB Multipart tube parts are built where deps.cbm names a CB Multipart release. */
    val hasCbm: Boolean get() = propOrNull("deps.cbm") != null

    val loaderName: String
        get() = when (loader) {
            "fabric" -> "Fabric"
            "forge" -> "Forge"
            else -> "NeoForge"
        }
}

/**
 * Shared setup for every node. The loader build scripts only add what their Gradle plugin
 * needs; naming, Java level, resource rewriting and the release jar layout all live here.
 */
fun Project.rpTubes(target: RPTubesTarget, loaderProps: Map<String, String>) {
    group = target.prop("mod.group")
    version = "${target.minecraft}-${target.loader}-${target.modVersion}"
    extensions.configure<BasePluginExtension> { archivesName.set(target.modId) }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(target.java))
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(target.java)
    }

    val main = extensions.getByType<SourceSetContainer>().getByName("main")
    for (overlay in listOfNotNull(target.loader, "forgelike".takeIf { target.forgeLike })) {
        val dir = rootProject.file("src/overlays/$overlay")
        if (dir.isDirectory) {
            main.resources.srcDir(dir)
        }
    }

    tasks.named<ProcessResources>("processResources") {
        val description = target.prop("mod.description").replace("{loader}", target.loaderName)
        val props = mapOf(
            "mod_id" to target.modId,
            "mod_name" to target.prop("mod.name"),
            "mod_version" to target.modVersion,
            "mod_authors" to target.prop("mod.authors"),
            "mod_license" to target.prop("mod.license"),
            "mod_description" to description,
            "mod_description_json" to description.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"),
            "minecraft_range" to target.prop("mod.mc_compat"),
            "java" to target.java.toString(),
            "jei_dependency" to jeiDependency(target),
        ) + loaderProps
        inputs.properties(props)
        inputs.property("rptubes_target", "${target.minecraft}-${target.loader}")
        filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "fabric.mod.json")) {
            expand(props)
        }
        includeEmptyDirs = false
        doLast { finishResources(destinationDir, target) }
    }

    tasks.named<Jar>("jar") {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        val attributes = mutableMapOf<String, Any>(
            "Specification-Title" to target.modId,
            "Specification-Vendor" to target.prop("mod.authors"),
            "Specification-Version" to "1",
            "Implementation-Title" to target.prop("mod.name"),
            "Implementation-Version" to target.modVersion,
            "Implementation-Vendor" to target.prop("mod.authors"),
        )
        if (target.atLeast("1.21.11")) {
            attributes["Automatic-Module-Name"] = target.modId
        }
        manifest.attributes(attributes)
    }
}

private fun jeiDependency(target: RPTubesTarget): String {
    if (!target.hasJei) return ""
    val required = if (target.loader == "neoforge") "type=\"optional\"" else "mandatory=false"
    return """
        |[[dependencies.${target.modId}]]
        |modId="jei"
        |$required
        |versionRange="${target.prop("deps.jei_range")}"
        |ordering="NONE"
        |side="CLIENT"
        |""".trimMargin()
}

/** Copies the jar players install into build/libs/{mod version}/ at the root. */
fun Project.collectJar(target: RPTubesTarget, jar: Any) {
    tasks.register<Copy>("collectJar") {
        group = "build"
        description = "Copies the release jar into the root build/libs/${target.modVersion}/"
        from(jar)
        into(rootProject.layout.buildDirectory.dir("libs/${target.modVersion}"))
    }
}

// ------------------------------------------------------------------------------------------
// Resources are kept in the newest layout (26.2 NeoForge). Older versions and the other
// loaders get them rewritten to what that version and loader read.

private fun finishResources(dir: File, t: RPTubesTarget) {
    val files = dir.walkTopDown().filter { it.isFile }.toList()
    val aliased = sortedSetOf<String>()
    for (file in files) {
        val rel = file.relativeTo(dir).invariantSeparatorsPath
        if (!keepResource(rel, t)) {
            file.delete()
            continue
        }
        if (rel.endsWith(".json") && rel != "fabric.mod.json") {
            val original = copyJson(JsonSlurper().parse(file))
            val data = transformJson(rel, copyJson(original), t, aliased)
            if (data != original) {
                writeJson(file, data)
            }
        }
        val dest = resourcePath(rel, t)
        if (dest != rel) {
            val moved = File(dir, dest)
            moved.parentFile.mkdirs()
            check(file.renameTo(moved)) { "could not move $rel to $dest" }
        }
    }
    for (tag in aliased) {
        val out = File(dir, resourcePath("data/${t.modId}/tags/item/$tag.json", t))
        out.parentFile.mkdirs()
        writeJson(out, mapOf("replace" to false, "values" to tagAliases(tag, t).map { mapOf("id" to "#$it", "required" to false) }))
    }
    dir.walkBottomUp().filter { it.isDirectory && it != dir && it.list()?.isEmpty() == true }.forEach { it.delete() }

    writeJson(File(dir, "pack.mcmeta"), packMeta(t))
    if (t.loader == "fabric") {
        val modJson = File(dir, "fabric.mod.json")
        @Suppress("UNCHECKED_CAST")
        val data = copyJson(JsonSlurper().parse(modJson)) as MutableMap<String, Any?>
        if (t.hasJei) {
            @Suppress("UNCHECKED_CAST")
            (data["entrypoints"] as MutableMap<String, Any?>)["jei_mod_plugin"] = listOf("com.rptubes.compat.jei.RPTubesJeiPlugin")
            data["suggests"] = mapOf("jei" to "*")
        }
        if (t.minecraft == "1.21.11") {
            data["accessWidener"] = "${t.modId}.accesswidener"
        }
        writeJson(modJson, data)
    }
}

private fun keepResource(rel: String, t: RPTubesTarget): Boolean = when {
    rel == "META-INF/services/mezz.jei.api.IModPlugin" -> t.hasJei
    // Item model definitions arrived in 1.21.4.
    rel.startsWith("assets/rptubes/items/") -> t.atLeast("1.21.4")
    // Only 1.21.11 Fabric needs the BlockEntityType constructor widened.
    rel == "rptubes.accesswidener" -> t.loader == "fabric" && t.minecraft == "1.21.11"
    // Fabric API only ships c:ingots/iron from 26.1.
    rel == "data/c/tags/item/ingots/iron.json" -> t.loader == "fabric" && !t.atLeast("26.1")
    rel.startsWith("data/rptubes/neoforge/") -> t.forgeLike
    // ProjectE has never had a Fabric release.
    rel.startsWith("data/rptubes/pe_custom_conversions/") -> t.forgeLike
    else -> true
}

private fun resourcePath(rel: String, t: RPTubesTarget): String {
    var path = rel
    if (t.loader == "forge" && !t.atLeast("26.1")) {
        path = path.replace(Regex("^data/c/tags/"), "data/forge/tags/")
    }
    if (t.loader == "forge") {
        path = path.replace(Regex("^data/rptubes/neoforge/"), "data/rptubes/forge/")
    }
    if (!t.atLeast("1.21")) {
        path = path
            .replace(Regex("^data/([^/]+)/(recipe|loot_table)/")) { "data/${it.groupValues[1]}/${it.groupValues[2]}s/" }
            .replace(Regex("^data/([^/]+)/tags/(block|item)/")) { "data/${it.groupValues[1]}/tags/${it.groupValues[2]}s/" }
    }
    return path
}

@Suppress("UNCHECKED_CAST")
private fun transformJson(rel: String, data: Any?, t: RPTubesTarget, aliased: MutableSet<String>): Any? {
    if (data !is MutableMap<*, *>) return data
    val map = data as MutableMap<String, Any?>
    when {
        rel.startsWith("assets/rptubes/models/") -> {
            // Block models only carry their render type from 1.21.11; before that it is set in code.
            if (!t.atLeast("1.21.11")) map.remove("render_type")
        }
        rel.startsWith("assets/rptubes/items/") -> {
            val model = map["model"] as? MutableMap<String, Any?>
            val tints = model?.get("tints") as? List<Any?>
            if (model != null && tints != null && t.loader != "neoforge") {
                model["tints"] = tints.map { (it as Map<String, Any?>) + ("type" to "rptubes:tube_paint") }
            }
        }
        rel.startsWith("data/rptubes/neoforge/biome_modifier/") && t.loader == "forge" -> {
            map["type"] = (map["type"] as String).replace("neoforge:", "forge:")
        }
        rel.startsWith("data/c/tags/") && t.loader == "forge" && !t.atLeast("26.1") -> return forgeTags(map)
        rel.startsWith("data/rptubes/recipe/") -> return recipe(map, t, aliased)
        rel.startsWith("data/rptubes/pe_custom_conversions/") -> {
            var r: Any? = map
            if (t.loader == "forge" && !t.atLeast("26.1")) r = forgeTags(r)
            // ProjectE for 1.20.1 and older wrote ingredients as {"#tag": amount} and outputs as plain strings.
            if (!t.atLeast("1.20.5")) r = legacyConversions(r as Map<String, Any?>)
            return r
        }
    }
    return map
}

@Suppress("UNCHECKED_CAST")
private fun legacyConversions(file: Map<String, Any?>): Map<String, Any?> {
    fun stack(x: Any?): String = (x as Map<String, Any?>).let { if ("tag" in it) "#" + it["tag"] else it["id"] as String }
    fun conversion(c: Any?): Map<String, Any?> {
        val source = c as Map<String, Any?>
        val out = LinkedHashMap<String, Any?>()
        source["count"]?.let { out["count"] = it }
        out["ingredients"] = (source["ingredients"] as List<Any?>).associate { stack(it) to ((it as Map<String, Any?>)["amount"] ?: 1) }
        out["output"] = stack(source["output"])
        source["propagateTags"]?.let { out["propagateTags"] = it }
        return out
    }
    val out = LinkedHashMap(file)
    (file["groups"] as? Map<String, Any?>)?.let { groups ->
        out["groups"] = groups.mapValuesTo(LinkedHashMap()) { (_, g) ->
            val group = LinkedHashMap(g as Map<String, Any?>)
            group["conversions"] = (group["conversions"] as List<Any?>).map { conversion(it) }
            group
        }
    }
    (file["values"] as? Map<String, Any?>)?.let { values ->
        val v = LinkedHashMap(values)
        (v["conversion"] as? List<Any?>)?.let { list -> v["conversion"] = list.map { conversion(it) } }
        out["values"] = v
    }
    return out
}

/**
 * Where two tag conventions coexist (Forge before 26.1 has forge: and c:, Fabric before 1.20.5
 * has c:ingots/tin and c:tin_ingots), recipes read a rptubes: tag that pulls in both. Pointing
 * forge: and c: at each other instead would loop, since Forge 1.21 already links c: to forge:.
 */
private fun needsTagAliases(t: RPTubesTarget): Boolean =
    (t.loader == "forge" && !t.atLeast("26.1")) || (t.loader == "fabric" && !t.atLeast("1.20.5"))

private fun tagAliases(tag: String, t: RPTubesTarget): List<String> {
    val (kind, material) = tag.split('/', limit = 2)
    return if (t.loader == "forge") listOf("forge:$tag", "c:$tag") else listOf("c:$tag", "c:${material}_$kind")
}

private fun aliasTags(x: Any?, modId: String, aliased: MutableSet<String>): Any? = when (x) {
    is Map<*, *> -> x.entries.associateTo(LinkedHashMap<String, Any?>()) { (k, v) -> k as String to aliasTags(v, modId, aliased) }
    is List<*> -> x.map { aliasTags(it, modId, aliased) }
    is String -> if (x.startsWith("#c:") && '/' in x) {
        aliased += x.removePrefix("#c:")
        "#$modId:" + x.removePrefix("#c:")
    } else x
    else -> x
}

/** Forge used its own forge: tag namespace until 26.1. */
private fun forgeTags(x: Any?): Any? = when (x) {
    is Map<*, *> -> x.entries.associateTo(LinkedHashMap<String, Any?>()) { (k, v) ->
        k as String to if (k == "tag" && v is String && v.startsWith("c:")) "forge:" + v.removePrefix("c:") else forgeTags(v)
    }
    is List<*> -> x.map { forgeTags(it) }
    is String -> if (x.startsWith("#c:")) "#forge:" + x.removePrefix("#c:") else x
    else -> x
}

/** Ingredients were objects before 1.21.2 turned them into bare ids and #tags. */
private fun ingredientObject(x: Any?): Any? = when (x) {
    is String -> if (x.startsWith("#")) mapOf("tag" to x.removePrefix("#")) else mapOf("item" to x)
    is List<*> -> x.map { ingredientObject(it) }
    else -> x
}

@Suppress("UNCHECKED_CAST")
private fun recipe(source: MutableMap<String, Any?>, t: RPTubesTarget, aliased: MutableSet<String>): Any? {
    var r: MutableMap<String, Any?> = source
    val conditions = r.remove("neoforge:conditions") as List<Map<String, Any?>>?
    if (conditions != null) {
        when {
            t.loader == "neoforge" -> r["neoforge:conditions"] = conditions
            t.loader == "forge" && t.atLeast("1.20.5") -> r["forge:condition"] =
                if (conditions.size == 1) forgeCondition(conditions[0])
                else mapOf("type" to "forge:and", "values" to conditions.map { forgeCondition(it) })
            t.loader == "forge" -> r["conditions"] = conditions.map { forgeCondition(it) }
            else -> r["fabric:load_conditions"] = conditions.map { fabricCondition(it) }
        }
    }
    if (needsTagAliases(t)) {
        r = aliasTags(r, t.modId, aliased) as MutableMap<String, Any?>
    }
    if (!t.atLeast("1.21.2")) {
        (r["key"] as? Map<String, Any?>)?.let { key -> r["key"] = key.mapValuesTo(LinkedHashMap()) { ingredientObject(it.value) } }
        if ("ingredient" in r) r["ingredient"] = ingredientObject(r["ingredient"])
        (r["ingredients"] as? List<Any?>)?.let { list ->
            r["ingredients"] = list.map { e ->
                if (e is Map<*, *> && "ingredient" in e) (e as Map<String, Any?>) + ("ingredient" to ingredientObject(e["ingredient"]))
                else ingredientObject(e)
            }
        }
    }
    if (!t.atLeast("1.20.5")) {
        val cooking = r["type"] in setOf("minecraft:smelting", "minecraft:blasting", "minecraft:smoking", "minecraft:campfire_cooking")
        val result = r["result"] as? Map<String, Any?>
        if (cooking && result != null && result.keys == setOf("id")) {
            r["result"] = result["id"]
        } else if (result != null && "id" in result) {
            r["result"] = result.entries.associateTo(LinkedHashMap<String, Any?>()) { (k, v) -> (if (k == "id") "item" else k) to v }
        }
        // Alloy ingredients carried their count beside the item or tag, not around it.
        (r["ingredients"] as? List<Any?>)?.let { list ->
            r["ingredients"] = list.map { e ->
                val inner = (e as? Map<String, Any?>)?.get("ingredient") as? Map<String, Any?>
                if (inner != null) LinkedHashMap(inner).apply { putAll(e.filterKeys { it != "ingredient" }) } else e
            }
        }
    }
    return r
}

private fun forgeCondition(c: Map<String, Any?>): Map<String, Any?> {
    val out = LinkedHashMap(c)
    out["type"] = (c["type"] as String).replace("neoforge:", "forge:")
    @Suppress("UNCHECKED_CAST")
    (c["value"] as? Map<String, Any?>)?.let { out["value"] = forgeCondition(it) }
    return out
}

private fun fabricCondition(c: Map<String, Any?>): Map<String, Any?> = when (c["type"]) {
    "neoforge:mod_loaded" -> mapOf("condition" to "fabric:all_mods_loaded", "values" to listOf((c["modid"] as String).replace('_', '-')))
    @Suppress("UNCHECKED_CAST")
    "neoforge:not" -> mapOf("condition" to "fabric:not", "value" to fabricCondition(c["value"] as Map<String, Any?>))
    else -> error("no Fabric form for recipe condition $c")
}

private fun packMeta(t: RPTubesTarget): Map<String, Any?> {
    val pack = LinkedHashMap<String, Any?>()
    pack["description"] = "${t.modId} resources"
    val format = t.prop("pack.format").toInt()
    if (t.atLeast("1.21.9")) {
        pack["max_format"] = format
        pack["min_format"] = listOf(format, 1)
    } else {
        pack["pack_format"] = format
        if (t.forgeLike) {
            pack["forge:resource_pack_format"] = format
            pack["forge:data_pack_format"] = t.prop("pack.data_format").toInt()
        }
    }
    return mapOf("pack" to pack)
}

private fun copyJson(x: Any?): Any? = when (x) {
    is Map<*, *> -> x.entries.associateTo(LinkedHashMap<String, Any?>()) { (k, v) -> k as String to copyJson(v) }
    is List<*> -> x.mapTo(ArrayList()) { copyJson(it) }
    else -> x
}

private fun writeJson(file: File, data: Any?) {
    file.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(data)) + "\n")
}
