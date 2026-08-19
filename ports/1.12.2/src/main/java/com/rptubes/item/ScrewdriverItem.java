package com.rptubes.item;

import com.rptubes.RPTubes;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

/**
 * Port of eloraam.base.ItemScrewdriver — limited uses, rotates blocks on right click.
 * OG cycled dispenser-style blocks through all 6 sides and machines via IRotatable.
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
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);
        IBlockState rotated = rotate(state);
        if (rotated == null || rotated == state) {
            return EnumActionResult.PASS;
        }
        if (!world.isRemote) {
            world.setBlockState(pos, rotated, 3);
            player.getHeldItem(hand).damageItem(1, player);
        }
        return EnumActionResult.SUCCESS;
    }

    private static IBlockState rotate(IBlockState state) {
        // Only rotate this mod's own blocks, not chests/logs/other rotatables
        ResourceLocation id = state.getBlock().getRegistryName();
        if (id == null || !id.getNamespace().equals(RPTubes.MOD_ID)) {
            return null;
        }
        for (IProperty<?> prop : state.getPropertyKeys()) {
            if ("facing".equals(prop.getName()) && prop instanceof PropertyDirection) {
                PropertyDirection pd = (PropertyDirection) prop;
                EnumFacing cur = state.getValue(pd);
                Collection<EnumFacing> allowed = pd.getAllowedValues();
                // OG order for 6-way blocks: md 0..5 (D,U,N,S,W,E) matching index order
                EnumFacing next = cur;
                for (int i = 0; i < 6; i++) {
                    next = EnumFacing.byIndex((next.getIndex() + 1) % 6);
                    if (allowed.contains(next)) {
                        return state.withProperty(pd, next);
                    }
                }
            }
        }
        return null;
    }
}
