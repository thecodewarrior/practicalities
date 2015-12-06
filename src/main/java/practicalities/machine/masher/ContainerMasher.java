package practicalities.machine.masher;

import cofh.lib.gui.slot.SlotAcceptValid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import practicalities.gui.ContainerBase;
import practicalities.gui.SlotRegion;

public class ContainerMasher extends ContainerBase {

	private SlotRegion food;
	
	public ContainerMasher(InventoryPlayer player, TileMasher tile) {
		bindPlayerInventory(player);
		
		Slot s = new SlotAcceptValid(tile.food, 0, 80, 28);
		addSlotToContainer(s);
		int index = s.slotNumber;
		
		food = new SlotRegion("foodSlot", new int[] { index });
		
		mainInv.addShiftTargets(food, hotbar);
		hotbar.addShiftTargets(food, mainInv);
		food.addShiftTargets(hotbar, mainInv);
		
		setShiftClickRegions(food, mainInv, hotbar);
	}
	
	@Override
	protected int getPlayerInventoryVerticalOffset() {
		return 84;
	}

	@Override
	protected int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
