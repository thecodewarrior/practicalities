package practicalities.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import practicalities.ConfigMan;
import practicalities.fluid.prechewedfood.BlockPreChewedFood;
import practicalities.machine.inventoryfilter.BlockInventoryFilter;
import practicalities.machine.masher.BlockMasher;
import practicalities.machine.playerinterface.BlockPlayerInterface;
import practicalities.machine.polaritynegator.BlockPolarityNegator;
import practicalities.machine.shippingcrate.BlockShippingCrate;
import practicalities.machine.vampiricgenerator.BlockVampiricGenerator;

public class ModBlocks {
	public static Block shippingCrate;
	public static Block stoneWall;
	public static Block vampiricGenerator;
	public static Block polarityNegator;
	public static Block inventoryFilter;
	public static Block masher;
	public static Block blockPreChewedFood;
	public static Fluid preChewedFood;
	public static Block playerInterface;
	
	public static void init() {
		
		preChewedFood = new Fluid("preChewedFood");
		preChewedFood.setDensity(2000).setTemperature(310).setViscosity(15000);
		FluidRegistry.registerFluid(preChewedFood);
		
		stoneWall = new BlockDecor(Material.rock, "stonewall", 1, Block.soundTypeStone);
		if(ConfigMan.enablePlayerInterface)
			playerInterface = new BlockPlayerInterface();
		
		if(ConfigMan.enablePreChewedFood) {
			blockPreChewedFood = new BlockPreChewedFood(preChewedFood);
			masher = new BlockMasher();
		}
		
		if(ConfigMan.enableInventoryFilter)
			inventoryFilter = new BlockInventoryFilter();
		
		if(ConfigMan.enableShippingCrate)
			shippingCrate = new BlockShippingCrate();
		
		if(ConfigMan.enableVampiricGenerator)
			vampiricGenerator = new BlockVampiricGenerator();
		
		if(ConfigMan.enableMagnet)
			polarityNegator = new BlockPolarityNegator();
	}
	
	public static void postInit() {
		
	}
}
