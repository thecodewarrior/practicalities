package practicalities.gui;

import net.minecraft.item.ItemStack;
import codechicken.lib.inventory.InventorySimple;

public abstract class InventorySingleStack<T> extends InventorySimple {

	protected String name;
	public T data;
	
	public InventorySingleStack(String name, T data) {
		super(1);
		this.name = name;
		this.data = data;
	}
	
	public ItemStack get() {
		return this.items[0];
	}

	public void set(ItemStack stack) {
		this.items[0] = stack;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return stack != null && stack.getItem() != null && validate(stack);
	}
	
	public abstract boolean validate(ItemStack stack);
	public String getInventoryName() {
		return name;
	}
	
}
