package practicalities.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.lib.util.Util;

public class BlockLaser extends BlockBase implements ITileEntityProvider, ILaserReciver {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyDirection ATTACHED = PropertyDirection.create("attached");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockLaser() {
		super(Material.glass, "laser");
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, POWERED, FACING, ATTACHED);
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
		if(worldIn.isRemote)
			return;
		
		updateLaser(worldIn, pos);
	}
	
	@Override
	public void laserHit(World world, BlockPos pos, EnumFacing sideHit, BlockPos laserPos, boolean powered) {
		if(world.isRemote)
			return;
		
		TileLaser tile = (TileLaser) world.getTileEntity(pos);
		tile.laserHit(powered);
		
		updateLaser(world, pos);
	}
	
	public void updateLaser(World world, BlockPos pos) {
		TileLaser tile = (TileLaser) world.getTileEntity(pos);
		
		boolean wasPowered = tile.powered;
		boolean isPowered = world.isBlockPowered(pos) || tile.isPoweredByLaser();
		
		if(wasPowered != isPowered) {
			tile.updatePower(isPowered);
			world.setBlockState(pos, world.getBlockState(pos).withProperty(POWERED, isPowered));
		}
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
	public boolean hasTileEntity() {
		return true;
	}
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.TRANSLUCENT;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileLaser();
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	// DIRECTIONAL PLACING
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(ATTACHED, facing.getOpposite());
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileLaser laser = (TileLaser) worldIn.getTileEntity(pos);
		
		laser.attached = state.getValue(ATTACHED);
		laser.facing = placer.getHorizontalFacing().getOpposite();
		
		updateLaser(worldIn, pos);
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean somethingHappened = false;
		
		if(Util.isHoldingWrench(playerIn)) {
			somethingHappened = true;
			TileLaser te = (TileLaser)worldIn.getTileEntity(pos);
			if(te.facing == te.attached.getOpposite()) {
				te.facing = side;
			} else {
				te.facing = te.attached.getOpposite();
			}
		}
		
		return somethingHappened;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileLaser laser = (TileLaser) worldIn.getTileEntity(pos);
		
		state = state.withProperty(FACING, laser.facing);
		state = state.withProperty(ATTACHED, laser.attached);
		return state;
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(ATTACHED)).getIndex();

        if (((Boolean)state.getValue(POWERED)).booleanValue())
        {
            i |= 8;
        }

        return i;
    }
	
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ATTACHED, EnumFacing.VALUES[meta & 7]).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
	}
}
