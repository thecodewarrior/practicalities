package practicalities.gui.element;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import practicalities.gui.GuiScreenBase;
import cofh.lib.gui.GuiBase;

public abstract class ElementScreenBase {
	protected GuiScreenBase gui;
	protected ResourceLocation texture;
	private FontRenderer fontRenderer;
	protected int posX;
	protected int posY;
	protected int sizeX;
	protected int sizeY;
	protected int texW = 256;
	protected int texH = 256;
	protected String name;
	private boolean visible = true;
	private boolean enabled = true;

	public ElementScreenBase(GuiScreenBase paramGuiBase, int paramInt1,
			int paramInt2) {
		this.gui = paramGuiBase;
		this.posX = paramInt1;
		this.posY = paramInt2;
	}

	public ElementScreenBase(GuiScreenBase paramGuiBase, int paramInt1,
			int paramInt2, int paramInt3, int paramInt4) {
		this.gui = paramGuiBase;
		this.posX = paramInt1;
		this.posY = paramInt2;
		this.sizeX = paramInt3;
		this.sizeY = paramInt4;
	}

	public ElementScreenBase setName(String paramString) {
		this.name = paramString;
		return this;
	}

	public ElementScreenBase setPosition(int paramInt1, int paramInt2) {
		this.posX = paramInt1;
		this.posY = paramInt2;
		return this;
	}

	public ElementScreenBase setSize(int paramInt1, int paramInt2) {
		this.sizeX = paramInt1;
		this.sizeY = paramInt2;
		return this;
	}

	public ElementScreenBase setTexture(String paramString, int paramInt1,
			int paramInt2) {
		this.texture = new ResourceLocation(paramString);
		this.texW = paramInt1;
		this.texH = paramInt2;
		return this;
	}

	public final ElementScreenBase setVisible(boolean paramBoolean) {
		this.visible = paramBoolean;
		return this;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public final ElementScreenBase setEnabled(boolean paramBoolean) {
		this.enabled = paramBoolean;
		return this;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void update(int paramInt1, int paramInt2) {
		update();
	}

	public void update() {
	}

	public abstract void drawBackground(int paramInt1, int paramInt2,
			float paramFloat);

	public abstract void drawForeground(int paramInt1, int paramInt2);

	public void addTooltip(List<String> paramList, int mouseX, int mouseY) {
	}

	public void drawModalRect(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4, int paramInt5) {
		this.gui.drawSizedModalRect(paramInt1, paramInt2, paramInt3, paramInt4,
				paramInt5);
	}

	public void drawStencil(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4, int paramInt5) {
		GL11.glDisable(3553);
		GL11.glStencilFunc(519, paramInt5, paramInt5);
		GL11.glStencilOp(0, 0, 7681);
		GL11.glStencilMask(1);
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);

		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.addVertex(paramInt1, paramInt4, 0.0D);
		Tessellator.instance.addVertex(paramInt3, paramInt4, 0.0D);
		Tessellator.instance.addVertex(paramInt3, paramInt2, 0.0D);
		Tessellator.instance.addVertex(paramInt1, paramInt2, 0.0D);
		Tessellator.instance.draw();

		GL11.glEnable(3553);
		GL11.glStencilFunc(514, paramInt5, paramInt5);
		GL11.glStencilMask(0);
		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
	}

	public void drawTexturedModalRect(int paramInt1, int paramInt2,
			int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
		this.gui.drawSizedTexturedModalRect(paramInt1, paramInt2, paramInt3,
				paramInt4, paramInt5, paramInt6, this.texW, this.texH);
	}

	public void drawCenteredString(FontRenderer paramFontRenderer,
			String paramString, int paramInt1, int paramInt2, int paramInt3) {
		paramFontRenderer.drawStringWithShadow(paramString, paramInt1
				- paramFontRenderer.getStringWidth(paramString) / 2, paramInt2,
				paramInt3);
	}

	public boolean onMousePressed(int paramInt1, int paramInt2, int paramInt3) {
		return false;
	}

	public void onMouseReleased(int paramInt1, int paramInt2) {
	}

	public boolean onMouseWheel(int paramInt1, int paramInt2, int paramInt3) {
		return false;
	}

	public boolean onKeyTyped(char paramChar, int paramInt) {
		return false;
	}

	public boolean intersectsWith(int paramInt1, int paramInt2) {
		if ((paramInt1 >= this.posX) && (paramInt1 <= this.posX + this.sizeX)
				&& (paramInt2 >= this.posY)
				&& (paramInt2 <= this.posY + this.sizeY)) {
			return true;
		}
		return false;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer == null ? this.gui.getFontRenderer()
				: this.fontRenderer;
	}

	public ElementScreenBase setFontRenderer(FontRenderer paramFontRenderer) {
		this.fontRenderer = paramFontRenderer;
		return this;
	}

	public final String getName() {
		return this.name;
	}

	public final GuiScreenBase getScreen() {
		return this.gui;
	}

	public final int getPosY() {
		return this.posY;
	}

	public final int getPosX() {
		return this.posX;
	}

	public final int getHeight() {
		return this.sizeY;
	}

	public final int getWidth() {
		return this.sizeX;
	}
}
