package practicalities.helpers.fakeplayer;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

public class PRFakePlayer extends FakePlayer {

	public PRFakePlayer(WorldServer world, GameProfile name) {
		super(world, name);
	}

}
