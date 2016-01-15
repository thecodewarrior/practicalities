package practicalities.lib.util.track;

public class TrackerRelease extends Tracker {

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Practicalities: Tried to call Tracker.clear outside of a dev environment! This is just plain bad!");
	}

	@Override
	public void track(String key, String value) {
		throw new UnsupportedOperationException("Practicalities: Tried to call Tracker.track outside of a dev environment! This is just plain bad!");
	}

}
