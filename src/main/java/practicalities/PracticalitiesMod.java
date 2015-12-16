package practicalities;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import practicalities.gui.GuiHandler;
import practicalities.machine.playerinterface.TilePlayerInterface;
import practicalities.machine.fieldrepeater.TileFieldRepeater;
import practicalities.machine.inductioncoil.TileInductionCoil;
import practicalities.machine.inventoryfilter.TileInventoryFilter;
import practicalities.machine.shippingcrate.TileShippingCrate;
import practicalities.machine.teslacoil.TileTeslaCoil;
import practicalities.machine.vampiricgenerator.TileVampiricGenerator;
import practicalities.network.Proxy;
import practicalities.register.ModItems;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = PracticalitiesMod.MODID, version = PracticalitiesMod.VERSION)
public class PracticalitiesMod {
	public static final String MODID = "practicalities";
	public static final String VERSION = "2.0.0-a5";
	public static final String TEXTURE_BASE = "practicalities:";
	private boolean initializedMachines;

	@Instance(PracticalitiesMod.MODID)
	public static PracticalitiesMod instance;
	public static final GuiHandler guiHandler = new GuiHandler();

	@SidedProxy(clientSide = "practicalities.network.ProxyClient", serverSide = "practicalities.network.Proxy")
	public static Proxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigMan.init(new Configuration(event.getSuggestedConfigurationFile()));

		proxy.preInit();
		initMachines();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		new CraftingRecipes().init();
		proxy.registerKeyBinds();
		proxy.registerRenderInformation();
		proxy.registerTickHandlers();
		proxy.registerPacketInformation();
	}

	private void initMachines() {
		if (!initializedMachines) {
			TileShippingCrate.initialize();
			TilePlayerInterface.initialize();
			TileVampiricGenerator.initialize();
			TileInventoryFilter.initialize();
			TileTeslaCoil.initialize();
			TileInductionCoil.initialize();
			TileFieldRepeater.initialize();
			initializedMachines = true;
		}
	}

	public static CreativeTabs tab = new CreativeTabs("tabPracticalities") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return ModItems.machineCore;
		}
	};

}