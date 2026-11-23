package com.rptubes.blockentity;
//? if fabric && >=1.20.6 && <1.21.1 {

/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerHelper;
import com.rptubes.inventory.InvWrapper;
import com.rptubes.inventory.SidedInvWrapper;

import com.rptubes.util.FuelHelper;
*///?}

import com.rptubes.block.AlloyFurnaceBlock;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.recipe.AlloyRecipe;
//? if fabric && >=1.21.1 && <1.21.11 {
/*import com.rptubes.util.FuelHelper;
*///?}
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
//? if fabric && >=1.21.1 && <1.21.11 {
/*import net.minecraft.world.item.crafting.RecipeInput;
*///?} else if >=1.21.1 && <1.21.11 {
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
//?} else if forgelike || (>=1.21.1 && <26.1.2) {
/*import net.minecraft.world.item.crafting.RecipeType;
*///?}
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if fabric && <1.20.6 {
/*import com.rptubes.util.FuelHelper;
import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.SidedInvWrapper;
*///?} else if fabric && >=1.21.11 && <26.1.2 {
/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.SidedInvWrapper;
*///?} else if neoforge && >=26.2 {
/*import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
*///?} else if fabric && >=1.21.1 {
/*import com.rptubes.inventory.IItemHandler;
import com.rptubes.inventory.ItemHandlerProvider;
import com.rptubes.inventory.SidedInvWrapper;
*///?} else if neoforge {
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
//?} else if forge {
/*import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
*///?}

/**
 * Port of eloraam.base.TileAlloyFurnace — 3x3 alloy grid, fuel, output.
 */
//? if forgelike && <1.21.1 {
/*public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
*///?} else if forgelike && <1.21.11 {
public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeInput {
//?} else if >=1.21.1 && <1.21.11 {
/*public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, ItemHandlerProvider {
*///?} else if forgelike {
/*public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, net.minecraft.world.item.crafting.RecipeInput {
*///?} else if <26.1.2 {
/*public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, com.rptubes.inventory.ItemHandlerProvider {
*///?} else {
/*public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity implements ItemHandlerProvider, WorldlyContainer, net.minecraft.world.item.crafting.RecipeInput {
*///?}
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

    //? if neoforge && <1.21.1 {


    /*private final IItemHandler handlerUp = new SidedInvWrapper(this, Direction.UP);
    private final IItemHandler handlerDown = new SidedInvWrapper(this, Direction.DOWN);
    private final IItemHandler handlerSide = new SidedInvWrapper(this, Direction.NORTH);

    public IItemHandler getItemHandler(Direction side) {
        if (side == Direction.UP) return this.handlerUp;
        if (side == Direction.DOWN) return this.handlerDown;
        return this.handlerSide;
    }
    *///?} else if neoforge && <1.21.11 {


    private final IItemHandler handlerUp = new SidedInvWrapper(this, Direction.UP);
    private final IItemHandler handlerDown = new SidedInvWrapper(this, Direction.DOWN);
    private final IItemHandler handlerSide = new SidedInvWrapper(this, Direction.NORTH);

    public IItemHandler getItemHandler(Direction side) {
        if (side == Direction.UP) {
            return this.handlerUp;
        }
        if (side == Direction.DOWN) {
            return this.handlerDown;
        }
        return this.handlerSide;
    }
    //?} else if fabric && >=1.21.11 && <26.1.2 {
    /*private IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    *///?} else if neoforge && >=26.2 {
    /*public ResourceHandler<ItemResource> getItemHandler(Direction side) {
        return new WorldlyContainerWrapper(this, side);
    }
    *///?} else if fabric && <1.21.1 {
    /*private final IItemHandler[] handlers =
            SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    *///?} else if !forge {
    /*private final IItemHandler handlerUp = new SidedInvWrapper(this, Direction.UP);
    private final IItemHandler handlerDown = new SidedInvWrapper(this, Direction.DOWN);
    private final IItemHandler handlerSide = new SidedInvWrapper(this, Direction.NORTH);

    public IItemHandler getItemHandler(Direction side) {
        if (side == Direction.UP) {
            return this.handlerUp;
        }
        if (side == Direction.DOWN) {
            return this.handlerDown;
        }
        return this.handlerSide;
    }
    *///?} else {
    /*private LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    *///?}

    public AlloyFurnaceBlockEntity(BlockPos pos, BlockState state) {
        //? if forgelike {
        super(ModBlockEntities.ALLOY_FURNACE.get(), pos, state);
        //?} else {
        /*super(ModBlockEntities.ALLOY_FURNACE, pos, state);
        *///?}
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
            //? if neoforge && >=26.1.2 {
            /*int burn = fuel.getBurnTime(RecipeType.SMELTING, level.fuelValues());
            *///?} else if >=1.21.11 {
            /*int burn = level.fuelValues().burnDuration(fuel);
            *///?} else if forge {
            /*int burn = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
            *///?} else if fabric {
            /*int burn = FuelHelper.getBurnTime(fuel);
            *///?} else {
            int burn = fuel.getBurnTime(RecipeType.SMELTING);
            //?}
            if (burn > 0) {
                furnace.burnTime = furnace.burnTimeTotal = burn;
                changed = true;
                //? if forgelike && <1.21.11 {
                if (fuel.hasCraftingRemainingItem()) {
                    furnace.items.set(SLOT_FUEL, fuel.getCraftingRemainingItem());
                //?} else if <1.21.11 {
                /*if (fuel.getItem().hasCraftingRemainingItem()) {
                    furnace.items.set(SLOT_FUEL, new ItemStack(fuel.getItem().getCraftingRemainingItem()));
                *///?} else if >=26.1.2 {
                /*// 26.1: crafting remainders are ItemStackTemplates now
                net.minecraft.world.item.ItemStackTemplate remainderTemplate = fuel.getCraftingRemainder();
                ItemStack remainder = remainderTemplate == null ? ItemStack.EMPTY : remainderTemplate.create();
                if (!remainder.isEmpty()) {
                    furnace.items.set(SLOT_FUEL, remainder);
                *///?} else if fabric {
                /*ItemStack remainder = fuel.getItem().getCraftingRemainder();
                if (!remainder.isEmpty()) {
                    furnace.items.set(SLOT_FUEL, remainder);
                *///?} else {
                /*ItemStack remainder = fuel.getCraftingRemainder();
                if (!remainder.isEmpty()) {
                    furnace.items.set(SLOT_FUEL, remainder);
                *///?}
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
                //? if >=26.1.2 || <1.20.1 {
                /*ItemStack result = recipe.assemble(furnace);
                *///?} else if forgelike || <1.21.1 {
                ItemStack result = recipe.assemble(furnace, level.registryAccess());
                //?} else if <1.21.11 {
                /*ItemStack result = recipe.assemble(furnace.recipeInput, level.registryAccess());
                *///?} else {
                /*ItemStack result = recipe.assemble(furnace.alloyInput(), level.registryAccess());
                *///?}
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

        //? if fabric && <1.20.6 {
        /*private AlloyRecipe findRecipe(Level level) {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING, this, level)
                .orElse(null);
        *///?} else if fabric && <1.21.1 {
    /*@Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private AlloyRecipe findRecipe(Level level) {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING, this, level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .orElse(null);
        *///?} else if fabric && <1.21.11 {
    /*@Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    /^* RecipeInput view of the 3x3 alloy grid only (slots 0-8). Separate from Container so remap does not collide. ^/
    private final RecipeInput recipeInput = new RecipeInput() {
        @Override
        public ItemStack getItem(int index) {
            return AlloyFurnaceBlockEntity.this.getItem(index);
        }

        @Override
        public int size() {
            return SLOT_INPUT_COUNT;
        }
    };

    private AlloyRecipe findRecipe(Level level) {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING, this.recipeInput, level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .orElse(null);
        *///?} else if fabric && <26.1.2 {
    /*@Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private AlloyRecipe findRecipe(Level level) {
        if (level.getServer() == null) {
            return null;
        }
        return level.getServer().getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING, alloyInput(), level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .orElse(null);
    }

    /^* Separate RecipeInput so this Container is not remapped as both Container and RecipeInput. ^/
    private net.minecraft.world.item.crafting.RecipeInput alloyInput() {
        return new net.minecraft.world.item.crafting.RecipeInput() {
            @Override
            public ItemStack getItem(int slot) {
                return items.get(slot);
            }

            @Override
            public int size() {
                return SLOT_INPUT_COUNT;
            }
        };
        *///?} else if <1.20.6 {
        /*private AlloyRecipe findRecipe(Level level) {
        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING.get(), this, level)
                .orElse(null);
        *///?} else if fabric {
    /*@Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private AlloyRecipe findRecipe(Level level) {
        if (level.getServer() == null) {
            return null;
        }
        return level.getServer().getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING, this, level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .orElse(null);
    }

    @Override
    public int size() {
        return SLOT_INPUT_COUNT;
        *///?} else if <1.21.11 {
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
        //?} else {
    /*@Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private AlloyRecipe findRecipe(Level level) {
        if (level.getServer() == null) {
            return null;
        }
        return level.getServer().getRecipeManager()
                .getRecipeFor(ModRecipes.ALLOY_SMELTING.get(), this, level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .orElse(null);
    }

    @Override
    public int size() {
        return SLOT_INPUT_COUNT;
        *///?}
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
        //? if <1.20.6 {
        /*if (!ItemStack.isSameItemSameTags(out, result)) {
        *///?} else {
        if (!ItemStack.isSameItemSameComponents(out, result)) {
        //?}
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
    //? if forgelike && >=1.21.1 && <1.21.11 {
    }

    /** RecipeInput view: the 3x3 alloy grid only (slots 0-8). */
    @Override
    public int size() {
        return SLOT_INPUT_COUNT;
    //?}
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
            //? if neoforge && >=26.1.2 {
            /*return this.level != null && stack.getBurnTime(RecipeType.SMELTING, this.level.fuelValues()) > 0;
            *///?} else if >=1.21.11 {
            /*return this.level != null && this.level.fuelValues().burnDuration(stack) > 0;
            *///?} else if forge {
            /*return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
            *///?} else if fabric {
            /*return FuelHelper.getBurnTime(stack) > 0;
            *///?} else {
            return stack.getBurnTime(RecipeType.SMELTING) > 0;
            //?}
        }
        return true;
    }

    @Override
    //? if <1.20.6 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
    *///?} else if <1.21.11 {
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    //?} else {
    /*protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
    *///?}
        tag.putInt("BurnTime", this.burnTime);
        tag.putInt("BurnTimeTotal", this.burnTimeTotal);
        tag.putInt("CookTime", this.cookTime);
        tag.putInt("CookTimeTotal", this.cookTimeTotal);
    }

    @Override
    //? if <1.20.6 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.burnTime = tag.getInt("BurnTime");
        this.burnTimeTotal = tag.getInt("BurnTimeTotal");
        this.cookTime = tag.getInt("CookTime");
        this.cookTimeTotal = tag.getInt("CookTimeTotal");
    *///?} else if <1.21.11 {
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.burnTime = tag.getInt("BurnTime");
        this.burnTimeTotal = tag.getInt("BurnTimeTotal");
        this.cookTime = tag.getInt("CookTime");
        this.cookTimeTotal = tag.getInt("CookTimeTotal");
    //?} else {
    /*protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.burnTime = tag.getIntOr("BurnTime", 0);
        this.burnTimeTotal = tag.getIntOr("BurnTimeTotal", 0);
        this.cookTime = tag.getIntOr("CookTime", 0);
        this.cookTimeTotal = tag.getIntOr("CookTimeTotal", 0);
    *///?}
    }

    //? if forge {
    /*@Override
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
    *///?} else if <1.20.6 {
    /*public IItemHandler getItemHandler(Direction side) {
        if (side == Direction.UP) {
            return this.handlers[0];
        }
        if (side == Direction.DOWN) {
            return this.handlers[1];
        }
        return this.handlers[2];
    }
}
    *///?} else if neoforge || >=26.1.2 {
}
    //?} else if <1.21.1 {

    /*public IItemHandler getItemHandler(Direction side) {
        if (side == Direction.UP) {
            return this.handlers[0];
        }
        if (side == Direction.DOWN) {
            return this.handlers[1];
        }
        return this.handlers[2];
    }
}
    *///?} else if <1.21.11 {


/*}
    *///?} else {

    /*@Override
    public IItemHandler getItemHandler(Direction side) {
        if (side == Direction.UP) {
            return this.handlers[0];
        }
        if (side == Direction.DOWN) {
            return this.handlers[1];
        }
        return this.handlers[2];
    }
}
    *///?}
