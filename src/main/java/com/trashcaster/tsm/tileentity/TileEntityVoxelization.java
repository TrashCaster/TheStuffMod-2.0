package com.trashcaster.tsm.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameData;

public class TileEntityVoxelization extends TileEntity implements IUpdatePlayerListBox {
    private String blockID = "minecraft:air";
    private int blockMeta = 0;
    private float progress = 1f;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("Block", this.blockID);
        compound.setInteger("BlockMeta", this.blockMeta);
        compound.setFloat("Progress", this.progress);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.blockID = compound.getString("Block");
        this.blockMeta = compound.getInteger("BlockMeta");
        this.progress = compound.getFloat("Progress");
    }

    @Override
    public void update() {
        if (this.progress > 0f) {
            this.progress -= 0.5f;
        }
        if (this.progress <= 0f) {
            this.worldObj.setBlockState(pos, getStoredBlockState());
            System.out.println(String.format("%s turned back into %s|%d",
                    new Object[] { pos.toString(), this.blockID, this.blockMeta }));
        }
    }

    public void setStoredBlockState(IBlockState state) {
        System.out.println("Updating blockstate of " + pos.toString());
        this.blockID = GameData.getBlockRegistry().getNameForObject(state.getBlock()).toString();
        this.blockMeta = state.getBlock().getMetaFromState(state);
        System.out.println(
                String.format("%s turned into %s|%d", new Object[] { pos.toString(), this.blockID, this.blockMeta }));
    }

    public IBlockState getStoredBlockState() {
        Block block = Block.getBlockFromName(this.blockID);
        IBlockState state = block.getStateFromMeta(blockMeta);
        return state;
    }

    public float getProgress() {
        return this.progress;
    }

    public void addProgress(float amount) {
        this.progress += amount;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.pos, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound nbttagcompound = pkt.getNbtCompound();
        this.readFromNBT(nbttagcompound);
    }
}