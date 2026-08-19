package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.DeployerMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import com.rptubes.util.inv.IItemHandler;
import com.rptubes.util.inv.SidedInvWrapper;

/** Port of TileDeploy — pulse places/uses items toward FACING. */
public class DeployerBlockEntity extends CompatTileEntity implements ISidedInventory, InvDefaults, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    private ItemStack[] items = new ItemStack[9];
    private boolean powered;
    private boolean active;
    private final IItemHandler handler = new SidedInvWrapper(this, ForgeDirection.DOWN);

    public DeployerBlockEntity() {
        super();
    }

    public void onNeighborChange() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        boolean signal = this.getWorld().isBlockIndirectlyGettingPowered(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
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
        ForgeDirection facing = this.getFacing();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.items[i];
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                continue;
            }
            if (this.useStack(stack, facing)) {
                if (com.rptubes.util.Stacks.isEmpty(stack)) {
                    this.items[i] = null;
                }
                this.markDirty();
                return;
            }
        }
    }

    private static final com.mojang.authlib.GameProfile DEPLOYER_PROFILE =
            new com.mojang.authlib.GameProfile(
                    java.util.UUID.fromString("f9524f3e-8a5e-4dc1-a03e-6f0a3f8f9f21"), "[RPTubes Deployer]");

    private boolean useStack(ItemStack stack, ForgeDirection facing) {
        if (!(this.getWorld() instanceof WorldServer)) {
            return false;
        }
        WorldServer ws = (WorldServer) this.getWorld();
        FakePlayer fake = FakePlayerFactory.get(ws, DEPLOYER_PROFILE);
        BlockPos front = this.getPos().offset(facing);
        float yaw = facing == ForgeDirection.SOUTH ? 0.0F
                : facing == ForgeDirection.WEST ? 90.0F
                : facing == ForgeDirection.NORTH ? 180.0F
                : facing == ForgeDirection.EAST ? -90.0F : 0.0F;
        fake.setLocationAndAngles(
                this.getPos().getX() + 0.5 + facing.offsetX * 0.5,
                this.getPos().getY() + 0.5 - fake.getDefaultEyeHeight() + facing.offsetY * 0.5,
                this.getPos().getZ() + 0.5 + facing.offsetZ * 0.5,
                yaw,
                facing == ForgeDirection.DOWN ? 90.0F : facing == ForgeDirection.UP ? -90.0F : 0.0F);
        fake.inventory.currentItem = 0;
        fake.inventory.setInventorySlotContents(0, stack);
        // tryPlaceItemIntoWorld handles both block placement (into an empty front
        // space) and item use against the block in front (bonemeal, flint&steel...)
        boolean used = stack.tryPlaceItemIntoWorld(fake, ws,
                front.getX(), front.getY(), front.getZ(),
                facing.getOpposite().ordinal(), 0.5F, 0.5F, 0.5F);
        fake.inventory.setInventorySlotContents(0, null);
        return used;
    }

    public ForgeDirection getFacing() {
        return MachineBlock.getFacing(this.getWorld().getBlockMetadata(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.getWorld() != null) {
            MachineBlock.setActive(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, value);
        }
        this.markDirty();
    }

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.deployer");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new DeployerMenu(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

        public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.items[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return com.rptubes.util.Stacks.getAndSplit(this.items, slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return com.rptubes.util.Stacks.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
        this.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getWorld() != null
                && this.getWorld().getTileEntity(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) == this
                && player.getDistanceSq(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5,
                this.getPos().getZ() + 0.5) <= 64.0;
    }

        public void clear() {
        java.util.Arrays.fill(this.items, null);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == this.getFacing().ordinal()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return side != this.getFacing().ordinal();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return side != this.getFacing().ordinal();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.items);
        tag.setBoolean("Powered", this.powered);
        tag.setBoolean("Active", this.active);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = new ItemStack[9];
        com.rptubes.util.Stacks.loadAllItems(tag, this.items);
        this.powered = tag.getBoolean("Powered");
        this.active = tag.getBoolean("Active");
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        return this.handler;
    }

    public void dropContents() {
        if (this.getWorld() != null) {
            com.rptubes.util.Stacks.dropInventory(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, this);
        }
    }
}
