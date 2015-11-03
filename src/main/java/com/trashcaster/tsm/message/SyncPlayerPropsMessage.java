package com.trashcaster.tsm.message;

import java.util.ArrayList;
import java.util.UUID;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.entity.ExtendedPlayer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncPlayerPropsMessage implements IMessage {
    UUID playerID;
    NBTTagCompound tag;

    // This is for sending to servers from clients who need other
    // players' accessories synced to them (Sync request)
    public SyncPlayerPropsMessage() {
    }

    // This is for sending to clients who need the synced data of another player
    public SyncPlayerPropsMessage(EntityPlayer player) {
        this.playerID = player.getGameProfile().getId();
        this.tag = new NBTTagCompound();
        ExtendedPlayer.get(player).saveNBTData(this.tag);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String id = ByteBufUtils.readUTF8String(buf);
        if (!id.equals("NULL")) {
            this.playerID = UUID.fromString(id);
            this.tag = ByteBufUtils.readTag(buf);
        } else {
            // assume this is a client to server request
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (playerID == null || tag == null) {
            ByteBufUtils.writeUTF8String(buf, "NULL");
            // assume this is a client to server request
            return;
        }
        ByteBufUtils.writeUTF8String(buf, this.playerID.toString());
        ByteBufUtils.writeTag(buf, this.tag);
    }

    public static class ClientHandler implements IMessageHandler<SyncPlayerPropsMessage, IMessage> {
        @Override
        public IMessage onMessage(final SyncPlayerPropsMessage message, final MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(message.playerID);
                    if (player != null) {
                        ExtendedPlayer.get(player).getAccessories().clear();
                        ExtendedPlayer.get(player).loadNBTData(message.tag);
                    }
                }
            });
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<SyncPlayerPropsMessage, IMessage> {
        @Override
        public IMessage onMessage(final SyncPlayerPropsMessage message, final MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Sync request received from " + ctx.getServerHandler().playerEntity.getName());
                    resync(ctx.getServerHandler().playerEntity, true);
                }
            });
            return null;
        }
    }

    /*
     * SyncPlayerPropsMessage#resync(EntityPlayerMP arg0, boolean arg1)
     * 
     * @arg0 - This is the player you want to send the resync to. Usually will
     * be the player who updates their inventory.
     * 
     * @arg1 - Whether to also send it to players who are tracking arg0
     */
    public static void resync(EntityPlayerMP playerToSync, boolean addTracking) {
        EntityTracker tracker = ((WorldServer) playerToSync.worldObj).getEntityTracker();
        ArrayList<EntityPlayer> sendToPlayers = new ArrayList<EntityPlayer>();
        if (addTracking) {
            sendToPlayers.addAll(tracker.getTrackingPlayers(playerToSync));
        }
        sendToPlayers.add(playerToSync);
        for (EntityPlayer player : sendToPlayers) {
            // System.out.println("Sent " + player.getName() + "'s sync packet
            // to " + playerToSync.getName());
            ExtendedPlayer.get(player).getAccessories();
            TSM.NETWORK.sendTo(new SyncPlayerPropsMessage(player), playerToSync);
        }
    }
}
