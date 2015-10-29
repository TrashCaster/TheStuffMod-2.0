package com.trashcaster.tsm.common;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.client.gui.inventory.GuiInventoryExpanded;
import com.trashcaster.tsm.entity.ExtendedPlayer;
import com.trashcaster.tsm.inventory.ContainerInventoryExpanded;
import com.trashcaster.tsm.inventory.InventoryAccessories;
import com.trashcaster.tsm.message.SyncPlayerPropsMessage;
import com.trashcaster.tsm.message.Message;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	private static CommonEventHandler eventHandler = new CommonEventHandler();

	public void preInit() {
		TSM.NETWORK.registerMessage(Message.ServerHandler.class, Message.class, 1, Side.SERVER);
		TSM.NETWORK.registerMessage(SyncPlayerPropsMessage.ServerHandler.class, SyncPlayerPropsMessage.class, 2, Side.SERVER);
		MinecraftForge.EVENT_BUS.register(eventHandler);
		FMLCommonHandler.instance().bus().register(eventHandler);
	}

	public void init() {}
	public void postInit() {}

	public static class CommonEventHandler {
		@SubscribeEvent
		public void onEntityConstructing(EntityConstructing event) {
			if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
				ExtendedPlayer.register((EntityPlayer) event.entity);
			}

			if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME) == null) {
				event.entity.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer((EntityPlayer) event.entity));
			}
		}

		@SubscribeEvent
		public void onEntityJoinWorld(EntityJoinWorldEvent event) {
			if (event.entity instanceof EntityPlayer) {
				ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.entity);
				if (!event.world.isRemote) {
				    TSM.NETWORK.sendToAll(new SyncPlayerPropsMessage((EntityPlayer)event.entity));
				}
			}
		}
		
		@SubscribeEvent
		public void onPlayerDeathDrops(PlayerDropsEvent event) {
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.entity);
			if (!event.entityPlayer.worldObj.isRemote) {
				for (int i=0; i<InventoryAccessories.INV_SIZE; i++) {
					ItemStack item = props.getAccessories().getStackInSlot(i);
					if (item != null) {
					    event.entityPlayer.dropItem(item, true, true);
					}
				}
			}
		}
	}
}
