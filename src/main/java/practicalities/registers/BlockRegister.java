package practicalities.registers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.Logger;
import practicalities.PracticalitiesMod;
import practicalities.blocks.BlockLaserEmmiter;

public final class BlockRegister {
	
	public static Block laserEmitter;
	
	public static void init(){
		laserEmitter = new BlockLaserEmmiter();
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenders(){
		
		Logger.info("Registering Block Renderers");
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerRender(Block block, int meta) {
		
		Item item = Item.getItemFromBlock(block);
		String name = item.getUnlocalizedName().substring(5);
		String resourceLocation = PracticalitiesMod.MODID + ":" + name;
		
		Logger.info("    %s", name);

		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
				new ModelResourceLocation(resourceLocation, "inventory"));

	
	}

}
