package practicalities.registers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.Logger;
import practicalities.PracticalitiesMod;
import practicalities.blocks.machine.quantumBinding.BlockQuantumBinding;
import practicalities.lib.common.BlockBase;

public class BlockRegister {

	public static Block quantumBinding;
	
	public static void init() {
		BlockBase.currentTab = PracticalitiesMod.tab;
		quantumBinding = new BlockQuantumBinding();
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		Logger.info("Registering Item Renderers");
		for (BlockBase block : BlockRenderQueue.blocks) {
			block.registerBlockModel();
		}
	}
}
