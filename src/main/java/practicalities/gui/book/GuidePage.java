package practicalities.gui.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.book.page.PageRecipe;
import practicalities.gui.book.page.PageText;
import practicalities.gui.element.ElementScreenBase;

public abstract class GuidePage {

	public List<ElementScreenBase> titleElements = new ArrayList<ElementScreenBase>();
	public List<ElementScreenBase> mainElements = new ArrayList<ElementScreenBase>();
	public List<ElementScreenBase> navElements = new ArrayList<ElementScreenBase>();
	
	public abstract void init(GuiScreenBase gui, GuideStateManager state);
	
	public static GuidePage getPage(GuiScreenBase gui, GuideStateManager state, String name, int pageNum) {
		String unlocalized = "guide.entry."+name+".page."+pageNum;
		String type = StatCollector.canTranslate(unlocalized+".type") ? StatCollector.translateToLocal(unlocalized+".type"):"text";
		type = type.toLowerCase();
		
		GuidePage page = null;
		if(type.equals("recipe")) {
			page = new PageRecipe(name, pageNum);
		}
		if(type.equals("text") || page == null) {
			page = new PageText(name, pageNum);
		}
		
		page.init(gui, state);
		
		return page;
	}
	
}
