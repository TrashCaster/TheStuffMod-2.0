package com.trashcaster.tsm.common;

import com.trashcaster.tsm.entity.ExtendedPlayer;
import com.trashcaster.tsm.inventory.InventoryAccessories;
import com.trashcaster.tsm.message.SyncPlayerPropsMessage;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {

    private CommonEventHandler eventHandler = new CommonEventHandler();

    public void preInit() {
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
    }

    public void init() {
    }

    public void postInit() {
    }

    public class CommonEventHandler {
        @SubscribeEvent
        public void onEntityConstructing(EntityConstructing event) {
            if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
                ExtendedPlayer.register((EntityPlayer) event.entity);
            }

            if (event.entity instanceof EntityPlayer
                    && event.entity.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME) == null) {
                event.entity.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME,
                        new ExtendedPlayer((EntityPlayer) event.entity));
            }
        }

        @SubscribeEvent
        public void onEntityJoinWorld(EntityJoinWorldEvent event) {
            if (event.entity instanceof EntityPlayerMP) {
                if (!event.world.isRemote) {
                    SyncPlayerPropsMessage.resync((EntityPlayerMP) event.entity, false);
                }
            }
        }

        // First, this way the items will be added,
        // and can be canceled by other mods
        @SubscribeEvent(priority=EventPriority.HIGHEST)
        public void onPlayerDeathDrops(PlayerDropsEvent event) {
            EntityPlayer player = event.entityPlayer;
            ExtendedPlayer props = ExtendedPlayer.get(player);
            if (!event.entityPlayer.worldObj.isRemote) {
                for (int i = 0; i < InventoryAccessories.INV_SIZE; i++) {
                    ItemStack item = props.getAccessories().getStackInSlot(i);
                    if (item != null) {
                        EntityItem e = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, item);
                        event.drops.add(e);
                    }
                }
                props.getAccessories().clear();
            }
            SyncPlayerPropsMessage.resync((EntityPlayerMP) event.entity, true);
        }

        @SubscribeEvent
        public void onPlayerTrack(PlayerEvent.StartTracking event) {
            if (event.entity instanceof EntityPlayer) {
                if (!event.entity.worldObj.isRemote) {
                    SyncPlayerPropsMessage.resync((EntityPlayerMP) event.entity, true);
                }
            }
        }
    }
}
