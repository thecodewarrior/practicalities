package practicalities.lib.util.track;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import practicalities.ConfigMan;

public class TrackerManager {

	public static TrackerServer server;
	public static TrackerClient client;
	
	public void init() {
		if(ConfigMan.isDev) {
			server = new TrackerServer();
			client = new TrackerClient();
			
			MinecraftForge.EVENT_BUS.register(this);
			MinecraftForge.EVENT_BUS.register(client);
		}
	}
	
	int timer = 20;
	
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent evt) {
		if(evt.phase == Phase.END) {
			if(timer == 0) {
				timer = 100;
				client.clear();
				server.clear();
			}
			timer--;
		}
	}
	
}
