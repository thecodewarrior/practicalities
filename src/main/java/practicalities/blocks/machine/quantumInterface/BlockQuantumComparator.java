package practicalities.blocks.machine.quantumInterface;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import practicalities.lib.common.BlockFacingBase;
import practicalities.lib.common.BlockLocation;
import practicalities.quantumNetwork.QuantumRegistry;

public class BlockQuantumComparator extends BlockFacingBase {

    public static final PropertyBool NOTHING = PropertyBool.create("nothing");
	
	public BlockQuantumComparator() {
		super(Material.rock, "quantumComparator", null);
	}
	
	@Override
	public List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops,
			boolean simulate) {
		return null;
	}
	
	@Override
	public void setupStates(IBlockState state) {
		state.withProperty(NOTHING, Boolean.FALSE);
		super.setupStates(state);
	}
	
	@Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { FACING, NOTHING });
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(NOTHING, Boolean.valueOf((meta & 8) > 0));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | ( state.getValue(NOTHING) ? 8 : 0);
	}
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public int getComparatorInputOverride(World ourWorldIn, BlockPos ourPos) {
		
		EnumFacing facing = ourWorldIn.getBlockState(ourPos).getValue(BlockFacingBase.FACING);
		
		String interfaceLoc = new BlockLocation((World)ourWorldIn, ourPos.offset(facing)).getString();
		String link = QuantumRegistry.interfaceLinks.get(interfaceLoc);
		if(link == null) return 0;
		BlockLocation bound = new BlockLocation( link );
		
		World world = bound.getWorld();
		BlockPos pos = bound.getPos();
		
		IBlockState boundState = world.getBlockState(pos.offset(facing));
		IBlockState compareState = ourWorldIn.getBlockState(ourPos.offset(facing, -1));
		
		boolean blocksEqual = boundState.getBlock() == compareState.getBlock();
		boolean statesEqual = true;
		boolean nbtEqual = false;
		
		Collection<IProperty> boundSet   = boundState  .getPropertyNames();
		Collection<IProperty> compareSet = compareState.getPropertyNames();
		
		Set<IProperty> propertiesToCheck = new HashSet<IProperty>(boundState.getPropertyNames());
					   propertiesToCheck.addAll(compareState.getPropertyNames());
		
		for (IProperty property : propertiesToCheck) {
			if(boundSet.contains(property) && compareSet.contains(property)) {
				if(boundState.getProperties().get(property) != compareState.getProperties().get(property)) {
					statesEqual = false;
					break;
				}
			} else {
				statesEqual = false;
				break;
			}
		}
		
		if(boundState.getBlock().hasTileEntity(boundState) && compareState.getBlock().hasTileEntity(boundState)) {
			NBTTagCompound boundTag = new NBTTagCompound();
			NBTTagCompound compareTag = new NBTTagCompound();
			world.getTileEntity(pos.offset(facing)).writeToNBT(boundTag);
			ourWorldIn.getTileEntity(ourPos.offset(facing, -1)).writeToNBT(compareTag);
			
			boundTag.removeTag("x");
			boundTag.removeTag("y");
			boundTag.removeTag("z");
			
			compareTag.removeTag("x");
			compareTag.removeTag("y");
			compareTag.removeTag("z");

			if(boundTag.equals(compareTag))
				nbtEqual = true;
		}
		
		int out = 0;
		if(blocksEqual) {
			out += 1;
			if(statesEqual) {
				out += 1;
				if(nbtEqual) {
					out += 1;
				}
			}
		}
		
		return out;
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
//		world.notifyNeighborsOfStateChange(pos, this);
		world.setBlockState(pos, state.cycleProperty(NOTHING));
//		world.
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public int isProvidingWeakPower(IBlockAccess ourWorldIn, BlockPos ourPos, IBlockState state, EnumFacing side) {
		if(!( ourWorldIn instanceof World)) {
			return 0;
		}
		
		EnumFacing facing = ourWorldIn.getBlockState(ourPos).getValue(BlockFacingBase.FACING);
		
		String interfaceLoc = new BlockLocation((World)ourWorldIn, ourPos.offset(facing)).getString();
		String boundLocString = QuantumRegistry.interfaceLinks.get(interfaceLoc);
		if(boundLocString == null)
			return 0;
		BlockLocation bound = new BlockLocation( boundLocString );
		
		World world = bound.getWorld();
		BlockPos pos = bound.getPos();
		
		IBlockState boundState = world.getBlockState(pos.offset(facing));
		IBlockState compareState = ourWorldIn.getBlockState(ourPos.offset(facing, -1));
		
		boolean blocksEqual = boundState.getBlock() == compareState.getBlock();
		
		return blocksEqual ? 15 : 0;
	}

}
