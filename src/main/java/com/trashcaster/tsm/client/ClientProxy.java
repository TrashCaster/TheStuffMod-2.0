package com.trashcaster.tsm.client;

import java.util.HashMap;
import java.util.UUID;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.client.gui.inventory.GuiCreativeInventoryExpanded;
import com.trashcaster.tsm.client.gui.inventory.GuiInventoryExpanded;
import com.trashcaster.tsm.common.CommonProxy;
import com.trashcaster.tsm.entity.ExtendedPlayer;
import com.trashcaster.tsm.inventory.InventoryAccessories;
import com.trashcaster.tsm.message.Message;
import com.trashcaster.tsm.message.SyncPlayerPropsMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

	private static ClientEventHandler eventHandler = new ClientEventHandler();
	public static HashMap<UUID,InventoryAccessories> playerInventories = new HashMap<UUID,InventoryAccessories>();

	@Override
	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		FMLCommonHandler.instance().bus().register(eventHandler);
		TSM.NETWORK.registerMessage(Message.ClientHandler.class, Message.class, 1, Side.CLIENT);
		TSM.NETWORK.registerMessage(SyncPlayerPropsMessage.ClientHandler.class, SyncPlayerPropsMessage.class, 2, Side.CLIENT);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	private static class ClientEventHandler {
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onOpenGui(GuiOpenEvent event) {
			if (event.gui != null && event.gui.getClass().equals(GuiInventory.class)) {
				if (Minecraft.getMinecraft().playerController.isInCreativeMode()) {
					event.gui = new GuiCreativeInventoryExpanded(Minecraft.getMinecraft().thePlayer);
				} else {
					event.gui = new GuiInventoryExpanded(Minecraft.getMinecraft().thePlayer);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onRenderPlayer(RenderPlayerEvent.Pre event) {
			InventoryAccessories inv = playerInventories.get(event.entityPlayer.getGameProfile().getId());
			System.out.println("Rendering "+event.entityPlayer.getName());
			if (inv != null) {
				for (int i=0; i<inv.getSizeInventory(); i++) {
					ItemStack is = inv.getStackInSlot(i);
					if (is != null) {
						RenderItem ri = Minecraft.getMinecraft().getRenderItem();
						GlStateManager.pushMatrix();
						GlStateManager.translate(0, 0.5d+i, 0);
						ri.renderItemModel(is);
						GlStateManager.popMatrix();
					}
				}
			}
		}
	}
}
