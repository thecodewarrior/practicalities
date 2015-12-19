package practicalities.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import practicalities.PracticalitiesMod;
import practicalities.gui.GuiHandler;
import practicalities.gui.book.GuiGuide;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemGuide extends Item implements IItemGui {

	public ItemGuide() {
		String name = "guide";
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(name);
		setCreativeTab(PracticalitiesMod.tab);
		setTextureName(PracticalitiesMod.TEXTURE_BASE + name);
		setMaxStackSize(1);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
		openGui(world, player);
		return item;
	}

	@Override
	public void openGui(World world, EntityPlayer entityplayer) {
		if(!world.isRemote)
			return;
		
		entityplayer.openGui(PracticalitiesMod.instance, GuiHandler.ITEM_ID, world, (int) entityplayer.posX,
				(int) entityplayer.posY, (int) entityplayer.posZ);

	}

	@Override
	public Object getGuiClient(ItemStack item, InventoryPlayer inventory) {
		return new GuiGuide();
	}

	@Override
	public Object getGuiServer(ItemStack item, InventoryPlayer inventory) {
		return null;//new Container() { public boolean canInteractWith(EntityPlayer p_75145_1_) { return true; } };
	}
	
}
