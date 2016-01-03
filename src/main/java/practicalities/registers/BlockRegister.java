package practicalities.registers;

import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.Logger;
import practicalities.PracticalitiesMod;
import practicalities.blocks.machine.quantumBinding.BlockQuantumBinding;
import practicalities.blocks.machine.quantumInterface.BlockQuantumComparator;
import practicalities.blocks.machine.quantumInterface.BlockQuantumInterface;
import practicalities.lib.common.BlockBase;

public class BlockRegister {

	public static Block quantumBinding;
	public static Block quantumInterface;

	public static Block quantumComparator;

	public static void init() {
		BlockBase.currentTab = PracticalitiesMod.tab;
		quantumBinding = new BlockQuantumBinding();
		quantumInterface = new BlockQuantumInterface();
		quantumComparator = new BlockQuantumComparator();
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		Logger.info("Registering Item Renderers");
		for (BlockBase block : BlockRenderQueue.blocks) {
			block.registerBlockModel();
		}
	}
}
