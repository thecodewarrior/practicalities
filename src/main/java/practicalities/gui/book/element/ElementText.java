package practicalities.gui.book.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;

import practicalities.Logger;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.element.ElementScreenBase;

public class ElementText extends ElementScreenBase {

	String text;
	ScaledResolution res;
	
	public ElementText(GuiScreenBase gui, int x, int y, int xSize, int ySize, String text) {
		super(gui, x, y, xSize, ySize);
		res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		this.text = escapeCodes(text, "n", "\n");
	}
	
	public String escapeCodes(String in, String code, String replace) {
		return in.replaceAll("(?<!\\\\)\\\\"+code, replace).replace("\\"+code, code);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
//		Logger.info("Scale: %d", res.);
		double s = 2.0/3.0;
		double S = 1/s;
		GL11.glScaled(s, s, s);
		this.getFontRenderer().drawSplitString(text, (int)((posX+3)*S), (int)((posY+3)*S), ((int)(sizeX*S))-6, 0x000000);
		GL11.glScaled(S, S, S);
	}

	@Override
	public void drawBackground(int paramInt1, int paramInt2, float paramFloat) {}

}
