package practicalities.gui.book.page;

import net.minecraft.util.StatCollector;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.book.GuidePage;
import practicalities.gui.book.GuideStateManager;
import practicalities.gui.book.element.ElementButton;
import practicalities.gui.book.element.ElementTitle;

public class PageInEntry extends GuidePage {

	String title;
	int pageNum;
	
	public PageInEntry(String pageName, int page) {
		title = StatCollector.translateToLocal("guide.entry." + pageName + ".title");
		this.pageNum = page;
	}
	
	@Override
	public void init(GuiScreenBase gui, GuideStateManager state) {
		
		this.titleElements.add(new ElementTitle(gui, 0, 0, title));
		
		final int number = pageNum;
		final GuideStateManager guideState = state;
		
		this.navElements.add(new ElementButton(gui, -13, 1, 12, 9, 0, 206) {
			public void click() {
				guideState.goToPrev();
			}
			@Override
			public boolean isEnabled() {
				return number > 0;
			}
		});
		
		this.navElements.add(new ElementButton(gui, 1, 1, 12, 9, 12, 206) {
			public void click() {
				guideState.goToNext();
			}
			@Override
			public boolean isEnabled() {
				return number < guideState.getMaxPageNum();
			}
		});
	}

}
