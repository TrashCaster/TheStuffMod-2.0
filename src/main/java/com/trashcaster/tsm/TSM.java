package com.trashcaster.tsm;

import com.trashcaster.tsm.block.BlockVoxelization;
import com.trashcaster.tsm.client.gui.inventory.GuiAccessoryInventory;
import com.trashcaster.tsm.common.CommonProxy;
import com.trashcaster.tsm.entity.ExtendedPlayer;
import com.trashcaster.tsm.inventory.ContainerAccessories;
import com.trashcaster.tsm.item.ItemAccessory;
import com.trashcaster.tsm.item.ItemVoxelizer;
import com.trashcaster.tsm.message.Message;
import com.trashcaster.tsm.message.SyncPlayerPropsMessage;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TSM.MODID, version = TSM.VERSION)
public class TSM {
    public static final String MODID = "tsm";
    public static final String VERSION = "1.0";

    @Instance("tsm")
    public static TSM INSTANCE;

    @SidedProxy(clientSide = "com.trashcaster.tsm.client.ClientProxy", serverSide = "com.trashcaster.tsm.common.CommonProxy")
    public static CommonProxy PROXY;

    public static SimpleNetworkWrapper NETWORK;

    public GuiHandler guiHandler = new GuiHandler();

    public static ItemVoxelizer voxelizer;
    public static ItemAccessory pendant;

    public static Block voxelization;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("TSMnet");

        NETWORK.registerMessage(Message.ServerHandler.class, Message.class, 1, Side.SERVER);
        NETWORK.registerMessage(Message.ClientHandler.class, Message.class, 1, Side.CLIENT);
        NETWORK.registerMessage(SyncPlayerPropsMessage.ServerHandler.class, SyncPlayerPropsMessage.class, 2,
                Side.SERVER);
        NETWORK.registerMessage(SyncPlayerPropsMessage.ClientHandler.class, SyncPlayerPropsMessage.class, 2,
                Side.CLIENT);

        PROXY.preInit();

        voxelizer = new ItemVoxelizer();
        pendant = new ItemAccessory("pendant", ItemAccessory.AccessoryType.AMULET);

        voxelization = new BlockVoxelization();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, guiHandler);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit();
    }

    public class GuiHandler implements IGuiHandler {
        @Override
        public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            if (ID == 0) {
                return new ContainerAccessories(player, player.inventory, ExtendedPlayer.get(player).getAccessories());
            }
            return null;
        }

        @Override
        public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            if (ID == 0) {
                return new GuiAccessoryInventory(player, player.inventory, ExtendedPlayer.get(player).getAccessories());
            }
            return null;
        }
    }
}
