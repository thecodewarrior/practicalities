package practicalities.helpers.fakeplayer;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.WorldServer;

public class PRFakePlayerFactory {
	private static GameProfile PR = new GameProfile(UUID.randomUUID(), "[PracticalitiesFakePlayer]");
	private static WeakReference<PRFakePlayer> PR_PLAYER = new WeakReference<PRFakePlayer>(null);

	public static PRFakePlayer getPlayer(WorldServer world)
	{
		if (PR_PLAYER == null || PR_PLAYER.get() == null)
		{
			PR_PLAYER = new WeakReference<PRFakePlayer>( new PRFakePlayer(world, PR) );
		}
		return PR_PLAYER.get();
	}
}
