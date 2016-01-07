package practicalities.blocks.machine.quantumInterface;

import java.util.List;

import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import practicalities.lib.common.BlockBase;
import practicalities.lib.common.BlockLocation;
import practicalities.lib.common.IPistonListener;
import practicalities.lib.common.PistonListenerHandler;
import practicalities.quantumNetwork.IQuantumBindable;
import practicalities.quantumNetwork.QuantumRegistry;

public class BlockQuantumInterface extends BlockBase implements IPistonListener, IQuantumBindable {

	public static final PropertyBool NOTHING = PropertyBool.create("nothing");
	
	public BlockQuantumInterface() {
		super(Material.rock, "quantumInterface", null);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { NOTHING });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(NOTHING, (meta & 8) > 0);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(NOTHING) ? 8 : 0;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		PistonListenerHandler.breakBlock(world, pos, this);
	}
	
	@Override
	public List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops,
			boolean simulate) {
		return null;
	}

	@Override
	public void move(BlockLocation loc, EnumPistonType type, EnumFacing direction) {
		QuantumRegistry.moveInterfaceLocation(loc.getString(), new BlockLocation(loc.getWorld(), loc.getPos().offset(direction)).getString());
	}

	@Override
	public void destroy(BlockLocation loc) {
		QuantumRegistry.interfaceLinks.remove(loc.getString());
		QuantumRegistry.save(loc.getWorld());
	}

	@Override
	public void bind(BlockLocation loc, BlockLocation bind) {
		QuantumRegistry.interfaceLinks.put(loc.getString(), bind.getString());
		QuantumRegistry.save(loc.getWorld());
	}

}
