package com.rptubes.item;

import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBuffer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

/**
 * Port of ItemBattery: 1500 RP2 units (150,000 FE). Right-click moves up to 25 units into the
 * first chargeable item on the hotbar. Charge lives in NBT rather than damage, so repairs and
 * Mending cannot refill it.
 */
public class BatteryItem extends Item {
    public static final long CAPACITY = 1500L * PowerBuffer.FE_PER_UNIT;
    public static final long TRANSFER = 25L * PowerBuffer.FE_PER_UNIT;
    private static final String KEY = "Energy";

    public BatteryItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static ItemStack charged(Item item) {
        ItemStack stack = new ItemStack(item);
        setCharge(stack, CAPACITY);
        return stack;
    }

    public static long getCharge(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : tag.getLong(KEY);
    }

    public static void setCharge(ItemStack stack, long charge) {
        charge = Math.max(0, Math.min(charge, CAPACITY));
        if (charge == 0) {
            stack.removeTagKey(KEY);
        } else {
            stack.getOrCreateTag().putLong(KEY, charge);
        }
    }

    public static long receive(ItemStack stack, long amount) {
        long charge = getCharge(stack);
        long added = Math.min(Math.min(amount, TRANSFER), CAPACITY - charge);
        if (added <= 0) {
            return 0;
        }
        setCharge(stack, charge + added);
        return added;
    }

    public static long extract(ItemStack stack, long amount) {
        long charge = getCharge(stack);
        long taken = Math.min(Math.min(amount, TRANSFER), charge);
        if (taken <= 0) {
            return 0;
        }
        setCharge(stack, charge - taken);
        return taken;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
            items.add(charged(this));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TextComponent(getCharge(stack) + " / " + CAPACITY + " FE").withStyle(ChatFormatting.BLUE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            chargeHotbar(stack, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static void chargeHotbar(ItemStack battery, Player player) {
        long charge = getCharge(battery);
        if (charge <= 0) {
            return;
        }
        Inventory inventory = player.inventory;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack target = inventory.getItem(slot);
            if (target.isEmpty() || target == battery || target.getItem() instanceof BatteryItem) {
                continue;
            }
            long moved = ItemPower.chargePlayer(player, slot, Math.min(TRANSFER, charge));
            if (moved > 0) {
                setCharge(battery, charge - moved);
                return;
            }
        }
    }
}
