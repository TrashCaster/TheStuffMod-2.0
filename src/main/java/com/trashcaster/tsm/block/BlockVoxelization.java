package com.trashcaster.tsm.block;

import com.trashcaster.tsm.tileentity.TileEntityVoxelization;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVoxelization extends BlockContainer {

    public BlockVoxelization() {
        super(Material.barrier);
        this.setHardness(-1.0F);
        this.setResistance(6000000.0F);
        this.isBlockContainer = true;

        GameRegistry.registerBlock(this, "voxelization");
        GameRegistry.registerTileEntity(TileEntityVoxelization.class, "voxelization");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        TileEntityVoxelization te = (TileEntityVoxelization) worldIn.getTileEntity(pos);
        if (te != null) {
            return te.getStoredBlockState().getBlock().getSelectedBoundingBox(worldIn, pos);
        }
        return new AxisAlignedBB(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ,
                pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityVoxelization();
    }

}
