package com.rptubes.registry;

import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.recipe.Ingredient;
import com.rptubes.RPTubes;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 1.7.10 has no JSON recipes at all, so alloy smelting and crafting recipes are
 * registered in code with ore dictionary ingredients.
 */
public final class ModRecipes {
    private static final List<AlloyRecipe> ALLOY_RECIPES = new ArrayList<AlloyRecipe>();

    public static List<AlloyRecipe> getAlloyRecipes() {
        return ALLOY_RECIPES;
    }

    public static AlloyRecipe findAlloyRecipe(IInventory inv) {
        for (AlloyRecipe recipe : ALLOY_RECIPES) {
            if (recipe.matches(inv)) {
                return recipe;
            }
        }
        return null;
    }

    /** Call during init, after ore dictionary entries from all mods are in place. */
    public static void register() {
        // Brass: 3 copper + 1 tin -> 4 brass (OG CraftLib alloy)
        if (!OreDictionary.getOres("ingotCopper").isEmpty()) {
            List<Ingredient> ingredients = new ArrayList<Ingredient>();
            ingredients.add(Ingredient.fromOre("ingotCopper"));
            ingredients.add(Ingredient.fromOre("ingotTin"));
            ALLOY_RECIPES.add(new AlloyRecipe(ingredients, new int[]{3, 1},
                    new ItemStack(ModItems.BRASS_INGOT.get(), 4), 200));
        }

        // Red alloy ingot: 4 redstone + 1 iron -> 1 red alloy (when ProjectRed provides it)
        List<ItemStack> redAlloy = OreDictionary.getOres("ingotRedAlloy");
        if (!redAlloy.isEmpty()) {
            ItemStack result = redAlloy.get(0).copy();
            result.stackSize = 1;
            List<Ingredient> ingredients = new ArrayList<Ingredient>();
            ingredients.add(Ingredient.fromStacks(new ItemStack(net.minecraft.init.Items.redstone)));
            ingredients.add(Ingredient.fromOre("ingotIron"));
            ALLOY_RECIPES.add(new AlloyRecipe(ingredients, new int[]{4, 1}, result, 200));
        }

        // Tin ore smelting (replaces the smelting/blasting JSONs)
        GameRegistry.addSmelting(ModBlocks.TIN_ORE.get(),
                new ItemStack(ModItems.TIN_INGOT.get()), 0.7F);
        GameRegistry.addSmelting(ModBlocks.COPPER_ORE.get(),
                new ItemStack(ModItems.COPPER_INGOT.get()), 0.7F);

        registerCrafting();
    }

    private static final int ANY = 0;
    private static final int PR = 1;
    private static final int NO_PR = 2;

    private static boolean projectRed;
    private static ItemStack electroSilicon;
    /** Results whose ProjectRed recipe could not be built, so their fallback is used instead. */
    private static final Set<String> PR_SKIPPED = new HashSet<String>();

