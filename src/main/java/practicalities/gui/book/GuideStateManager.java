package practicalities.gui.book;

import net.minecraft.util.StatCollector;
import practicalities.gui.GuiScreenBase;

public class GuideStateManager {

	public GuidePage page;
	
	GuiScreenBase gui;
	int xSize, ySize;
	String currentPage;
	int currentPageNum;
	int maxPageNum;
	
	public GuideStateManager(GuiScreenBase gui, int xSize, int ySize) {
		this.gui = gui; this.xSize = xSize; this.ySize = ySize;
		goToPage("intro", 0);
	}
	
	public void goToPage(String name, int number) {
		if(currentPage == null || !currentPage.equals(name)) {
			this.maxPageNum = getMaxPageNum(name);
		}
		if(number > maxPageNum)
			number = maxPageNum;
		this.currentPage = name;
		this.currentPageNum = number;
		page = GuidePage.getPage(gui, this, name, number);
	}
	
	int getMaxPageNum(String name) {
		int i = 0;
		while(StatCollector.canTranslate("guide.entry."+name+".page."+i)) {
			i++;
		}
		i--;
		return i;
	}
	
	public void goToNext() {
		goToPage(currentPage, currentPageNum+1);
	}
	
	public void goToPrev() {
		goToPage(currentPage, currentPageNum-1);
	}
	
	public int getMaxPageNum() {
		return maxPageNum;
	}
}
