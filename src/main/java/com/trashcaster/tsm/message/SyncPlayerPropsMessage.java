package com.trashcaster.tsm.message;

import java.util.UUID;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.client.ClientProxy;
import com.trashcaster.tsm.entity.ExtendedPlayer;
import com.trashcaster.tsm.inventory.InventoryAccessories;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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

	public SyncPlayerPropsMessage() {
	}

	// This packet should only be sent from the server
	public SyncPlayerPropsMessage(EntityPlayer player) {
		this.playerID = player.getGameProfile().getId();
		this.tag = new NBTTagCompound();
		ExtendedPlayer.get(player).getAccessories().writeToNBT(this.tag);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.playerID.toString());
		ByteBufUtils.writeTag(buf, this.tag);
	}

	@SideOnly(Side.CLIENT)
	public static class ClientHandler implements IMessageHandler<SyncPlayerPropsMessage, IMessage> {
		@Override
		public IMessage onMessage(final SyncPlayerPropsMessage message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
				    InventoryAccessories inv = new InventoryAccessories();
					inv.readFromNBT(message.tag);
					ClientProxy.playerInventories.put(message.playerID, inv);
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
					System.out.println("Client packet received on server. This should NOT be happening!");
				}
			});
			return null;
		}
	}
}
