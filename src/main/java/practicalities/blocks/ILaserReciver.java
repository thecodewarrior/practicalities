package practicalities.blocks;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface ILaserReciver {

	public void laserHit(World world, BlockPos pos, EnumFacing sideHit, BlockPos laserPos, boolean powered);
	
}
