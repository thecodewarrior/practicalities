package practicalities.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import practicalities.PracticalitiesMod;
import practicalities.helpers.ItemHelpers;
import practicalities.registers.ItemRegister;

public class ItemImbuedTool extends ItemPickaxe {

	public ItemImbuedTool() {
		super(ItemRegister.imbuedToolMaterial);
		String name = "imbuedTool";
		GameRegistry.registerItem(this, name);
		setUnlocalizedName(name);
		setCreativeTab(PracticalitiesMod.tab);
		setMaxStackSize(1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean p_77624_4_) {
		ItemHelpers.addFlairToList(list, "item.imbuedTool");
	}

	// @Override
	// public boolean onItemUse(ItemStack a, EntityPlayer b, World c, int d, int
	// e, int f, int g, float h, float i, float j) {
	// return ModItems.blockFakeTorch.onItemUse(new
	// ItemStack(ModItems.blockFakeTorch, 1), b, c, d, e, f, g, h, i, j);
	// }

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase entity2) {
		setDamage(stack, 0);
		return true;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, 0);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos,
			EntityLivingBase playerIn) {

		setDamage(stack, 0);
		return true;
	}

	@Override
	public float getDigSpeed(ItemStack stack, IBlockState state) {
		return efficiencyOnProperMaterial;
	}

}