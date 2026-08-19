package com.rptubes.blockentity;

import com.rptubes.block.AlloyFurnaceBlock;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.recipe.AlloyRecipe;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * Port of eloraam.base.TileAlloyFurnace — 3x3 alloy grid, fuel, output.
 */
public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_COUNT = 9;
    public static final int SLOT_FUEL = 9;
    public static final int SLOT_OUTPUT = 10;
    public static final int SIZE = 11;

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int burnTime;
    private int burnTimeTotal;
    private int cookTime;
    private int cookTimeTotal = 200;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnTimeTotal;
                case 2 -> cookTime;
                case 3 -> cookTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnTimeTotal = value;
                case 2 -> cookTime = value;
                case 3 -> cookTimeTotal = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    public AlloyFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALLOY_FURNACE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlloyFurnaceBlockEntity furnace) {
        boolean wasLit = furnace.isLit();
        boolean changed = false;

        if (furnace.burnTime > 0) {
            --furnace.burnTime;
        }

        AlloyRecipe recipe = furnace.findRecipe(level);
        boolean canSmelt = recipe != null && furnace.canOutput(recipe);

        if (furnace.burnTime == 0 && canSmelt) {
            ItemStack fuel = furnace.items.get(SLOT_FUEL);
            int burn = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
            if (burn > 0) {
                furnace.burnTime = furnace.burnTimeTotal = burn;
                changed = true;
                if (fuel.hasCraftingRemainingItem()) {
                    furnace.items.set(SLOT_FUEL, fuel.getCraftingRemainingItem());
                } else {
                    fuel.shrink(1);
                    if (fuel.isEmpty()) {
                        furnace.items.set(SLOT_FUEL, ItemStack.EMPTY);
                    }
                }
            }
        }

        if (furnace.isLit() && canSmelt) {
            furnace.cookTimeTotal = recipe.getCookTime();
            ++furnace.cookTime;
            if (furnace.cookTime >= furnace.cookTimeTotal) {
                furnace.cookTime = 0;
                recipe.consumeInputs(furnace);
                ItemStack result = recipe.assemble(furnace, level.registryAccess());
                ItemStack out = furnace.items.get(SLOT_OUTPUT);
                if (out.isEmpty()) {
                    furnace.items.set(SLOT_OUTPUT, result);
                } else {
                    out.grow(result.getCount());
                }
                changed = true;
            }
        } else {
            furnace.cookTime = 0;
        }

        if (wasLit != furnace.isLit()) {
            changed = true;
            level.setBlock(pos, state.setValue(AlloyFurnaceBlock.LIT, furnace.isLit()), 3);
        }
        if (changed) {
            furnace.setChanged();
        }
    }

    private boolean isLit() {
        return this.burnTime > 0;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private AlloyRecipe findRecipe(Level level) {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING.get(), this, level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .orElse(null);
    }

    private boolean canOutput(AlloyRecipe recipe) {
        ItemStack result = recipe.getResultStack();
        if (result.isEmpty()) {
            return false;
        }
        ItemStack out = this.items.get(SLOT_OUTPUT);
        if (out.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(out, result)) {
            return false;
        }
        return out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.rptubes.alloy_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new AlloyFurnaceMenu(id, inventory, this, this.data);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
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
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{SLOT_OUTPUT};
        }
        if (side == Direction.UP) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        }
        return new int[]{SLOT_FUEL};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == SLOT_OUTPUT;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == SLOT_OUTPUT) {
            return false;
        }
        if (index == SLOT_FUEL) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("BurnTimeTotal", this.burnTimeTotal);
        tag.putInt("CookTime", this.cookTime);
        tag.putInt("CookTimeTotal", this.cookTimeTotal);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.burnTime = tag.getInt("BurnTime");
        this.burnTimeTotal = tag.getInt("BurnTimeTotal");
        this.cookTime = tag.getInt("CookTime");
        this.cookTimeTotal = tag.getInt("CookTimeTotal");
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER && side != null) {
            if (side == Direction.UP) {
                return handlers[0].cast();
            }
            if (side == Direction.DOWN) {
                return handlers[1].cast();
            }
            return handlers[2].cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<? extends IItemHandler> handler : handlers) {
            handler.invalidate();
        }
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    }
}
