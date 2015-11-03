package com.trashcaster.tsm.client;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.client.gui.inventory.GuiCreativeInventoryExpanded;
import com.trashcaster.tsm.client.gui.inventory.GuiInventoryExpanded;
import com.trashcaster.tsm.common.CommonProxy;
import com.trashcaster.tsm.entity.ExtendedPlayer;
import com.trashcaster.tsm.inventory.InventoryAccessories;
import com.trashcaster.tsm.item.ItemAccessory;
import com.trashcaster.tsm.item.ItemAccessory.AccessoryType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

    private ClientEventHandler eventHandler = new ClientEventHandler();
    public static ResourceLocation BLANK_ACCESSORY_ICON = new ResourceLocation(TSM.MODID,
            "gui/container/accessory_slot");

    public static ResourceLocation AMULET = new ResourceLocation(TSM.MODID, "models/accessory/amulet.png");

    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
    }

    @Override
    public void init() {
        super.init();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(TSM.voxelizer, 0,
                new ModelResourceLocation(TSM.MODID + ":" + TSM.voxelizer.getName(), "inventory"));
        renderItem.getItemModelMesher().register(TSM.pendant, 0,
                new ModelResourceLocation(TSM.MODID + ":" + TSM.pendant.getName(), "inventory"));
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    private class ClientEventHandler {
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onTextureStitch(TextureStitchEvent.Pre event) {
            event.map.registerSprite(BLANK_ACCESSORY_ICON);
            event.map.registerSprite(AMULET);
        }

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

        /*
         * A bug in RenderPlayerEvent causes the client player to be used for
         * rendering instead of the actively rendered player. We offset the
         * render position back to 0,0,0 and the offset it to the correct
         * player.
         */

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onRenderPlayer(RenderPlayerEvent.Post event) {
            EntityPlayer p1 = Minecraft.getMinecraft().thePlayer;
            double x1 = p1.prevPosX + (p1.posX - p1.prevPosX) * event.partialRenderTick;
            double y1 = p1.prevPosY + (p1.posY - p1.prevPosY) * event.partialRenderTick;
            double z1 = p1.prevPosZ + (p1.posZ - p1.prevPosZ) * event.partialRenderTick;

            EntityPlayer p2 = event.entityPlayer;
            double x2 = p2.prevPosX + (p2.posX - p2.prevPosX) * event.partialRenderTick;
            double y2 = p2.prevPosY + (p2.posY - p2.prevPosY) * event.partialRenderTick;
            double z2 = p2.prevPosZ + (p2.posZ - p2.prevPosZ) * event.partialRenderTick;
            GL11.glPushMatrix();
            GL11.glTranslated(-x1, -y1, -z1);
            GL11.glTranslated(x2, y2, z2);
            InventoryAccessories inv = ExtendedPlayer.get(event.entityPlayer).getAccessories();
            float yaw = p2.prevRotationYaw + (p2.rotationYaw - p2.prevRotationYaw) * event.partialRenderTick;

            ClientUtil.getInstance().updatePlayerAnimations(event);
            float bodyYaw = event.renderer.getPlayerModel().bipedBody.rotateAngleY;

            if (inv != null) {
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack is = inv.getStackInSlot(i) != null ? inv.getStackInSlot(i).copy() : null;
                    if (is != null && is.getItem() instanceof ItemAccessory) {
                        // ItemStack's getItem should always be an accessory for
                        // this inventory, but we have to check, just in case
                        // someone NBT edits something else in
                        ItemAccessory.AccessoryType type = ((ItemAccessory) is.getItem()).getType();
                        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
                        Tessellator t = Tessellator.getInstance();
                        if (type.equals(AccessoryType.AMULET)) {
                            GlStateManager.pushMatrix();
                            GlStateManager.rotate(bodyYaw, 0, 1, 0);
                            GlStateManager.rotate(180f, 1, 0, 0);
                            double offset = Math.min(0.5d,
                                    Math.max(-0.5d, (-1d + (new Random(i * 1000).nextDouble() * 2d)) / 8d));
                            double zoffset = (-1d + (new Random(i * 1000).nextDouble() * 2d)) / 16d;
                            boolean flip = new Random(i * 1000).nextBoolean();
                            if (flip) {
                                GlStateManager.disableCull();
                                GlStateManager.scale(-1, 1, 1);
                            }
                            GlStateManager.translate(offset, 0d, Math.max(0d, zoffset));
                            GlStateManager.translate(0, 0, 0.12d);
                            ri.renderItemModelForEntity(is, event.entityPlayer, TransformType.HEAD);
                            GlStateManager.popMatrix();
                        }
                    }
                }
            }
            GL11.glPopMatrix();
        }

    }
}
