package com.rptubes.item;

import com.rptubes.block.AlloyFurnaceBlock;
import com.rptubes.block.MachineBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Port of eloraam.base.ItemScrewdriver — limited uses, rotates blocks on right click.
 * OG cycled dispenser-style blocks through all 6 sides and machines via IRotatable.
 * On 1.7.10 the facing lives in metadata, so rotation is a meta cycle.
 */
public class ScrewdriverItem extends Item {
    public ScrewdriverItem() {
        this.setMaxDamage(128);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    /**
     * Forge pre-block hook (same one the OG used) — runs before onBlockActivated,
     * so rotation wins over the machine GUI opening.
     */
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
                                  int x, int y, int z, int side,
                                  float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        // Only rotate this mod's own facing blocks, not chests/logs/other rotatables
        boolean machine = block instanceof MachineBlock;
        boolean furnace = block instanceof AlloyFurnaceBlock;
        if (!machine && !furnace) {
            return false;
        }
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if (machine) {
                // OG order for 6-way blocks: md 0..5 (D,U,N,S,W,E)
                int next = ((meta & 7) + 1) % 6;
                world.setBlockMetadataWithNotify(x, y, z, (meta & 8) | next, 3);
            } else {
                int next = ((meta & 3) + 1) & 3;
                world.setBlockMetadataWithNotify(x, y, z, (meta & 4) | next, 3);
            }
            stack.damageItem(1, player);
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, 0.6F);
        }
        return true;
    }
}
