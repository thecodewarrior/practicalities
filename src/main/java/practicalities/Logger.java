package practicalities;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;

public class Logger {

	public static void log(Level level, String format, Object... data) {

		FMLLog.log(level, "[@Practicalities] " + format, data);
	}

	public static void fatal(String format, Object... data) {
		log(Level.FATAL, format, data);
	}

	public static void error(String format, Object... data) {
		log(Level.ERROR, format, data);
	}

	public static void warning(String format, Object... data) {
		log(Level.WARN, format, data);
	}

	public static void info(String format, Object... data) {
		log(Level.INFO, format, data);
	}

	public static void debug(String format, Object... data) {
		log(Level.DEBUG, format, data);
	}

	public static void trace(String format, Object... data) {
		log(Level.TRACE, format, data);
	}
	
	public static void track(String name, String format, Object... data) {
		if(!ConfigMan.isDev)
			throw new UnsupportedOperationException("Practicalities: Tried to call Logger.track outside of a dev environment! This is just plain bad!");
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			PracticalitiesMod.proxy.trackerServer.track(name, String.format(format, data));
		} else {
			PracticalitiesMod.proxy.trackerClient.track(name, String.format(format, data));
		}
	}
}
