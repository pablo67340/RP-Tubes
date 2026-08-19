package com.rptubes.network;

import com.rptubes.blockentity.AlloyFurnaceBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import com.rptubes.blockentity.AutoCraftingTableMk2BlockEntity;
import com.rptubes.blockentity.BufferBlockEntity;
import com.rptubes.blockentity.DeployerBlockEntity;
import com.rptubes.blockentity.EjectorBlockEntity;
import com.rptubes.blockentity.FilterBlockEntity;
import com.rptubes.blockentity.ItemDetectorBlockEntity;
import com.rptubes.blockentity.RegulatorBlockEntity;
import com.rptubes.blockentity.RelayBlockEntity;
import com.rptubes.blockentity.RetrieverBlockEntity;
import com.rptubes.blockentity.SortingMachineBlockEntity;
import com.rptubes.client.AlloyFurnaceScreen;
import com.rptubes.client.AutoCraftingTableMk2Screen;
import com.rptubes.client.AutoCraftingTableScreen;
import com.rptubes.client.BufferScreen;
import com.rptubes.client.DeployerScreen;
import com.rptubes.client.EjectorScreen;
import com.rptubes.client.FilterScreen;
import com.rptubes.client.ItemDetectorScreen;
import com.rptubes.client.RegulatorScreen;
import com.rptubes.client.RelayScreen;
import com.rptubes.client.RetrieverScreen;
import com.rptubes.client.SortingMachineScreen;
import com.rptubes.menu.AlloyFurnaceMenu;
import com.rptubes.menu.AutoCraftingTableMenu;
import com.rptubes.menu.AutoCraftingTableMk2Menu;
import com.rptubes.menu.BufferMenu;
import com.rptubes.menu.DeployerMenu;
import com.rptubes.menu.EjectorMenu;
import com.rptubes.menu.FilterMenu;
import com.rptubes.menu.ItemDetectorMenu;
import com.rptubes.menu.RegulatorMenu;
import com.rptubes.menu.RelayMenu;
import com.rptubes.menu.RetrieverMenu;
import com.rptubes.menu.SortingMachineMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import com.rptubes.util.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int GUI_ALLOY_FURNACE = 0;
    public static final int GUI_FILTER = 1;
    public static final int GUI_EJECTOR = 2;
    public static final int GUI_DEPLOYER = 3;
    public static final int GUI_BUFFER = 4;
    public static final int GUI_ITEM_DETECTOR = 5;
    public static final int GUI_RELAY = 6;
    public static final int GUI_RETRIEVER = 7;
    public static final int GUI_REGULATOR = 8;
    public static final int GUI_SORTING_MACHINE = 9;
    public static final int GUI_AUTO_CRAFTING_TABLE = 10;
    public static final int GUI_ACT_MK2 = 11;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity be = world.getTileEntity(x, y, z);
        switch (id) {
            case GUI_ALLOY_FURNACE:
                return new AlloyFurnaceMenu(player.inventory, (AlloyFurnaceBlockEntity) be);
            case GUI_FILTER:
                return new FilterMenu(player.inventory, (FilterBlockEntity) be);
            case GUI_EJECTOR:
                return new EjectorMenu(player.inventory, (EjectorBlockEntity) be);
            case GUI_DEPLOYER:
                return new DeployerMenu(player.inventory, (DeployerBlockEntity) be);
            case GUI_BUFFER:
                return new BufferMenu(player.inventory, (BufferBlockEntity) be);
            case GUI_ITEM_DETECTOR:
                return new ItemDetectorMenu(player.inventory, (ItemDetectorBlockEntity) be);
            case GUI_RELAY:
                return new RelayMenu(player.inventory, (RelayBlockEntity) be);
            case GUI_RETRIEVER:
                return new RetrieverMenu(player.inventory, (RetrieverBlockEntity) be);
            case GUI_REGULATOR:
                return new RegulatorMenu(player.inventory, (RegulatorBlockEntity) be);
            case GUI_SORTING_MACHINE:
                return new SortingMachineMenu(player.inventory, (SortingMachineBlockEntity) be);
            case GUI_AUTO_CRAFTING_TABLE:
                return new AutoCraftingTableMenu(player.inventory, (AutoCraftingTableBlockEntity) be);
            case GUI_ACT_MK2:
                return new AutoCraftingTableMk2Menu(player.inventory, (AutoCraftingTableMk2BlockEntity) be);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Object menu = this.getServerGuiElement(id, player, world, x, y, z);
        if (menu == null) {
            return null;
        }
        switch (id) {
            case GUI_ALLOY_FURNACE:
                return new AlloyFurnaceScreen((AlloyFurnaceMenu) menu);
            case GUI_FILTER:
                return new FilterScreen((FilterMenu) menu);
            case GUI_EJECTOR:
                return new EjectorScreen((EjectorMenu) menu);
            case GUI_DEPLOYER:
                return new DeployerScreen((DeployerMenu) menu);
            case GUI_BUFFER:
                return new BufferScreen((BufferMenu) menu);
            case GUI_ITEM_DETECTOR:
                return new ItemDetectorScreen((ItemDetectorMenu) menu);
            case GUI_RELAY:
                return new RelayScreen((RelayMenu) menu);
            case GUI_RETRIEVER:
                return new RetrieverScreen((RetrieverMenu) menu);
            case GUI_REGULATOR:
                return new RegulatorScreen((RegulatorMenu) menu);
            case GUI_SORTING_MACHINE:
                return new SortingMachineScreen((SortingMachineMenu) menu);
            case GUI_AUTO_CRAFTING_TABLE:
                return new AutoCraftingTableScreen((AutoCraftingTableMenu) menu);
            case GUI_ACT_MK2:
                return new AutoCraftingTableMk2Screen((AutoCraftingTableMk2Menu) menu);
            default:
                return null;
        }
    }
}
