package practicalities.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import practicalities.ConfigMan;

public class NetHandler {
	public static SimpleNetworkWrapper network;
	
	public static void init() {
		network = new SimpleNetworkWrapper("Practicalities");
		if(ConfigMan.isDev) {
			network.registerMessage(MessageTracker.class, MessageTracker.class, 200, Side.CLIENT);
		}
	}
}
