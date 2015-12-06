package practicalities.gui;

import practicalities.PracticalitiesMod;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import cofh.api.energy.IEnergyStorage;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.MathHelper;

public class ElementEnergyBar extends ElementEnergyStored {
	
	public ElementEnergyBar(GuiBase gui, int x, int y, IEnergyStorage storage, int width, int height) {
		super(gui, x, y, storage);
		this.sizeX = width;
		this.sizeY = height;
		this.texW = 128;
		this.texH = 32;
		texture = new ResourceLocation(PracticalitiesMod.TEXTURE_BASE + "textures/gui/element/energy.png");
	}
	

	int getPixelsFull()
	{
		if (this.storage.getMaxEnergyStored() <= 0) {
			return 0;
		}
		double percentage = this.storage.getEnergyStored() / (double)this.storage.getMaxEnergyStored();
		int pixels = (int)( this.sizeY * percentage );
		
		return (this.alwaysShowMinimum) && (this.storage.getEnergyStored() > 0) ? Math.max(1, MathHelper.round(pixels)) : MathHelper.round(pixels);
	}
	
	public void drawRect(int x, int y, int w, int h, double u, double v, double texW, double texH) {
		Tessellator localTessellator = Tessellator.instance;
		localTessellator.startDrawingQuads();
		localTessellator.addVertexWithUV(x+0, y+h, 10, u,      v+texH);
		localTessellator.addVertexWithUV(x+w, y+h, 10, u+texW, v+texH);
		localTessellator.addVertexWithUV(x+w, y+0, 10, u+texW, v     );
		localTessellator.addVertexWithUV(x+0, y+0, 10, u,      v     );
		localTessellator.draw();
	}
	
	public void drawBackground(int paramInt1, int paramInt2, float paramFloat) {
		int i = getScaled();
		
		RenderHelper.bindTexture(this.texture);
		
		int bottom = this.posY + this.sizeY;
		
		drawRect(this.posX, this.posY, this.sizeX, this.sizeY,
				0,   0, 0.5, 1);
		
		drawRect(this.posX, bottom -i, this.sizeX, i,
				0.5, 0, 0.5, 1);
	}

}
