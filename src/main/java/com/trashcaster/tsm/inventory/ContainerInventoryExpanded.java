package com.trashcaster.tsm.inventory;

import com.trashcaster.tsm.entity.ExtendedPlayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;

public class ContainerInventoryExpanded extends ContainerPlayer {

    public ContainerInventoryExpanded(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
        super(playerInventory, localWorld, player);
        InventoryAccessories accessories = ExtendedPlayer.get(player).getAccessories();

        for (int i = 0; i < accessories.getSizeInventory(); i++) {
            this.addSlotToContainer(new Slot(accessories, i, 18 * i, 18 * i));
        }
    }

}
