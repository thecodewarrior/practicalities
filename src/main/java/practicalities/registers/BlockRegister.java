package practicalities.registers;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.Logger;
import practicalities.blocks.BlockBase;
import practicalities.blocks.BlockLaser;
import practicalities.blocks.TileLaser;
import practicalities.client.render.RenderLaser;
import practicalities.machines.shippingcrate.BlockShippingCrate;
import practicalities.machines.shippingcrate.TileShippingCrate;

public final class BlockRegister {
	
	public static BlockBase shippingcrate;
	public static BlockBase laser;
	public static BlockBase laserActivator;
	
	@SideOnly(Side.CLIENT)
	public static TileEntitySpecialRenderer<TileLaser> laserRender;
	
	private static void initTileEntities(){
		Logger.info("Registering TileEntities");
		TileShippingCrate.initialize();
		TileLaser.initialize();
	}
	
	public static void init(){
		shippingcrate = new BlockShippingCrate();
		laser = new BlockLaser();
		
		initTileEntities();
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenders(){
		Logger.info("Registering Block Renderers");
		
		shippingcrate.initModel();
		laser.initModel();
		laserRender = new RenderLaser();
		ClientRegistry.bindTileEntitySpecialRenderer(TileLaser.class, laserRender);
	}

}
