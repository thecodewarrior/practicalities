package practicalities.items;

import java.util.List;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.Logger;
import practicalities.PracticalitiesMod;
import practicalities.helpers.ItemHelpers;

public class ItemBase extends Item  {

	public ItemBase(String name) {
		setUnlocalizedName(name);
		GameRegistry.registerItem(this, name);
		setCreativeTab(PracticalitiesMod.tab);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {

	}
	
	public String getSimpleName(){
		return this.getUnlocalizedName().substring(5);
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
    	Logger.info("    Registering model for %s",getSimpleName());
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