    /**
     * Same recipes as the 1.12.2 JSONs. ProjectRed variants need ProjectRed 1.7.10 and every
     * one of their ingredients; otherwise the matching fallback recipe is registered.
     */
    private static void registerCrafting() {
        projectRed = Loader.isModLoaded("ProjRed|Core") && !OreDictionary.getOres("ingotRedAlloy").isEmpty();
        electroSilicon = projectRed ? findElectroSilicon() : null;

        shaped(ANY, "rptubes:accelerator", 1, new String[]{"BCB", "MRM", "BCB"}, 'B', "ore:ingotBrass", 'C', "ore:ingotCopper", 'M', "rptubes:mag_tube", 'R', "minecraft:redstone_block");
        shaped(ANY, "rptubes:act_mk2", 1, new String[]{"PWP", "WCW", "PcP"}, 'C', "rptubes:auto_crafting_table", 'P', "ore:plankWood", 'W', "rptubes:pneumatic_tube", 'c', "minecraft:chest");
        shaped(ANY, "rptubes:alloy_furnace", 1, new String[]{"BBB", "B B", "BBB"}, 'B', "minecraft:brick_block");
        shaped(ANY, "rptubes:auto_crafting_table", 1, new String[]{" b ", "bwb", " b "}, 'b', "rptubes:brass_ingot", 'w', "minecraft:crafting_table");
        shaped(PR, "rptubes:battery", 1, new String[]{"NCN", "NTN", "NCN"}, 'C', "ore:ingotCopper", 'N', "ore:dustElectrotine", 'T', "ore:ingotTin");
        shaped(PR, "rptubes:battery_box", 1, new String[]{"BWB", "BIB", "IAI"}, 'A', "ore:ingotElectrotineAlloy", 'B', "rptubes:battery", 'I', "ore:ingotIron", 'W', "ore:plankWood");
        shaped(NO_PR, "rptubes:battery_box", 1, new String[]{"BWB", "BIB", "IAI"}, 'A', "minecraft:lapis_block", 'B', "rptubes:battery", 'I', "ore:ingotIron", 'W', "ore:plankWood");
        shaped(NO_PR, "rptubes:battery", 1, new String[]{"NCN", "NTN", "NCN"}, 'C', "ore:ingotCopper", 'N', "minecraft:dye@4", 'T', "ore:ingotTin");
        shaped(ANY, "rptubes:block_breaker", 1, new String[]{"SPS", "SIS", "SRS"}, 'I', "minecraft:iron_pickaxe", 'P', "minecraft:piston", 'R', "minecraft:redstone", 'S', "minecraft:stone@0");
        shaped(ANY, "rptubes:buffer", 1, new String[]{"IWI", "W W", "IWI"}, 'I', "minecraft:iron_bars", 'W', "ore:plankWood");
        shaped(ANY, "rptubes:deployer", 1, new String[]{"SCS", "SPS", "SRS"}, 'C', "minecraft:chest", 'P', "minecraft:piston", 'R', "minecraft:redstone", 'S', "minecraft:stone@0");
        shaped(ANY, "rptubes:ejector", 1, new String[]{"WBW", "WTW", "SRS"}, 'B', "minecraft:chest", 'R', "minecraft:redstone", 'S', "minecraft:stone@0", 'T', "rptubes:transposer", 'W', "ore:plankWood");
        shaped(PR, "rptubes:filter", 1, new String[]{"SSS", "GPG", "SRS"}, 'G', "minecraft:gold_ingot", 'P', "minecraft:piston", 'R', "ore:ingotRedAlloy", 'S', "minecraft:stone@0");
        shaped(NO_PR, "rptubes:filter", 1, new String[]{"SSS", "GPG", "SRS"}, 'G', "minecraft:gold_ingot", 'P', "minecraft:piston", 'R', "minecraft:redstone", 'S', "minecraft:stone@0");
        shaped(ANY, "rptubes:igniter", 1, new String[]{"NFN", "SDS", "SRS"}, 'D', "rptubes:deployer", 'F', "minecraft:flint_and_steel", 'N', "minecraft:netherrack", 'R', "minecraft:redstone", 'S', "minecraft:stone@0");
        shaped(PR, "rptubes:item_detector", 1, new String[]{"BTB", "RPR", "WWW"}, 'B', "ore:ingotBrass", 'P', "minecraft:stone_pressure_plate", 'R', "ore:ingotRedAlloy", 'T', "rptubes:pneumatic_tube", 'W', "ore:plankWood");
        shaped(NO_PR, "rptubes:item_detector", 1, new String[]{"BTB", "RPR", "WWW"}, 'B', "ore:ingotBrass", 'P', "minecraft:stone_pressure_plate", 'R', "minecraft:redstone", 'T', "rptubes:pneumatic_tube", 'W', "ore:plankWood");
        shaped(ANY, "rptubes:mag_tube", 8, new String[]{"TTT", "TCT", "TTT"}, 'C', "ore:ingotCopper", 'T', "rptubes:pneumatic_tube");
        shaped(ANY, "rptubes:pneumatic_tube", 8, new String[]{"BGB"}, 'B', "ore:ingotBrass", 'G', "minecraft:glass");
        shapeless(ANY, "rptubes:redstone_tube", 1, "minecraft:redstone", "rptubes:pneumatic_tube");
        shaped(PR, "rptubes:regulator", 1, new String[]{"BDB", "WRW", "BPB"}, 'B', "ore:ingotBrass", 'D', "rptubes:item_detector", 'P', "rptubes:buffer", 'R', "ore:ingotRedAlloy", 'W', "ore:plankWood");
        shaped(NO_PR, "rptubes:regulator", 1, new String[]{"BDB", "WRW", "BPB"}, 'B', "ore:ingotBrass", 'D', "rptubes:item_detector", 'P', "rptubes:buffer", 'R', "minecraft:redstone", 'W', "ore:plankWood");
        shaped(PR, "rptubes:relay", 1, new String[]{"WBW", "WTW", "SRS"}, 'B', "minecraft:chest", 'R', "ore:ingotRedAlloy", 'S', "minecraft:stone@0", 'T', "rptubes:transposer", 'W', "ore:plankWood");
        shaped(NO_PR, "rptubes:relay", 1, new String[]{"WBW", "WTW", "SRS"}, 'B', "minecraft:chest", 'R', "minecraft:redstone", 'S', "minecraft:stone@0", 'T', "rptubes:transposer", 'W', "ore:plankWood");
        shapeless(ANY, "rptubes:restriction_tube", 1, "ore:ingotIron", "rptubes:pneumatic_tube");
        shaped(PR, "rptubes:retriever", 1, new String[]{"BEB", "IFI", "IRI"}, 'B', "ore:ingotBrass", 'E', "minecraft:ender_pearl", 'F', "rptubes:filter", 'I', "ore:ingotIron", 'R', "ore:ingotRedAlloy");
        shaped(NO_PR, "rptubes:retriever", 1, new String[]{"BEB", "IFI", "IRI"}, 'B', "ore:ingotBrass", 'E', "minecraft:ender_pearl", 'F', "rptubes:filter", 'I', "ore:ingotIron", 'R', "minecraft:redstone");
        shaped(ANY, "rptubes:retrievulator", 1, new String[]{"BCB", "RHR", "BBB"}, 'B', "ore:ingotBrass", 'C', "minecraft:comparator", 'H', "minecraft:hopper", 'R', "minecraft:redstone");
        shaped(ANY, "rptubes:screwdriver", 1, new String[]{"I ", " W"}, 'I', "minecraft:iron_ingot", 'W', "minecraft:stick");
        shaped(PR, "rptubes:solar_panel", 1, new String[]{"WWW", "WBW", "WWW"}, 'B', "ore:ingotElectrotineAlloy", 'W', "pr:electro_silicon");
        shaped(NO_PR, "rptubes:solar_panel", 1, new String[]{"GGG", "LDL", "CCC"}, 'C', "ore:ingotCopper", 'D', "minecraft:daylight_detector", 'G', "minecraft:glass", 'L', "minecraft:dye@4");
        shaped(PR, "rptubes:sorting_machine", 1, new String[]{"IFI", "IRI", "III"}, 'F', "rptubes:filter", 'I', "ore:ingotIron", 'R', "ore:ingotRedAlloy");
        shaped(NO_PR, "rptubes:sorting_machine", 1, new String[]{"IFI", "IRI", "III"}, 'F', "rptubes:filter", 'I', "ore:ingotIron", 'R', "minecraft:redstone");
        shaped(PR, "rptubes:thermopile", 1, new String[]{"CIC", "WBW", "CIC"}, 'B', "ore:ingotElectrotineAlloy", 'C', "ore:ingotCopper", 'I', "ore:ingotIron", 'W', "pr:electro_silicon");
        shaped(NO_PR, "rptubes:thermopile", 1, new String[]{"CIC", "WBW", "CIC"}, 'B', "minecraft:redstone", 'C', "ore:ingotCopper", 'I', "ore:ingotIron", 'W', "minecraft:dye@4");
        shaped(ANY, "rptubes:transposer", 1, new String[]{"SSS", "WPW", "SRS"}, 'P', "minecraft:piston", 'R', "minecraft:redstone", 'S', "minecraft:stone@0", 'W', "ore:plankWood");
    }

