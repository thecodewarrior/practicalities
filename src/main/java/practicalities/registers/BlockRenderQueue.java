package practicalities.registers;

import java.util.ArrayList;

import practicalities.lib.common.BlockBase;

public class BlockRenderQueue {
	public static ArrayList<BlockBase> blocks = new ArrayList<BlockBase>();
	
	public static void add(BlockBase block) {
		blocks.add(block);
	}
}
