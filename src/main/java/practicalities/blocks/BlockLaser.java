package practicalities.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.Logger;
import practicalities.machines.shippingcrate.TileShippingCrate;

public class BlockLaser extends BlockBase implements ITileEntityProvider, ILaserReciver {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
//    public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockLaser() {
		super(Material.glass, "laser");
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, FACING);
	}
	
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.UP);
    }
	
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileLaser tile = (TileLaser) worldIn.getTileEntity(pos);
		tile.onBreak();
    }
    
	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
//		if(worldIn.isRemote)
//			return;
		
		updateLaser(worldIn, pos);
	}
	
	@Override
	public void laserHit(World world, BlockPos pos, EnumFacing sideHit, BlockPos laserPos, boolean powered) {
//		if(world.isRemote)
//			return;
		
		TileLaser tile = (TileLaser) world.getTileEntity(pos);
		tile.laserHit(powered);
		
		updateLaser(world, pos);
	}
	
	public void updateLaser(World world, BlockPos pos) {
//		IBlockState state = world.getBlockState(pos);
		
		TileLaser tile = (TileLaser) world.getTileEntity(pos);
		
		boolean wasPowered = tile.powered;
		boolean isPowered = world.isBlockPowered(pos) || tile.isPoweredByLaser();
		
		if(wasPowered != isPowered) {
//			IBlockState newState = state.withProperty(POWERED, isPowered);
			tile.updatePower(isPowered);
//			if(!world.isRemote)
//				world.setBlockState(pos, newState);
		}
	}
	
	@Override
	public boolean hasTileEntity() {
		return true;
	}


	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileLaser();
	}
	
	// DIRECTIONAL PLACING
	
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)), 2);
    }
	
	public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn) {
        if (MathHelper.abs((float)entityIn.posX - (float)clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - (float)clickedBlock.getZ()) < 2.0F)
        {
            double d0 = entityIn.posY + (double)entityIn.getEyeHeight();

            if (d0 - (double)clickedBlock.getY() > 2.0D)
            {
                return EnumFacing.UP;
            }

            if ((double)clickedBlock.getY() - d0 > 0.0D)
            {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }

	public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, getFacing(meta).getOpposite());//.withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
    }

    public static EnumFacing getFacing(int meta) {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }
    
    public int getMetaFromState(IBlockState state) {
    	int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

//        if (((Boolean)state.getValue(POWERED)).booleanValue())
//        {
//            i |= 8;
//        }

        return i;
    }
}
