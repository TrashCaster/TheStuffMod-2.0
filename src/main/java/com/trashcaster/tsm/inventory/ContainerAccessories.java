package com.trashcaster.tsm.inventory;

import com.trashcaster.tsm.client.ClientProxy;
import com.trashcaster.tsm.item.ItemAccessory;
import com.trashcaster.tsm.message.SyncPlayerPropsMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerAccessories extends Container {

    private static final int ARMOR_START = InventoryAccessories.INV_SIZE, ARMOR_END = ARMOR_START + 3,

    INV_START = ARMOR_END + 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1,

    HOTBAR_END = HOTBAR_START + 8;

    final EntityPlayer player;

    public ContainerAccessories(final EntityPlayer player, InventoryPlayer inventoryPlayer,
            InventoryAccessories inventoryCustom) {
        this.player = player;

        int i;

        int x = 0;
        int y = 0;
        for (i = 0; i < inventoryCustom.getSizeInventory(); i++) {
            this.addSlotToContainer(new Slot(inventoryCustom, i, 98 + x * 18, 26 + y * 18) {
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack == null)
                        return false;
                    return stack.getItem() instanceof ItemAccessory;
                }
                
                @SideOnly(Side.CLIENT)
                @Override
                public String getSlotTexture() {
                    return ClientProxy.BLANK_ACCESSORY_ICON.toString();
                }
            });
            x++;
            if (x > 2) {
                x = 0;
                y++;
            }
        }

        for (i = 0; i < 4; ++i) {
            final int k = i;
            this.addSlotToContainer(
                    new Slot(inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 8, 8 + i * 18) {
                        public int getSlotStackLimit() {
                            return 1;
                        }

                        public boolean isItemValid(ItemStack stack) {
                            if (stack == null)
                                return false;
                            return stack.getItem().isValidArmor(stack, k, player);
                        }

                        @SideOnly(Side.CLIENT)
                        public String getSlotTexture() {
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
                if (itemstack1.getItem() instanceof ItemAccessory) {
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
        if (!this.player.worldObj.isRemote) {
            SyncPlayerPropsMessage.resync((EntityPlayerMP) this.player, true);
        }
        return itemstack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!this.player.worldObj.isRemote) {
            SyncPlayerPropsMessage.resync((EntityPlayerMP) this.player, true);
        }
    }

    @Override
    protected boolean mergeItemStack(ItemStack itemstack, int i, int j, boolean flag) {
        // The default implementation in Slot doesn't take into account the Slot.isItemValid() and Slot.getSlotStackLimit() values.
        // So here is a modified implementation. I have only modified the parts with a comment.
    
        boolean flag1 = false;
        int k = i;
        if (flag) {
            k = j - 1;
        }
        if (itemstack.isStackable()) {
            while (itemstack.stackSize > 0 && (!flag && k < j || flag && k >= i)) {
                Slot slot = (Slot)inventorySlots.get(k);
                ItemStack itemstack1 = slot.getStack();
    
                if (flag) {
                    k--;
                }
                else {
                    k++;
                }
    
                // Check if item is valid:
                if (!slot.isItemValid(itemstack)) {
                    continue;
                }
    
                if (itemstack1 != null && itemstack1.getItem() == itemstack.getItem() && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == itemstack1.getItemDamage())
                        && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
                    //ItemStack.areItemStacksEqual(par0ItemStack, par1ItemStack)
                    //ItemStack.areItemStackTagsEqual(par0ItemStack, par1ItemStack)
                    int i1 = itemstack1.stackSize + itemstack.stackSize;
    
                    // Don't put more items than the slot can take:
                    int maxItemsInDest = Math.min(itemstack1.getMaxStackSize(), slot.getSlotStackLimit());
    
                    if (i1 <= maxItemsInDest) {
                        itemstack.stackSize = 0;
                        itemstack1.stackSize = i1;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                    else if (itemstack1.stackSize < maxItemsInDest) {
                        itemstack.stackSize -= maxItemsInDest - itemstack1.stackSize;
                        itemstack1.stackSize = maxItemsInDest;
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }
    
            }
        }
        if (itemstack.stackSize > 0) {
            int l;
            if (flag) {
                l = j - 1;
            }
            else {
                l = i;
            }
            do {
                if ((flag || l >= j) && (!flag || l < i)) {
                    break;
                }
                Slot slot1 = (Slot)inventorySlots.get(l);
                ItemStack itemstack2 = slot1.getStack();
    
                if (flag) {
                    l--;
                }
                else {
                    l++;
                }
    
                // Check if item is valid:
                if (!slot1.isItemValid(itemstack)) {
                    continue;
                }
    
                if (itemstack2 == null) {
    
                    // Don't put more items than the slot can take:
                    int nbItemsInDest = Math.min(itemstack.stackSize, slot1.getSlotStackLimit());
                    ItemStack itemStack1 = itemstack.copy();
                    itemstack.stackSize -= nbItemsInDest;
                    itemStack1.stackSize = nbItemsInDest;
    
                    slot1.putStack(itemStack1);
                    slot1.onSlotChanged();
                    // itemstack.stackSize = 0;
                    flag1 = true;
                    break;
                }
            } while (true);
        }
        return flag1;
    }

    private int getArmorType(ItemStack is) {
        int type = -1;
        for (int i = 0; i < 4; i++) {
            if (is.getItem().isValidArmor(is, i, null)) {
                type = i;
                break;
            }
        }
        return type;
    }
}