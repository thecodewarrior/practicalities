package practicalities.blocks.machine.redstoneLaser;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface ILaserListener {

	public void laserHit(World world, BlockPos pos, EnumFacing side);
	public void laserStopHit(World world, BlockPos pos, EnumFacing side);
	
}
