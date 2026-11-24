package com.rptubes.item;

import com.rptubes.power.ItemPower;
import com.rptubes.power.PowerBuffer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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

    public BatteryItem() {
        this.setMaxStackSize(1);
    }

    public static ItemStack charged(Item item) {
        ItemStack stack = new ItemStack(item);
        setCharge(stack, CAPACITY);
        return stack;
    }

    public static long getCharge(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getLong(KEY);
    }

    public static void setCharge(ItemStack stack, long charge) {
        charge = Math.max(0, Math.min(charge, CAPACITY));
        NBTTagCompound tag = stack.getTagCompound();
        if (charge == 0) {
            if (tag != null) {
                tag.removeTag(KEY);
                if (tag.isEmpty()) {
                    stack.setTagCompound(null);
                }
            }
        } else {
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }
            tag.setLong(KEY, charge);
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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            chargeHotbar(stack, player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static void chargeHotbar(ItemStack battery, EntityPlayer player) {
        long charge = getCharge(battery);
        if (charge <= 0) {
            return;
        }
        InventoryPlayer inventory = player.inventory;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack target = inventory.getStackInSlot(slot);
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
