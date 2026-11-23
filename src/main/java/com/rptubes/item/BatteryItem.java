package com.rptubes.item;

import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBuffer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.11 {
import net.minecraft.world.InteractionResultHolder;
//?}
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//? if >=1.20.6 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

/**
 * Port of ItemBattery: 1500 RP2 units (150,000 FE). Right-click moves up to 25 units into the
 * first chargeable item on the hotbar. Charge lives in custom data rather than damage, so
 * repairs and Mending cannot refill it.
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
        //? if <1.20.6 {
        /*CompoundTag tag = stack.getTag();
        return tag == null ? 0 : tag.getLong(KEY);
        *///?} else if <1.21.11 {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? 0 : data.copyTag().getLong(KEY);
        //?} else {
        /*CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? 0 : data.copyTag().getLongOr(KEY, 0L);
        *///?}
    }

    public static void setCharge(ItemStack stack, long charge) {
        charge = Math.max(0, Math.min(charge, CAPACITY));
        //? if <1.20.6 {
        /*if (charge == 0) {
            stack.removeTagKey(KEY);
        } else {
            stack.getOrCreateTag().putLong(KEY, charge);
        }
        *///?} else {
        CustomData existing = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = existing.copyTag();
        if (charge == 0) {
            tag.remove(KEY);
        } else {
            tag.putLong(KEY, charge);
        }
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        //?}
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
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getCharge(stack) / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x4090FF;
    }

    @Override
    //? if <1.21.11 {
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            chargeHotbar(stack, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    //?} else {
    /*public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            chargeHotbar(stack, player);
        }
        return InteractionResult.SUCCESS;
    *///?}
    }

    private static void chargeHotbar(ItemStack battery, Player player) {
        long charge = getCharge(battery);
        if (charge <= 0) {
            return;
        }
        Inventory inventory = player.getInventory();
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