    /**
     * ProjectRed 1.7.10 keeps its ingots and dusts as metadata of one part item, numbered by
     * declaration order. Electro silicon has no ore name, so it is only used when the
     * electrotine ingot and dust sit where that numbering puts them.
     */
    private static ItemStack findElectroSilicon() {
        for (ItemStack ingot : OreDictionary.getOres("ingotElectrotineAlloy")) {
            if (ingot == null || ingot.getItem() == null || ingot.getItemDamage() != 55) {
                continue;
            }
            for (ItemStack dust : OreDictionary.getOres("dustElectrotine")) {
                if (dust != null && dust.getItem() == ingot.getItem() && dust.getItemDamage() == 56) {
                    return new ItemStack(ingot.getItem(), 1, 59);
                }
            }
        }
        return null;
    }

    private static boolean wanted(int cond, String result) {
        if (cond == PR) {
            return projectRed;
        }
        if (cond == NO_PR) {
            return !projectRed || PR_SKIPPED.contains(result);
        }
        return true;
    }

    /** Resolves "ore:name", "pr:electro_silicon", "modid:item" or "modid:item@meta"; null when missing. */
    private static Object ingredient(String spec, boolean strictOre) {
        if (spec.startsWith("ore:")) {
            String ore = spec.substring(4);
            return strictOre && OreDictionary.getOres(ore).isEmpty() ? null : ore;
        }
        if (spec.equals("pr:electro_silicon")) {
            return electroSilicon == null ? null : electroSilicon.copy();
        }
        int at = spec.indexOf('@');
        String name = at < 0 ? spec : spec.substring(0, at);
        int meta = at < 0 ? 0 : Integer.parseInt(spec.substring(at + 1));
        Object obj = Item.itemRegistry.getObject(name);
        if (obj instanceof Item) {
            return new ItemStack((Item) obj, 1, meta);
        }
        Block block = (Block) Block.blockRegistry.getObject(name);
        return block == null ? null : new ItemStack(block, 1, meta);
    }

