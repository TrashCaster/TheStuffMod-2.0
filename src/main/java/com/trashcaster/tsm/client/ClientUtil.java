package com.trashcaster.tsm.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class ClientUtil {

    private static ClientUtil instance = null;

    public static ClientUtil getInstance() {
        if (instance == null)
            instance = new ClientUtil();
        return instance;
    }

    public void updatePlayerAnimations(RenderPlayerEvent event) {

        float f2 = this.interpolateRotation(event.entityPlayer.prevRenderYawOffset, event.entityPlayer.renderYawOffset,
                event.partialRenderTick);
        float f3 = this.interpolateRotation(event.entityPlayer.prevRotationYawHead, event.entityPlayer.rotationYawHead,
                event.partialRenderTick);
        float f4 = f3 - f2;
        float f5;

        if (event.entityPlayer.isRiding() && event.entityPlayer.ridingEntity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase1 = (EntityLivingBase) event.entityPlayer.ridingEntity;
            f2 = this.interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset,
                    event.partialRenderTick);
            f4 = f3 - f2;
            f5 = MathHelper.wrapAngleTo180_float(f4);

            if (f5 < -85.0F) {
                f5 = -85.0F;
            }

            if (f5 >= 85.0F) {
                f5 = 85.0F;
            }

            f2 = f3 - f5;

            if (f5 * f5 > 2500.0F) {
                f2 += f5 * 0.2F;
            }
        }

        float f9 = event.entityPlayer.prevRotationPitch
                + (event.entityPlayer.rotationPitch - event.entityPlayer.prevRotationPitch) * event.partialRenderTick;
        f5 = handleRotationFloat(event.entityPlayer, event.partialRenderTick);
        rotateCorpse((AbstractClientPlayer) event.entityPlayer, f5, f2, event.partialRenderTick);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        float f6 = 0.0625F;
        GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
        float f7 = event.entityPlayer.prevLimbSwingAmount
                + (event.entityPlayer.limbSwingAmount - event.entityPlayer.prevLimbSwingAmount)
                        * event.partialRenderTick;
        float f8 = event.entityPlayer.limbSwing - event.entityPlayer.limbSwingAmount * (1.0F - event.partialRenderTick);

        if (event.entityPlayer.isChild()) {
            f8 *= 3.0F;
        }

        if (f7 > 1.0F) {
            f7 = 1.0F;
        }

        GlStateManager.enableAlpha();
        event.renderer.getPlayerModel().setLivingAnimations(event.entityPlayer, f8, f7, event.partialRenderTick);
        event.renderer.getPlayerModel().setRotationAngles(f8, f7, f5, f4, f9, 0.0625F, event.entityPlayer);

    }

    private void preRenderCallback(AbstractClientPlayer p_77041_1_, float p_77041_2_) {
        float f1 = 0.9375F;
        GlStateManager.scale(f1, f1, f1);
    }

    private void rotateCorpse(AbstractClientPlayer player, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
        if (player.isEntityAlive() && player.isPlayerSleeping()) {
            GlStateManager.rotate(player.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90f, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else {
            superRotateCorpse(player, p_77043_2_, p_77043_3_, p_77043_4_);
        }
    }

    private void superRotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
        GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);

        if (p_77043_1_.deathTime > 0) {
            float f3 = (p_77043_1_.deathTime + p_77043_4_ - 1.0F) / 20.0F * 1.6F;
            f3 = MathHelper.sqrt_float(f3);

            if (f3 > 1.0F) {
                f3 = 1.0F;
            }

            GlStateManager.rotate(f3 * 90f, 0.0F, 0.0F, 1.0F);
        } else {
            String s = EnumChatFormatting.getTextWithoutFormattingCodes(p_77043_1_.getName());

            if (s != null && (s.equals("Dinnerbone") || s.equals("Grumm")) && (!(p_77043_1_ instanceof EntityPlayer)
                    || ((EntityPlayer) p_77043_1_).func_175148_a(EnumPlayerModelParts.CAPE))) {
                GlStateManager.translate(0.0F, p_77043_1_.height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    private float handleRotationFloat(EntityLivingBase p_77044_1_, float p_77044_2_) {
        return p_77044_1_.ticksExisted + p_77044_2_;
    }

    private float interpolateRotation(float p_77034_1_, float p_77034_2_, float p_77034_3_) {
        float f3;

        for (f3 = p_77034_2_ - p_77034_1_; f3 < -180.0F; f3 += 360.0F) {
            ;
        }

        while (f3 >= 180.0F) {
            f3 -= 360.0F;
        }

        return p_77034_1_ + p_77034_3_ * f3;
    }

}
