package com.trashcaster.tsm.message;

import com.trashcaster.tsm.TSM;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Message implements IMessage {

    String string = "";

    public Message() {
    }

    public Message(String string) {
        this.string = string;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        string = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, string);
    }

    public static class ClientHandler implements IMessageHandler<Message, IMessage> {
        @Override
        public IMessage onMessage(final Message message, final MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    System.out.println(String.format("Received %s from server", message.string));
                }
            });
            return null;
        }
    }

    public static class ServerHandler implements IMessageHandler<Message, IMessage> {
        @Override
        public IMessage onMessage(final Message message, final MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or
                                                                                                     // Minecraft.getMinecraft()
                                                                                                     // on
                                                                                                     // the
                                                                                                     // client
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    System.out.println(String.format("Received %s from %s", message.string,
                            ctx.getServerHandler().playerEntity.getName()));
                    if (message.string.equals("accessoryinv")) {
                        int x = (int) ctx.getServerHandler().playerEntity.posX;
                        int y = (int) ctx.getServerHandler().playerEntity.posY;
                        int z = (int) ctx.getServerHandler().playerEntity.posZ;
                        ctx.getServerHandler().playerEntity.openGui(TSM.INSTANCE, 0,
                                ctx.getServerHandler().playerEntity.worldObj, x, y, z);
                    }
                }
            });
            return new Message("done");
        }
    }
}
