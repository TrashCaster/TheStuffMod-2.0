package com.trashcaster.tsm.client.gui.inventory;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.inventory.ContainerAccessories;
import com.trashcaster.tsm.inventory.InventoryAccessories;
import com.trashcaster.tsm.message.Message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiAccessoryInventory extends InventoryEffectRenderer {
	int time = 0;
	
	private float xSize_lo;
	private float ySize_lo;

	private static final ResourceLocation iconLocation = new ResourceLocation(TSM.MODID,"textures/gui/container/accessory_inventory.png");

	private final InventoryAccessories inventory;

	public GuiAccessoryInventory(EntityPlayer player, InventoryPlayer inventoryPlayer, InventoryAccessories inventoryCustom) {
		super(new ContainerAccessories(player, inventoryPlayer, inventoryCustom));
		this.inventory = inventoryCustom;
	}

	/**
	 * 
	 * Draws the screen and all the components in it.
	 * 
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		time++;
		super.drawScreen(par1, par2, par3);
		this.xSize_lo = (float) par1;
		this.ySize_lo = (float) par2;
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.translate(0, 0, 1000);
        Color color = new Color(255,255,255,Math.min(255, Math.max(600-time,0)));
        Color color2 = new Color(10,10,10,Math.min(255, Math.max(600-time,0)));
        if (color.getAlpha() > 5) {
        	String str = StatCollector.translateToLocalFormatted("inventory.accessoryClose", Keyboard.getKeyName(mc.gameSettings.keyBindInventory.getKeyCode()));
            this.fontRendererObj.drawSplitString(str, 9, 9, this.width+1, color2.getRGB());
            this.fontRendererObj.drawSplitString(str, 8, 8, this.width, color.getRGB());
        }
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
	}

	/**
	 * 
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 * 
	 */

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String s = StatCollector.translateToLocal("inventory.accessory");
		this.fontRendererObj.drawString(s, 125 - this.fontRendererObj.getStringWidth(s)/2, 16, 4210752);
	}

	/**
	 * 
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 * 
	 */

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(iconLocation);

		int k = this.guiLeft;//(this.width - this.xSize) / 2;

		int l = this.guiTop;//(this.height - this.ySize) / 2;

		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, (float) (k + 51) - this.xSize_lo, (float) (l + 75 - 50) - this.ySize_lo, mc.thePlayer);

	}

	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode() && this.isShiftKeyDown()) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
            Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(Minecraft.getMinecraft().thePlayer));
        } else {
        	super.keyTyped(typedChar, keyCode);
        }
    }
}