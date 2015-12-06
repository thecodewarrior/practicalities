package practicalities.machine.masher;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import practicalities.Getter;
import practicalities.PracticalitiesMod;
import practicalities.gui.ElementEnergyBar;
import practicalities.gui.ElementProgress;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementFluidTank;

public class GuiMasher extends GuiBase {

	TileMasher tile;
	
	public GuiMasher(InventoryPlayer inventory, TileMasher tile) {
		super(new ContainerMasher(inventory, tile));
		this.tile = tile;
		texture = new ResourceLocation(PracticalitiesMod.TEXTURE_BASE + "textures/gui/masher.png");
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		Getter<Double, TileMasher> getter = new Getter<Double, TileMasher>(tile) {
			public Double get() {
				return tile.getProgress();
			}
		};
		
		addElement(new ElementProgress(this,
				84, 46, 0, 166, 11, 11, // 79, 27, 18, 17, //
				84, 57, 0, 177, 11, 0, // 79, 44, 18, 0, 
				getter
			));
		
		
		
		
		ElementEnergyBar energy = new ElementEnergyBar(this, 7, 7, tile.energy, 16, 60);
		addElement(energy);
		
		ElementFluidTank tank = new ElementFluidTank(this, 153, 7, tile.tank);
		addElement(tank);
	}

}
