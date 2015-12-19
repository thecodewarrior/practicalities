package practicalities.gui.book.element;

import practicalities.gui.GuiScreenBase;
import practicalities.gui.element.ElementScreenBase;

public class ElementTitle extends ElementScreenBase {

	String title;
	int textWidth;
	boolean initialized = false;
	
	public ElementTitle(GuiScreenBase paramGuiBase, int paramInt1, int paramInt2, String title) {
		super(paramGuiBase, paramInt1, paramInt2);
		this.title = title;
	}

	public void init() {
		if(!initialized) {
			
			this.textWidth = this.gui.fontRendererObj.getStringWidth(title);
			this.posX -= textWidth/2;
			this.posY -= this.gui.fontRendererObj.FONT_HEIGHT+4;
			
			initialized = true;
		}
	}
	
	@Override
	public void drawBackground(int paramInt1, int paramInt2, float paramFloat) {
		init();
		this.gui.drawTexturedModalRect(posX-7, posY-3, 0, 193, 7, 13);
		this.gui.drawTexturedModalRect(posX, posY-3, 7, 193, textWidth, 13);
		this.gui.drawTexturedModalRect(posX+textWidth, posY-3, 162, 193, 7, 13);
	}

	@Override
	public void drawForeground(int paramInt1, int paramInt2) {
		init();
		this.gui.fontRendererObj.drawString(title, posX, posY, 0x000000);
	}

}
