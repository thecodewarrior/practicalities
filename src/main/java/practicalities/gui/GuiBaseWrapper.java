package practicalities.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import cofh.lib.gui.GuiBase;

public class GuiBaseWrapper extends GuiBase {

	GuiScreenBase gui;
	
	public GuiBaseWrapper(GuiScreenBase gui) {
		super(new Container() { public boolean canInteractWith(EntityPlayer p) { return false; } });
		this.gui = gui;
	}

	
}
