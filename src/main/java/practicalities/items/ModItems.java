package practicalities.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.EnumHelper;
import practicalities.ConfigMan;
import practicalities.PracticalitiesMod;
import practicalities.blocks.ModBlocks;
import practicalities.items.filtercard.ItemFilterCard;
import practicalities.items.netherbane.ItemNetherbane;

public class ModItems {
	public static final ToolMaterial imbuedToolMaterial =
			EnumHelper.addToolMaterial("imbued", 
					100, 10000, 6000, 10, 40);

	public static final ToolMaterial imbuedWeaponMaterial = 
			EnumHelper.addToolMaterial("imbued", 
					6,10000, 20, 40, 40);
	
	public static final ToolMaterial netherBaneMaterial =
			EnumHelper.addToolMaterial("netherBane",4, 5000, 10.0F, 4.0F, 16);

	// tools
	public static Item voidBucket;
	public static Item bucketPreChewedFood;
	
	public static Item matterTransporter;
	public static Item magnet;
	public static Item sitisStick;
	public static Item imbuedTool;
	public static Item imbuedSword;
	public static Item netherBlade;
	
	// crafting components
	public static Item diamondRod;
	public static Item machineCore;
	public static Item imbuedCore;
	public static Item machinePlate;
	public static Item imbuedRod;

	// other
	public static Item filterCard;
	
	public static void init(){
		// tools
		if(ConfigMan.enableVoidBucket)
			voidBucket = new ItemVoidBucket();
		
		if(ConfigMan.enableMatterTransporter)
			matterTransporter = new ItemMatterTransporter();
		
		if(ConfigMan.enableMagnet)
			magnet = new ItemMagnet();
		
		if(ConfigMan.enableSitisStick)
			sitisStick = new ItemSitisStick();
		
		if(ConfigMan.enableImbuedItems){
			imbuedSword = new ItemImbuedSword();
			imbuedTool = new ItemImbuedTool();
		}
		
		if(ConfigMan.enableNetherBlade){
			netherBlade = new ItemNetherbane();
		}
				
		// crafting components
		machineCore = new ItemCraftingBase("machineCore");
		diamondRod = new ItemCraftingBase("diamondRod");
		imbuedCore = new ItemCraftingBase("imbuedCore");
		imbuedRod = new ItemCraftingBase("imbuedRod");
		machinePlate = new ItemCraftingBase("machinePlate");

		// other
		filterCard = new ItemFilterCard();
	}
	
	public static void postInit() {
		if(ConfigMan.enablePreChewedFood) {
			bucketPreChewedFood = new ItemBucket(ModBlocks.blockPreChewedFood) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void addInformation(ItemStack p_77624_1_,
						EntityPlayer p_77624_2_, List list,
						boolean p_77624_4_) {
					super.addInformation(p_77624_1_, p_77624_2_, list, p_77624_4_);
					list.add(StatCollector.translateToLocal("tooltip.bucketPreChewedFood.flair"));
				}
			};
			bucketPreChewedFood.setUnlocalizedName("bucketPreChewedFood").setContainerItem(Items.bucket).setTextureName(PracticalitiesMod.TEXTURE_BASE + "bucket_preChewedFood");
			GameRegistry.registerItem(bucketPreChewedFood, "bucket_preChewedFood");
		}
	}
}
