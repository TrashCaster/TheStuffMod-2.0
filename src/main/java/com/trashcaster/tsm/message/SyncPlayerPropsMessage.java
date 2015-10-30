package com.trashcaster.tsm.message;

import java.util.UUID;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.client.ClientProxy;
import com.trashcaster.tsm.entity.ExtendedPlayer;
import com.trashcaster.tsm.inventory.InventoryAccessories;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					System.out.println("Sync request received from "+ctx.getServerHandler().playerEntity.getName());
					EntityTracker tracker = ((WorldServer) ctx.getServerHandler().playerEntity.worldObj).getEntityTracker();
					for (EntityPlayer player:tracker.getTrackingPlayers(ctx.getServerHandler().playerEntity)) {
						ExtendedPlayer.get(player).getAccessories();
						TSM.NETWORK.sendTo(new SyncPlayerPropsMessage(player), ctx.getServerHandler().playerEntity);
					}
				}
			});
			return null;
		}
	}
}