    private static ItemStack result(String name, int count) {
        Object stack = ingredient(name, false);
        if (!(stack instanceof ItemStack)) {
            return null;
        }
        ((ItemStack) stack).stackSize = count;
        return (ItemStack) stack;
    }

    private static void shaped(int cond, String name, int count, String[] pattern, Object... keys) {
        if (!wanted(cond, name)) {
            return;
        }
        List<Object> args = new ArrayList<Object>();
        for (String row : pattern) {
            args.add(row);
        }
        for (int i = 0; i < keys.length; i += 2) {
            Object resolved = ingredient((String) keys[i + 1], cond == PR);
            if (resolved == null) {
                skip(cond, name, (String) keys[i + 1]);
                return;
            }
            args.add(keys[i]);
            args.add(resolved);
        }
        ItemStack out = result(name, count);
        if (out != null) {
            GameRegistry.addRecipe(new ShapedOreRecipe(out, args.toArray()));
        }
    }

    private static void shapeless(int cond, String name, int count, String... specs) {
        if (!wanted(cond, name)) {
            return;
        }
        List<Object> args = new ArrayList<Object>();
        for (String spec : specs) {
            Object resolved = ingredient(spec, cond == PR);
            if (resolved == null) {
                skip(cond, name, spec);
                return;
            }
            args.add(resolved);
        }
        ItemStack out = result(name, count);
        if (out != null) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(out, args.toArray()));
        }
    }

    private static void skip(int cond, String name, String spec) {
        if (cond == PR) {
            PR_SKIPPED.add(name);
        }
        RPTubes.LOGGER.info("Recipe for {} not added: {} is unavailable", name, spec);
    }

    private ModRecipes() {
    }
}
