package practicalities.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.input.Keyboard;

import practicalities.ConfigMan;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy{
	
	private KeyBinding magnetToggle = new KeyBinding("key.practicalities.magnet_toggle", Keyboard.KEY_M, "key.categories.gameplay");
	
	@Override
	public boolean isClient() {

		return true;
	}

	@Override
	public boolean isServer() {

		return false;
	}
	
	@Override
	public void registerKeyBinds() {
		ClientRegistry.registerKeyBinding(magnetToggle);
	}
	
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(magnetToggle.isPressed()) {
        	Net.channel.sendToServer(new ToggleMagnetPacket());
        }
    }
	
	@SubscribeEvent
	public void drawDebug(RenderGameOverlayEvent.Text event) {
		if(!ConfigMan.isDev) return;
		if(!Minecraft.getMinecraft().gameSettings.showDebugInfo) return;
		
		event.left.add("Saturation:" + Minecraft.getMinecraft().thePlayer.getFoodStats().getSaturationLevel());
	}
}
