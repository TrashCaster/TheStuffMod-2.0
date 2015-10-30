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
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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

	private ClientEventHandler eventHandler = new ClientEventHandler();

	@Override
	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		FMLCommonHandler.instance().bus().register(eventHandler);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	private class ClientEventHandler {
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
		public void onMouse(MouseEvent event) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
				event.setCanceled(true);
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onRenderPlayer(RenderPlayerEvent.Post event) {
			InventoryAccessories inv = ExtendedPlayer.get(event.entityPlayer).getAccessories();
			EntityPlayer p1 = Minecraft.getMinecraft().thePlayer;
			double x1 = p1.prevPosX + (p1.posX - p1.prevPosX)*event.partialRenderTick;
			double y1 = p1.prevPosY + (p1.posY - p1.prevPosY)*event.partialRenderTick;
			double z1 = p1.prevPosZ + (p1.posZ - p1.prevPosZ)*event.partialRenderTick;

			EntityPlayer p2 = event.entityPlayer;
			double x2 = p2.prevPosX + (p2.posX - p2.prevPosX)*event.partialRenderTick;
			double y2 = p2.prevPosY + (p2.posY - p2.prevPosY)*event.partialRenderTick;
			double z2 = p2.prevPosZ + (p2.posZ - p2.prevPosZ)*event.partialRenderTick;

			GL11.glPushMatrix();
			GL11.glTranslated(-x1,-y1,-z1); // or negative values, if I'm inverting the wrong way
			GL11.glTranslated(x2,y2,z2); // or positive values, if I'm inverting the wrong way
			if (System.currentTimeMillis() % 2000 == 0)
			System.out.println("Rendering "+event.entityPlayer.getName());
			if (inv != null) {
				for (int i=0; i<inv.getSizeInventory(); i++) {
					ItemStack is = inv.getStackInSlot(i);
					if (is != null) {
						RenderItem ri = Minecraft.getMinecraft().getRenderItem();
						GlStateManager.pushMatrix();
						GlStateManager.translate(0, 0.25d+((double)i)*0.5d, 0);
						ri.renderItemModel(is);
						GlStateManager.popMatrix();
					}
				}
			}
			// Render
			GL11.glPopMatrix();
		}
	}
}
