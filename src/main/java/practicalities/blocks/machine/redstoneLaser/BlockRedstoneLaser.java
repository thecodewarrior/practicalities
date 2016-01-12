package practicalities.blocks.machine.redstoneLaser;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import practicalities.lib.common.BlockTileBase;

public class BlockRedstoneLaser extends BlockTileBase {

	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockRedstoneLaser() {
		super(Material.glass, "redstoneLaser", null);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, POWERED);
	}
	
	@Override
	public List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops,
			boolean simulate) {
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRedstoneLaser();
	}

}
