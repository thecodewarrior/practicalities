package practicalities.blocks.machine.quantumBinding;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import practicalities.blocks.machine.quantumInterface.BlockQuantumComparator;
import practicalities.lib.common.BlockBase;
import practicalities.lib.common.BlockLocation;
import practicalities.lib.common.IPistonListener;
import practicalities.lib.common.PistonHelper;
import practicalities.lib.common.PistonListenerHandler;
import practicalities.lib.util.Utils;
import practicalities.quantumNetwork.IQuantumBindable;
import practicalities.quantumNetwork.QuantumRegistry;
import practicalities.registers.BlockRegister;

public class BlockQuantumBinding extends BlockBase implements IPistonListener, IQuantumBindable {

	public BlockQuantumBinding() {
		super(Material.gourd, "quantumBinding", null);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops, boolean simulate) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		PistonListenerHandler.breakBlock(world, pos, this);
	}

	@Override
	public void move(BlockLocation loc, EnumPistonType type, EnumFacing direction) {
		String origPosStr = loc.getString();
		String newPosStr = new BlockLocation(loc.getWorld(), loc.getPos().offset(direction)).getString();
		
		World world = loc.getWorld();
		BlockPos pos = loc.getPos();
		BlockPos pushPos = pos.offset(direction);
		
		if(world.getBlockState(pushPos).getBlock().isAir(world, pushPos)) {
			world.setBlockState(pushPos, this.getDefaultState());
		} else {
			this.destroy(loc);
			this.dropBlockAsItem(world, pushPos, getDefaultState(), 0);
		}
		
		boolean contains = QuantumRegistry.links.containsKey(origPosStr);
		
		if(contains && QuantumRegistry.links.get(origPosStr) == null) {
			QuantumRegistry.links.remove(origPosStr);
			return;
		}
		
		boolean isSticky = type == EnumPistonType.STICKY;
		if( !isSticky && contains ) {
			
			String posStr = QuantumRegistry.links.get(loc.getString());
			World linkWorld = Utils.desterilizeWorld(posStr);
			BlockPos linkPos = Utils.desterilizePos(posStr);
			boolean couldMove = PistonHelper.push(linkWorld, linkPos, direction);
			if( couldMove ) {
				String newLinkPosStr = Utils.sterilizeLocation(linkWorld, linkPos.offset(direction));
				QuantumRegistry.links.put(origPosStr, newLinkPosStr);
				
				for(String searchPos : QuantumRegistry.interfaceLinks.keySet()) {
					if(QuantumRegistry.interfaceLinks.get(searchPos).equals(posStr)) {
						QuantumRegistry.interfaceLinks.put(searchPos, newLinkPosStr);
						
						BlockLocation interfaceLoc = new BlockLocation(searchPos);
						IBlockState state = interfaceLoc.getWorld().getBlockState(interfaceLoc.getPos());
						if(state.getBlock() == BlockRegister.quantumInterface) {
							interfaceLoc.getWorld().notifyNeighborsOfStateChange(interfaceLoc.getPos(), state.getBlock());
						}
						
						break;
					}
				}
			}
		}
		
		QuantumRegistry.moveLinkLocation(origPosStr, newPosStr);
		QuantumRegistry.save(loc.getWorld());
	}

	@Override
	public void destroy(BlockLocation loc) {
		QuantumRegistry.links.remove(loc.getString());
		QuantumRegistry.save(loc.getWorld());
	}

	@Override
	public void bind(BlockLocation loc, BlockLocation bind) {
		QuantumRegistry.links.put(loc.getString(), bind.getString());
		QuantumRegistry.save(loc.getWorld());
	}
}
