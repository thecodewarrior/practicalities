package practicalities.lib.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class PistonHelper {
	public static boolean push(World worldIn, BlockPos pos, EnumFacing direction)
    {
		pos = pos.offset(direction, -1);
        BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(worldIn, pos, direction, true);
        List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
        List<BlockPos> list1 = blockpistonstructurehelper.getBlocksToDestroy();

        if (!blockpistonstructurehelper.canMove())
        {
            return false;
        }
        else
        {
            for (int j = list1.size() - 1; j >= 0; --j)
            {
                BlockPos blockpos = (BlockPos)list1.get(j);
                Block block = worldIn.getBlockState(blockpos).getBlock();
                //With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                float chance = block instanceof BlockSnow ? -1.0f : 1.0f;
                block.dropBlockAsItemWithChance(worldIn, blockpos, worldIn.getBlockState(blockpos), chance, 0);
                worldIn.setBlockToAir(blockpos);
            }

            for (int k = list.size() - 1; k >= 0; --k)
            {
            	BlockPos toMove = list.get(k);
            	IBlockState toMoveState = worldIn.getBlockState(toMove);
            	worldIn.setBlockState(toMove, Blocks.air.getDefaultState());
            	worldIn.setBlockState(toMove.offset(direction), toMoveState);
            }

            return true;
        }
    }
}
