package practicalities.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;

public class ItemBucketPCF extends ItemBucket {

	public ItemBucketPCF(Block block) {
		super(block);
		setUnlocalizedName("bucket_preChewedFood");
		setTextureName("bucket_preChewedFood");
		GameRegistry.registerItem(this, block.getUnlocalizedName());
	}
	
	

}
