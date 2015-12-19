package practicalities.gui.book.element;

import net.minecraft.util.ResourceLocation;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.element.ElementScreenBase;

public class ElementIcon extends ElementScreenBase {

	int u, v;
	ResourceLocation tex;
	
	public ElementIcon(GuiScreenBase gui, int x, int y, int w, int h, int u, int v, ResourceLocation tex) {
		super(gui, x, y, w, h);
		this.u = u; this.v = v; this.tex = tex;
	}

	@Override
	public void drawBackground(int paramInt1, int paramInt2, float paramFloat) {
		gui.bindTexture(tex);
		gui.drawTexturedModalRect(posX, posY, u, v, sizeX, sizeY);
		gui.bindTexture(gui.texture);
	}

	@Override
	public void drawForeground(int paramInt1, int paramInt2) {
		
	}

}
