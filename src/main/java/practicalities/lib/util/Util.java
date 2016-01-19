package practicalities.lib.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Util {
	
	public static boolean isWrench(ItemStack stack) {
		return stack != null && stack.getItem() == Items.stick;
	}
	
	public static boolean isHoldingWrench(EntityLivingBase entity) {
		if(entity != null && isWrench( entity.getHeldItem() )) {
			return true;
		}
		return false;
	}
	
}
