package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.DeployerMenu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/** Port of TileDeploy — pulse places/uses items toward FACING. */
public class DeployerBlockEntity extends TileEntity implements ISidedInventory, INamedContainerProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private boolean powered;
    private boolean active;
    private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH);

    public DeployerBlockEntity() {
        super(ModBlockEntities.DEPLOYER.get());
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
            BlockRayTraceResult hit = new BlockRayTraceResult(
                    Vector3d.atCenterOf(this.worldPosition), facing, this.worldPosition, false);
            BlockItemUseContext ctx = new BlockItemUseContext(this.level, null, Hand.MAIN_HAND, stack, hit);
            ActionResultType result = ((BlockItem) stack.getItem()).place(ctx);
            return result.consumesAction();
        }

        BlockPos clickPos = frontState.isAir() ? this.worldPosition : front;
        Direction clickFace = frontState.isAir() ? facing : facing.getOpposite();
        BlockRayTraceResult hit = new BlockRayTraceResult(Vector3d.atCenterOf(clickPos), clickFace, clickPos, false);
        ItemUseContext ctx = new ItemUseContext(this.level, null, Hand.MAIN_HAND, stack, hit);
        ActionResultType result = stack.getItem().useOn(ctx);
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
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.rptubes.deployer");
    }

    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
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
        return ItemStackHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStackHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
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
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        tag.putBoolean("Powered", this.powered);
        tag.putBoolean("Active", this.active);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null) {
            return handlers[0].cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<? extends IItemHandler> h : handlers) {
            h.invalidate();
        }
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH);
    }

    @Override
    public void setRemoved() {
        if (this.level != null) {
            net.minecraft.inventory.InventoryHelper.dropContents(this.level, this.worldPosition, this);
        }
        super.setRemoved();
    }
}
