package com.rptubes.item;

import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

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
        CompoundNBT tag = stack.getTag();
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
            items.add(charged(this));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - (double) getCharge(stack) / CAPACITY;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0x4090FF;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            chargeHotbar(stack, player);
        }
        return ActionResult.sidedSuccess(stack, level.isClientSide);
    }

    private static void chargeHotbar(ItemStack battery, PlayerEntity player) {
        long charge = getCharge(battery);
        if (charge <= 0) {
            return;
        }
        PlayerInventory inventory = player.inventory;
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
