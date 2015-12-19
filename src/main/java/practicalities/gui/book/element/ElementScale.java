package practicalities.gui.book.element;

import org.lwjgl.opengl.GL11;

import practicalities.gui.GuiScreenBase;
import practicalities.gui.element.ElementScreenBase;

public class ElementScale extends ElementScreenBase {

	double scale;
	
	public ElementScale(GuiScreenBase paramGuiBase, double scale) {
		super(paramGuiBase, 0, 0);
		this.scale = scale;
	}

	@Override
	public void drawBackground(int paramInt1, int paramInt2, float paramFloat) {
		GL11.glScaled(scale, scale, scale);
	}

	@Override
	public void drawForeground(int paramInt1, int paramInt2) {
		GL11.glScaled(scale, scale, scale);
	}

}
