package practicalities.gui.book.page;

import net.minecraft.util.StatCollector;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.book.GuiGuide;
import practicalities.gui.book.GuideStateManager;
import practicalities.gui.book.element.ElementText;

public class PageText extends PageInEntry {

	String text;
	
	public PageText(String pageName, int page) {
		super(pageName, page);
		text = StatCollector.translateToLocal("guide.entry." + pageName + ".page." + page);
	}
	
	@Override
	public void init(GuiScreenBase gui, GuideStateManager state) {
		super.init(gui, state);
		this.mainElements.add(new ElementText(gui, 0, 0, GuiGuide.MAIN_SIZE_X, GuiGuide.MAIN_SIZE_Y, text));
	}

}
