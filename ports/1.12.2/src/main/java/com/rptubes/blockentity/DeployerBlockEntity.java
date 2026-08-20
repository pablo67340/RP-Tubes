package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.DeployerMenu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/** Port of TileDeploy — pulse places/uses items toward FACING. */
public class DeployerBlockEntity extends TileEntity implements ISidedInventory, InvDefaults {
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private boolean powered;
    private boolean active;
    private final IItemHandler handler = new SidedInvWrapper(this, EnumFacing.DOWN);

    public DeployerBlockEntity() {
        super();
    }

    public void onNeighborChange() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        boolean signal = this.world.isBlockPowered(this.pos);
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
        this.markDirty();
    }

    private void tryDeploy() {
        EnumFacing facing = this.getFacing();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (this.useStack(stack, facing)) {
                if (stack.isEmpty()) {
                    this.items.set(i, ItemStack.EMPTY);
                }
                this.markDirty();
                return;
            }
        }
    }

    private static final com.mojang.authlib.GameProfile DEPLOYER_PROFILE =
            new com.mojang.authlib.GameProfile(
                    java.util.UUID.fromString("f9524f3e-8a5e-4dc1-a03e-6f0a3f8f9f21"), "[RPTubes Deployer]");

    private boolean useStack(ItemStack stack, EnumFacing facing) {
        if (!(this.world instanceof WorldServer)) {
            return false;
        }
        WorldServer ws = (WorldServer) this.world;
        FakePlayer fake = FakePlayerFactory.get(ws, DEPLOYER_PROFILE);
        BlockPos front = this.pos.offset(facing);
        fake.setLocationAndAngles(
                this.pos.getX() + 0.5 + facing.getXOffset() * 0.5,
                this.pos.getY() + 0.5 - fake.getDefaultEyeHeight() + facing.getYOffset() * 0.5,
                this.pos.getZ() + 0.5 + facing.getZOffset() * 0.5,
                facing.getAxis().isHorizontal() ? facing.getHorizontalAngle() : 0.0F,
                facing == EnumFacing.DOWN ? 90.0F : facing == EnumFacing.UP ? -90.0F : 0.0F);
        fake.setHeldItem(EnumHand.MAIN_HAND, stack);
        // onItemUse handles both block placement (into an empty front space)
        // and item use against the block in front (bonemeal, flint&steel...)
        EnumActionResult result = stack.onItemUse(fake, ws, front, EnumHand.MAIN_HAND,
                facing.getOpposite(), 0.5F, 0.5F, 0.5F);
        fake.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        return result == EnumActionResult.SUCCESS;
    }

    public EnumFacing getFacing() {
        return this.world.getBlockState(this.pos).getValue(MachineBlock.FACING);
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            if (state.getValue(MachineBlock.ACTIVE) != value) {
                this.world.setBlockState(this.pos, state.withProperty(MachineBlock.ACTIVE, value), 2);
            }
        }
        this.markDirty();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.deployer");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new DeployerMenu(inv, this);
    }

    @Override
    public int getSizeInventory() {
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
    public ItemStack getStackInSlot(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return ItemStackHelper.getAndSplit(this.items, slot, amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return ItemStackHelper.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null
                && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5,
                this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == this.getFacing()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != this.getFacing();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != this.getFacing();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        ItemStackHelper.saveAllItems(tag, this.items);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag, this.items);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.handler;
        }
        return super.getCapability(cap, side);
    }

    public void dropContents() {
        if (this.world != null) {
            net.minecraft.inventory.InventoryHelper.dropInventoryItems(this.world, this.pos, this);
        }
    }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                                 net.minecraft.block.state.IBlockState oldState,
                                 net.minecraft.block.state.IBlockState newState) {
        // Default Forge behavior recreates modded tile entities on ANY state
        // change (e.g. lit/active toggles), silently wiping their contents.
        return oldState.getBlock() != newState.getBlock();
    }
}
