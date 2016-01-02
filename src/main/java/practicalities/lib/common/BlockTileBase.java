package practicalities.lib.common;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTileBase extends BlockBase implements ITileEntityProvider {
	
	public BlockTileBase(Material material, String name, Class<? extends ItemBlock> itemClass) {
		super(material, name, itemClass);
	}

	@Override
	public abstract List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops, boolean simulate);
	
	@Override
	public abstract TileEntity createNewTileEntity(World worldIn, int meta);

}
