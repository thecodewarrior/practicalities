package practicalities.blocks;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import practicalities.entity.EntityLaser;

public interface ILaserReciver {

	public void laserHit(World world, BlockPos pos, EnumFacing sideHit, EntityLaser laser);
	
}
