package practicalities.lib.util.track;

import java.util.HashMap;
import java.util.Map;

import practicalities.network.MessageTracker;
import practicalities.network.NetHandler;

public class TrackerServer extends Tracker {

	Map<String, String> tracks = new HashMap<>();
	
	@Override
	public void clear() {
		tracks.clear();
	}

	@Override
	public void track(String key, String value) {
		if(!value.equals(tracks.get(key))) {
			NetHandler.network.sendToAll(new MessageTracker(key, value));
		}
		tracks.put(key, value);
	}

}
