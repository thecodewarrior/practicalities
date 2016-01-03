package practicalities.lib.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

public class PistonListenerHandler {
	
	public static final PistonListenerHandler instance = new PistonListenerHandler();
	
	public static List<BlockLocation> blocksMoved = new ArrayList<>();
	public static Map<String, IPistonListener> blockListeners = new HashMap<>();
	
	public static void breakBlock(World world, BlockPos pos, IPistonListener listener) {
		BlockLocation loc = new BlockLocation(world, pos);
		loc.data = 0;
		blocksMoved.add(loc);
		blockListeners.put(loc.getString(), listener);
	}
	
	@SubscribeEvent
	public void tickEnd(TickEvent event) {
		if(event.type == Type.SERVER && event.phase == Phase.END) {
			for(BlockLocation loc : blocksMoved) {
				if(--loc.data <= 0) {
					processBlockMovement(loc, blockListeners.get(loc.getString()));
				}
			}
			List<BlockLocation> blocksCopy = new ArrayList<>(blocksMoved);
			for(BlockLocation loc : blocksCopy) {
				if(loc.data <= 0) {
					blocksMoved.remove(loc);
					blockListeners.remove(loc.getString());
				}
			}
		}
	}
	
	public void processBlockMovement(BlockLocation loc, IPistonListener listener) {
		World world = loc.getWorld();
		IBlockState state = world.getBlockState(loc.getPos());
		
		if( state.getBlock() == Blocks.piston_extension ) {
			EnumFacing dir = state.getValue(BlockPistonExtension.FACING);
			BlockPistonExtension.EnumPistonType type = state.getValue(BlockPistonExtension.TYPE);
			
			listener.move(loc, type, dir);
		} else {
			listener.destroy(loc);
		}
	}
}
