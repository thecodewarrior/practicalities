package practicalities.gui.book.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import practicalities.Logger;
import practicalities.PracticalitiesMod;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.ItemHelper;
import practicalities.gui.book.GuiGuide;
import practicalities.gui.book.GuideStateManager;
import practicalities.gui.book.element.CraftingMatrix;
import practicalities.gui.book.element.ElementIcon;
import practicalities.gui.book.element.ElementRecipe;
import practicalities.gui.book.element.ElementText;

public class PageRecipe extends PageInEntry {

	ResourceLocation tex = new ResourceLocation(PracticalitiesMod.TEXTURE_BASE + "textures/gui/guide_recipe.png");
	
	CraftingMatrix matrix = new CraftingMatrix();
	String text = "";
	
	public PageRecipe(String pageName, int page) {
		super(pageName, page);
		
		String prefix = "guide.entry." + pageName + ".page." + page;
		
		String recipe = StatCollector.translateToLocal(prefix);
		text = StatCollector.translateToLocal(prefix + ".text");
		
		String[] itemTexts = recipe.substring(1, recipe.length()-1).split("\\]\\[");
		
		ItemStack[] items = new ItemStack[itemTexts.length];
		
		for(int i = 0; i < itemTexts.length; i++) {
			String itemText = itemTexts[i];
			if(!itemText.equals(""))
				items[i] = ItemHelper.instance().parseItemStack(itemText);
		}
		
		matrix.result = items[items.length-1];
		if(items.length-1 == 9) {
			matrix.items[0] = items[0];
			matrix.items[1] = items[1];
			matrix.items[2] = items[2];
			
			matrix.items[3] = items[3];
			matrix.items[4] = items[4];
			matrix.items[5] = items[5];
			
			matrix.items[6] = items[6];
			matrix.items[7] = items[7];
			matrix.items[8] = items[8];
		} else if(items.length-1 == 4) {
			matrix.items[0] = items[0];
			matrix.items[1] = items[1];
			
			matrix.items[3] = items[2];
			matrix.items[4] = items[3];
		} else if(items.length-1 == 1) {
			matrix.items[4] = items[0];
		} else {
			Logger.warning("INVALID RECIPE LENGTH! Length: %d", items.length-1);
		}
		
	}
	
	@Override
	public void init(GuiScreenBase gui, GuideStateManager state) {
		super.init(gui, state);	
		mainElements.add(new ElementIcon(gui, 0, 0, GuiGuide.MAIN_SIZE_X, GuiGuide.MAIN_SIZE_Y, 0, 0, tex));
		
		mainElements.add(new ElementRecipe(gui, 59, 50, matrix, 2, 56, 18));
		
		mainElements.add(new ElementText(gui, 3, 110, GuiGuide.MAIN_SIZE_X-6, GuiGuide.MAIN_SIZE_Y-110, text));
	}

}
