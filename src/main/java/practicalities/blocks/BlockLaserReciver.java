package practicalities.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockLaserReciver extends BlockBase implements ILaserReciver {

	public static final PropertyDirection ATTACHED = PropertyDirection.create("attached");
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockLaserReciver() {
		super(Material.glass, "laserReciver");
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, POWERED, ATTACHED);
	}

	@Override
	public void laserHit(World world, BlockPos pos, EnumFacing sideHit, BlockPos laserPos, boolean powered) {
		// TODO Auto-generated method stub

	}

}
