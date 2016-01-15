package practicalities.lib.util.track;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrackerClient extends Tracker {

	Map<String, String> tracks = new HashMap<>();
	Map<String, String> packetTracks = new HashMap<>();
	boolean needToClear = true;
	
	@Override
	public void clear() {
		if(needToClear) {
			tracks.clear();
			packetTracks.clear();
		}
		needToClear = true;
	}

	@Override
	public void track(String key, String value) {
		if(needToClear) {
			tracks.clear();
			packetTracks.clear();
			needToClear = false;
		}
		tracks.put(key, value);
	}

	public void trackPacket(String key, String value) {
		if(needToClear) {
			tracks.clear();
			packetTracks.clear();
			needToClear = false;
		}
		packetTracks.put(key, value);
	}
	
	@SubscribeEvent
	public void onDebugText(RenderGameOverlayEvent.Text event) {
		if(!Minecraft.getMinecraft().gameSettings.showDebugInfo)
			return;
		Set<String> keys = new HashSet<>();
		keys.addAll(tracks.keySet());
		keys.addAll(packetTracks.keySet());
		
		event.left.add("KEY: §4SERVER §9CLIENT");
		for(String key : keys) {
			event.left.add(key + ": §4" + packetTracks.get(key) + " §9" + tracks.get(key));
		}
	}
}
