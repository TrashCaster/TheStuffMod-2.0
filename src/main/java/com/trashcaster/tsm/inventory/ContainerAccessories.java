package com.trashcaster.tsm.inventory;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.message.SyncPlayerPropsMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerAccessories extends Container {
	
	private static final int ARMOR_START = InventoryAccessories.INV_SIZE, ARMOR_END = ARMOR_START + 3,

	INV_START = ARMOR_END + 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1,

	HOTBAR_END = HOTBAR_START + 8;
	
	final EntityPlayer player;

	public ContainerAccessories(final EntityPlayer player, InventoryPlayer inventoryPlayer, InventoryAccessories inventoryCustom) {
		this.player = player;
		
		int i;
		
		int x = 0;
		int y = 0;
		for (i=0; i<inventoryCustom.getSizeInventory(); i++) {
		        this.addSlotToContainer(new Slot(inventoryCustom, i, 98+x*18, 26+y*18) {
	                public int getSlotStackLimit()
	                {
	                    return 1;
	                }
	                public boolean isItemValid(ItemStack stack)
	                {
	                    if (stack == null) return false;
	                    return stack.getItem() instanceof ItemBlock;
	                }
		        });
		        x++;
		        if (x > 2) {
		        	x = 0; y++;
		        }
		}

		for (i = 0; i < 4; ++i) {
			final int k = i;
			this.addSlotToContainer(new Slot(inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i,
					8, 8 + i * 18) {
                public int getSlotStackLimit()
                {
                    return 1;
                }
                public boolean isItemValid(ItemStack stack)
                {
                    if (stack == null) return false;
                    return stack.getItem().isValidArmor(stack, k, player);
                }
                @SideOnly(Side.CLIENT)
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[k];
                }
            });
		}

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par2) {

		ItemStack itemstack = null;

		Slot slot = (Slot) this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack()) {

			ItemStack itemstack1 = slot.getStack();

			itemstack = itemstack1.copy();

			if (par2 < INV_START) {
				if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END + 1, true)) {
					return null;
				}
				slot.onSlotChange(itemstack1, itemstack);
			} else {
				// This is where to specify accessory items
				if (itemstack1.getItem() instanceof ItemBlock) {
					if (!this.mergeItemStack(itemstack1, 0, InventoryAccessories.INV_SIZE, false)) {
						return null;
					}
				} else if (getArmorType(itemstack1) != -1) {
					int type = getArmorType(itemstack1);

					if (!this.mergeItemStack(itemstack1, ARMOR_START + type, ARMOR_START + type + 1, false)) {
						return null;
					}

				} else if (par2 >= INV_START && par2 < HOTBAR_START) {
					if (!this.mergeItemStack(itemstack1, HOTBAR_START, HOTBAR_START + 1, false)) {

						return null;
					}
				} else if (par2 >= HOTBAR_START && par2 < HOTBAR_END + 1) {
					if (!this.mergeItemStack(itemstack1, INV_START, INV_END + 1, false)) {
						return null;
					}
				}
			}

			if (itemstack1.stackSize == 0) {

				slot.putStack((ItemStack) null);

			} else {

				slot.onSlotChanged();

			}
			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, itemstack1);
		}
		return itemstack;
	}
	
	@Override
    public void detectAndSendChanges()
    {
    	super.detectAndSendChanges();
    	if (!this.player.worldObj.isRemote) {
    	    TSM.NETWORK.sendToAll(new SyncPlayerPropsMessage(player));
    	}
    }
	
	private int getArmorType(ItemStack is) {
		int type = -1;
		for (int i=0; i<4; i++) {
			if (is.getItem().isValidArmor(is, i, null)) {
				type = i;
				break;
			}
		}
		return type;
	}
}