package com.trashcaster.tsm.entity;

import com.trashcaster.tsm.inventory.InventoryAccessories;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
    public final static String EXT_PROP_NAME = "ExtendedPlayer";

    private final EntityPlayer player;

    private final InventoryAccessories accessories;

    public ExtendedPlayer(EntityPlayer player) {
        this.player = player;
        this.accessories = new InventoryAccessories();
    }

    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(player));
    }

    public static final ExtendedPlayer get(EntityPlayer player) {
        return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagCompound inventory = new NBTTagCompound();
        this.accessories.writeToNBT(inventory);
        properties.setTag("accessories", inventory);
        compound.setTag(EXT_PROP_NAME, properties);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
        NBTTagCompound inventory = properties.getCompoundTag("accessories");
        this.accessories.readFromNBT(inventory);
    }

    @Override
    public void init(Entity entity, World world) {
    }

    public InventoryAccessories getAccessories() {
        return this.accessories;
    }
}