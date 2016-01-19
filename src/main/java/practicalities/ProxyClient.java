package practicalities;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import practicalities.lib.util.AdvancedLangLoader;
import practicalities.registers.BlockRegister;
import practicalities.registers.GuideRegister;
import practicalities.registers.ItemRegister;

public class ProxyClient extends ProxyCommon implements IResourceManagerReloadListener {

	@Override
	public void preInit() {
		super.preInit();
		registerRenders();
		
	}
	
	@Override
	public void registerTickHandlers() {
		super.registerTickHandlers();
	}
	
	@Override
	public void init() {
		super.init();
		GuideRegister.init();
		( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener(this);
	}
	
	@Override
	public void registerRenders() {
		OBJLoader.instance.addDomain(PracticalitiesMod.MODID);
		ItemRegister.registerRenders();
		BlockRegister.registerRenders();
	}
	
//	@SubscribeEvent
//    public void onModelBakeEvent(ModelBakeEvent event) {
//        Object object =  event.modelRegistry.getObject(LaserModel.resourceLocation);
//        if (object != null) {
//            ExampleISBM customModel = new ExampleISBM();
//            event.modelRegistry.putObject(ExampleISBM.modelResourceLocation, customModel);
//        }
//    }

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		String lang = Minecraft.getMinecraft().gameSettings.language;
		InputStream stream = stream(PracticalitiesMod.TEXTURE_BASE + "guides/"+lang+".lang");
		if(stream == null) stream = stream(PracticalitiesMod.TEXTURE_BASE + "guides/en_US.lang");
		StringTranslate.inject(AdvancedLangLoader.parse(stream));
	}
	
	public InputStream stream(String resource) {
		try {
			return Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(resource)).getInputStream();
		} catch (IOException e) {
			return null;
		}
	}
}
