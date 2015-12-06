package practicalities.gui;

import practicalities.Getter;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementBase;

public class ElementProgress extends ElementBase {

	public Getter<Double, ?> getter;
	
	public int zeroX, zeroY, zeroU, zeroV, zeroWidth, zeroHeight;
	public int fullX, fullY, fullU, fullV, fullWidth, fullHeight;

	public ElementProgress(GuiBase gui,
			int zeroX, int zeroY, int zeroU, int zeroV, int zeroWidth, int zeroHeight,
			int fullX, int fullY, int fullU, int fullV, int fullWidth, int fullHeight,
			Getter<Double, ?> getter) {
		
		super(gui, zeroX, zeroY);
		
		this.zeroX = zeroX; this.zeroY = zeroY; this.zeroU = zeroU; this.zeroV = zeroV; this.zeroWidth = zeroWidth; this.zeroHeight = zeroHeight;
		this.fullX = fullX; this.fullY = fullY; this.fullU = fullU; this.fullV = fullV; this.fullWidth = fullWidth; this.fullHeight = fullHeight;

		this.getter = getter;
	}

	@Override
	public void drawBackground(int paramInt1, int paramInt2, float paramFloat) {
		double p = getter.get();
		int x = animateBetween(zeroX, fullX, p);
		int y = animateBetween(zeroY, fullY, p);
		int u = animateBetween(zeroU, fullU, p);
		int v = animateBetween(zeroV, fullV, p);
		int w = animateBetween(zeroWidth, fullWidth, p);
		int h = animateBetween(zeroHeight, fullHeight, p);
		
		gui.drawTexturedModalRect(x, y, u, v, w, h);
		
	}

	@Override
	public void drawForeground(int paramInt1, int paramInt2) {}
	
	public int animateBetween(int zero, int one, double progress) {
		return (int) ( zero + (( one-zero )*progress) );
	}

}
