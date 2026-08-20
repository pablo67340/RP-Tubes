package com.rptubes.blockentity;

import com.rptubes.inventory.SidedInvWrapper;

import com.rptubes.inventory.ItemHandlerProvider;

import com.rptubes.inventory.IItemHandler;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.DeployerMenu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Port of TileDeploy — pulse places/uses items toward FACING. */
public class DeployerBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, ItemHandlerProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private boolean powered;
    private boolean active;
    private final IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH);

    public DeployerBlockEntity() {
        super(ModBlockEntities.DEPLOYER);
    }

    public void onNeighborChange() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        boolean signal = this.level.hasNeighborSignal(this.worldPosition);
        if (signal) {
            if (this.powered) {
                return;
            }
            this.powered = true;
            if (!this.active) {
                this.setActive(true);
                this.tryDeploy();
            }
        } else {
            this.powered = false;
            if (this.active) {
                this.setActive(false);
            }
        }
        this.setChanged();
    }

    private void tryDeploy() {
        Direction facing = this.getFacing();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (this.useStack(stack, facing)) {
                if (stack.isEmpty()) {
                    this.items.set(i, ItemStack.EMPTY);
                }
                this.setChanged();
                return;
            }
        }
    }

    private boolean useStack(ItemStack stack, Direction facing) {
        BlockPos front = this.worldPosition.relative(facing);
        BlockState frontState = this.level.getBlockState(front);

        if (stack.getItem() instanceof BlockItem) {
            BlockHitResult hit = new BlockHitResult(
                    Vec3.atCenterOf(this.worldPosition), facing, this.worldPosition, false);
            BlockPlaceContext ctx = new MachinePlaceContext(this.level, InteractionHand.MAIN_HAND, stack, hit);
            InteractionResult result = ((BlockItem) stack.getItem()).place(ctx);
            return result.consumesAction();
        }

        BlockPos clickPos = frontState.isAir() ? this.worldPosition : front;
        Direction clickFace = frontState.isAir() ? facing : facing.getOpposite();
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(clickPos), clickFace, clickPos, false);
        UseOnContext ctx = new MachineUseContext(this.level, InteractionHand.MAIN_HAND, stack, hit);
        InteractionResult result = stack.getItem().useOn(ctx);
        return result.consumesAction();
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(MachineBlock.FACING);
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.level != null) {
            BlockState state = this.getBlockState();
            if (state.getValue(MachineBlock.ACTIVE) != value) {
                this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), 2);
            }
        }
        this.setChanged();
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.rptubes.deployer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new DeployerMenu(id, inv, this);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == this.getFacing()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction != this.getFacing();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction != this.getFacing();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }




    public IItemHandler getItemHandler(Direction side) {
        if (side == null) {
            return this.handlers[0];
        }
        return this.handlers[Math.abs(side.ordinal() % this.handlers.length)];
    }

    /** 1.16.5 official mappings keep these constructors protected. */
    private static final class MachineUseContext extends UseOnContext {
        MachineUseContext(Level level, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
            super(level, null, hand, stack, hit);
        }
    }

    private static final class MachinePlaceContext extends BlockPlaceContext {
        MachinePlaceContext(Level level, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
            super(level, null, hand, stack, hit);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null) {
            net.minecraft.world.Containers.dropContents(this.level, this.worldPosition, this);
        }
        super.setRemoved();
    }
}
