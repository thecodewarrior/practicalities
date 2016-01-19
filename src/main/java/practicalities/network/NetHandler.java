package practicalities.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import practicalities.ConfigMan;
import practicalities.network.message.MessageSyncLasers;
import practicalities.network.message.MessageTracker;

public class NetHandler {
	public static SimpleNetworkWrapper network;
	
	public static void init() {
		int i = 0;
		network = new SimpleNetworkWrapper("Practicalities");
		if(ConfigMan.isDev) {
			network.registerMessage(MessageTracker.class, MessageTracker.class, 200, Side.CLIENT);
		}
		network.registerMessage(MessageSyncLasers.class, MessageSyncLasers.class, i++, Side.CLIENT);
	}
}
