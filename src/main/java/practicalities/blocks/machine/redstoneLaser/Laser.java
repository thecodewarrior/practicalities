package practicalities.blocks.machine.redstoneLaser;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class Laser {

	EnumFacing direction;
	int begining, end;
	
	public void beginHit(World world, BlockPos pos) {
		EnumFacing sideHit = direction.getOpposite();
		
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		
		if(block instanceof ILaserListener) {
			( (ILaserListener) block ).laserHit(world, pos, sideHit);
		}
		
		if(block.isFlammable(world, pos, sideHit)) {
			BlockPos toMakeFirePos = pos.offset(sideHit);
			IBlockState toMakeFireState = world.getBlockState(toMakeFirePos);
			Block toMakeFireBlock = toMakeFireState.getBlock();
			
			if( toMakeFireBlock.isAir(world, toMakeFirePos) || toMakeFireBlock.isReplaceable(world, toMakeFirePos)) {
				world.setBlockState(toMakeFirePos, Blocks.fire.getDefaultState());
			}
		}
	}
	
	public void endHit(World world, BlockPos pos) {
		
	}
	
}
