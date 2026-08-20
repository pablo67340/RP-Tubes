package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.DeployerMenu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerProvider;
import com.rptubes.inventory.SidedInvWrapper;

/** Port of TileDeploy — pulse places/uses items toward FACING. */
public class DeployerBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, ItemHandlerProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private boolean powered;
    private boolean active;
    private final IItemHandler handlerDown = new SidedInvWrapper(this, Direction.DOWN);

    public IItemHandler getItemHandler(Direction side) {
        return this.handlerDown;
    }

    public DeployerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DEPLOYER, pos, state);
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
            BlockPlaceContext ctx = new BlockPlaceContext(this.level, null, InteractionHand.MAIN_HAND, stack, hit);
            InteractionResult result = ((BlockItem) stack.getItem()).place(ctx);
            return result.consumesAction();
        }

        BlockPos clickPos = frontState.isAir() ? this.worldPosition : front;
        Direction clickFace = frontState.isAir() ? facing : facing.getOpposite();
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(clickPos), clickFace, clickPos, false);
        UseOnContext ctx = new UseOnContext(this.level, null, InteractionHand.MAIN_HAND, stack, hit);
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
                this.level.setBlock(this.worldPosition, state.setValue(MachineBlock.ACTIVE, value), Block.UPDATE_CLIENTS);
            }
        }
        this.setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.deployer");
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
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }




    @Override
    public void setRemoved() {
        if (this.level != null) {
            net.minecraft.world.Containers.dropContents(this.level, this.worldPosition, this);
        }
        super.setRemoved();
    }
}
