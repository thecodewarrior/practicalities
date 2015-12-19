package practicalities.gui.book.element;

import net.minecraft.item.ItemStack;
import practicalities.gui.GuiHelper;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.element.ElementScreenBase;

public class ElementRecipe extends ElementScreenBase {
	
	CraftingMatrix matrix;
	int gap, resultX, resultY;
	
	public ElementRecipe(GuiScreenBase paramGuiBase, int paramInt1, int paramInt2, CraftingMatrix matrix, int gap, int resultX, int resultY) {
		super(paramGuiBase, paramInt1, paramInt2);
		this.matrix = matrix; this.gap = gap; this.resultX = resultX; this.resultY = resultY;
	}

	@Override
	public void drawBackground(int paramInt1, int paramInt2, float paramFloat) {
		
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		
		GuiHelper.instance().itemRenderSetup();
		
		int dist = (16+gap);
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				renderItem(posX+( dist*i ), posY+( dist*j ), matrix.items[i+(j*3)], mouseX, mouseY);
			}
		}
		
		renderItem(posX+resultX, posY+resultY, matrix.result, mouseX, mouseY);
		
		
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				tryTooltip(posX+( dist*i ), posY+( dist*j ), matrix.items[i+(j*3)], mouseX, mouseY);
			}
		}
		
		GuiHelper.instance().itemRenderCleanup();
		
		tryTooltip(posX+resultX, posY+resultY, matrix.result, mouseX, mouseY);
		
	}
	
	void tryTooltip(int x, int y, ItemStack stack, int mouseX, int mouseY) {
		if(	mouseX >= x && mouseX <= x+16 &&
			mouseY >= y && mouseY <= y+16) {
			GuiHelper.instance().drawItemTooltip(stack, mouseX, mouseY);
		}
	}
	
	void renderItem(int x, int y, ItemStack stack, int mouseX, int mouseY) {
		GuiHelper.instance().drawItemStack(stack, x, y);
	}

}
