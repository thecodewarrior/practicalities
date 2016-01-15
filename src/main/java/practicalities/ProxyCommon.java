package practicalities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.items.netherbane.EntityNetherbane;
import practicalities.lib.util.track.Tracker;
import practicalities.lib.util.track.TrackerClient;
import practicalities.lib.util.track.TrackerRelease;
import practicalities.lib.util.track.TrackerServer;
import practicalities.registers.BlockRegister;
import practicalities.registers.ItemRegister;
import practicalities.registers.RecipeRegister;

public class ProxyCommon {
	
	public void preInit() {
		ItemRegister.init();
		BlockRegister.init();
		if(!ConfigMan.isDev) {
			trackerClient = trackerServer = new TrackerRelease();
		}
	}

	public void init() {
		
	}

	public void postInit() {
		new RecipeRegister().init();
		registerTickHandlers();
	}

	public void registerKeyBindings() {

	}

	public void registerRenders() {

	}
	
	public void registerTickHandlers() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	/*Events*/
	
	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {
		if(event.entity.worldObj.isRemote) 
			return;
		
//		if (event.source == TileVampiricGenerator.vampiricDamage) {
//			event.setCanceled(true);
//		}
		
		if(event.entityLiving instanceof EntityPig){
			if(event.entity.worldObj.rand.nextDouble() <.0002){
				EntityItem hamCheese = new EntityItem(event.entityLiving.worldObj,
						event.entityLiving.posX,event.entityLiving.posY,event.entityLiving.posZ,
						new ItemStack(ItemRegister.hamCheese));
				hamCheese.dimension = event.entityLiving.dimension;
				
				event.drops.add(hamCheese);
				
			}
		}
	}
	
	@SubscribeEvent
	public void onItemToss(ItemTossEvent event) {
		if(event.entity.worldObj.isRemote) 
			return;
		
		if (event.entityItem.dimension == -1) {
			ItemStack itemTossed = event.entityItem.getEntityItem();
			if (itemTossed.getDisplayName().equals("Netherbane") && itemTossed.getItem().equals(Items.diamond_sword)) {
				event.entity.worldObj.spawnEntityInWorld(EntityNetherbane.convert(event.entityItem));
				event.setCanceled(true);
				
			}
		}
	}
	
	public Tracker trackerClient = new TrackerClient();
	public Tracker trackerServer = new TrackerServer();
	
	int timer = 20;
	
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent evt) {
		if(evt.phase == Phase.END) {
			if(timer == 0) {
				timer = 100;
				trackerClient.clear();
				trackerServer.clear();
			}
			timer--;
		}
	}
}
