package com.trashcaster.tsm.client.gui.inventory;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.status.StatusConsoleListener;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.message.Message;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiCreativeInventoryExpanded extends GuiContainerCreative {
	int time = 0;
	
	public GuiCreativeInventoryExpanded(EntityPlayer p_i1094_1_) {
		super(p_i1094_1_);
	}

	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		time++;
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.translate(0, 0, 1000);
        Color color = new Color(255,255,255,Math.min(255, Math.max(600-time,0)));
        Color color2 = new Color(10,10,10,Math.min(255, Math.max(600-time,0)));
        if (color.getAlpha() > 5) {
        	String str = StatCollector.translateToLocalFormatted("inventory.accessoryOpen", Keyboard.getKeyName(mc.gameSettings.keyBindInventory.getKeyCode()));
            this.fontRendererObj.drawSplitString(str, 9, 9, this.width+1, color2.getRGB());
            this.fontRendererObj.drawSplitString(str, 8, 8, this.width, color.getRGB());
        }
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }
	
	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode() && this.isShiftKeyDown()) {
            TSM.NETWORK.sendToServer(new Message("accessoryinv"));
        } else {
        	super.keyTyped(typedChar, keyCode);
        }
    }
}
