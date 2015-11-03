package com.trashcaster.tsm.item;

import com.trashcaster.tsm.TSM;
import com.trashcaster.tsm.tileentity.TileEntityVoxelization;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemVoxelizer extends Item {

    private final String name = "voxelizer";

    public ItemVoxelizer() {
        GameRegistry.registerItem(this, name);
        setUnlocalizedName(TSM.MODID + "." + name);
        setCreativeTab(CreativeTabs.tabTools);
    }

    public String getName() {
        return name;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        playerIn.playSound("tsm:item.voxelizer.finish", 1f, 1f);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        playerIn.playSound("tsm:item.voxelizer.finish", 1f, 1f);
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        MovingObjectPosition ray = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, false);
        boolean valid = false;
        if (ray != null && ray.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
            IBlockState block = worldIn.getBlockState(ray.getBlockPos());
            if (block.getBlock().equals(Blocks.stone)) {
                valid = true;
            }
        }
        if (valid) {
            playerIn.playSound("tsm:item.voxelizer.start", 1f, 2f);
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        } else {
            playerIn.playSound("tsm:item.voxelizer.fail", 1f, 1f);
        }
        return itemStackIn;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (count < getMaxItemUseDuration(stack) - 5) {
            if (count % 3 == 0) {
                player.playSound("tsm:item.voxelizer.use", 1f, 1f);
            }
        }
        MovingObjectPosition ray = this.getMovingObjectPositionFromPlayer(player.worldObj, player, false);
        boolean valid = false;
        if (ray != null && ray.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
            IBlockState block = player.worldObj.getBlockState(ray.getBlockPos());
            if (block.getBlock().equals(Blocks.stone)) {
                valid = true;

                if (!player.worldObj.isRemote) {
                    player.worldObj.setBlockState(ray.getBlockPos(), TSM.voxelization.getDefaultState());
                    TileEntityVoxelization te = new TileEntityVoxelization();
                    te.setStoredBlockState(block);
                    player.worldObj.setTileEntity(ray.getBlockPos(), te);
                    player.worldObj.markBlockForUpdate(ray.getBlockPos());
                }
            }
            if (block.getBlock().equals(TSM.voxelization)) {
                if (!player.worldObj.isRemote) {
                    TileEntityVoxelization te = (TileEntityVoxelization) player.worldObj
                            .getTileEntity(ray.getBlockPos());
                    if (te != null) {
                        te.addProgress(1f);
                    }
                }
                valid = true;
            }
        }
        if (!valid) {
            player.stopUsingItem();
        }
    }
}
