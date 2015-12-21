package practicalities.items;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import practicalities.PracticalitiesMod;

public class ItemCraftingPiece extends ItemBase {
	public TMap<String, SubItem> subItemMap = new THashMap<String, SubItem>();
	public ArrayList<SubItem> subItemList = new ArrayList<SubItem>();

	public class SubItem {

		public String name;
		public int meta;

		public SubItem(String name, int meta) {

			this.name = name;
			this.meta = meta;
		}
	}

	public ItemCraftingPiece() {
		super("craftingPiece");
		this.setHasSubtypes(true);
	}

	public ItemStack addItem(String name, boolean oreDict) {

		int number = subItemList.size();

		if (subItemMap.containsKey(String.valueOf(name))) {
			return null;
		}

		SubItem entry = new SubItem(name, number);

		subItemMap.put(String.valueOf(name), entry);
		subItemList.add(entry);

		ItemStack item = new ItemStack(this, 1, number);

		if (oreDict) {

			OreDictionary.registerOre(entry.name, item);
		}

		return item;
	}

	public ItemStack addItem(String name) {

		return addItem(name, false);
	}

	public String getEntryName(ItemStack stack) {

		int meta = getDamage(stack);
		if (!subItemMap.containsKey(Integer.valueOf(meta))) {
			return "invalid";
		}
		return subItemMap.get(meta).name;
	}

	public ItemStack getSubItem(String name) {
		if (subItemMap.containsKey(name)) {
			int meta = subItemMap.get(name).meta;
			return new ItemStack(this, 1, meta);
		}
		return new ItemStack(Items.apple);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < subItemList.size(); i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		int meta = getDamage(stack);

		if (subItemList.size() <= meta) {
			return "item.invalid";
		}
		SubItem item = subItemList.get(meta);

		return "item." + item.name;// new
									// StringBuilder().append(getUnlocalizedName()).append('.').append(item.name).toString();
	}

}