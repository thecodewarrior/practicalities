package practicalities.lib.client;

import net.minecraft.client.renderer.OpenGlHelper;

public class RenderUtils {

	public static void setBrightnessByBlockLight(int lightValue) {
		int i = lightValue << 20 | lightValue << 4;
        int j = i % 65536;
        int k = i / 65536;
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j/1.0F, k/1.0F);
	}
	
}
