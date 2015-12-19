package practicalities.gui.book;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import practicalities.PracticalitiesMod;
import practicalities.gui.GuiScreenBase;
import practicalities.gui.book.element.ElementProxy;
import practicalities.gui.element.ElementScreenBase;

public class GuiGuide extends GuiScreenBase {

	public static int MAIN_SIZE_X = 170;
	public static int MAIN_SIZE_Y = 193;
	
	public GuiGuide() {
		super();
		this.texture = new ResourceLocation(PracticalitiesMod.TEXTURE_BASE + "textures/gui/guide.png");
		this.xSize = MAIN_SIZE_X; this.ySize = MAIN_SIZE_Y;
		state = new GuideStateManager(this, MAIN_SIZE_X, MAIN_SIZE_Y);
	}
	
	ElementProxy mainContent, titleContent, navContent;
	
	GuideStateManager state;
	
	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - MAIN_SIZE_X) / 2;
        this.guiTop = (this.height - MAIN_SIZE_Y) / 2;
        
//        this.elements = new Composite
        
		mainContent = new ElementProxy(this, 0, 0, MAIN_SIZE_X, MAIN_SIZE_Y) {
			public List<ElementScreenBase> getWrapped() {
				return state.page.mainElements;
			}
		};
		addElement(mainContent);
		
		titleContent = new ElementProxy(this, MAIN_SIZE_X/2, 0, 0, 0) {
			public List<ElementScreenBase> getWrapped() {
				return state.page.titleElements;
			}
		};
		addElement(titleContent);
		
		navContent = new ElementProxy(this, MAIN_SIZE_X/2, MAIN_SIZE_Y, 0, 0) {
			public List<ElementScreenBase> getWrapped() {
				return state.page.navElements;
			}
		};
		addElement(navContent);
	}
}
