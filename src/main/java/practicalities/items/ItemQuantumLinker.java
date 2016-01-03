package practicalities.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import practicalities.Logger;
import practicalities.lib.common.BlockLocation;
import practicalities.lib.util.Utils;
import practicalities.quantumNetwork.IQuantumBindable;

public class ItemQuantumLinker extends ItemBase {

	public ItemQuantumLinker() {
		super("quantumTuner");
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return super.hasEffect(stack) || ( tag != null && tag.getBoolean("isBound") );
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if(worldIn.isRemote)
			return itemStackIn;
		NBTTagCompound tag = itemStackIn.getTagCompound();
		if(tag == null) {
			tag = new NBTTagCompound();
			itemStackIn.setTagCompound(tag);
			tag.setBoolean("isBound", false);
		}
		if(playerIn.isSneaking()) {
			tag.setBoolean("isBound", false);
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote)
			return true;
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
			tag.setBoolean("isBound", false);
		}
		
		Block b = worldIn.getBlockState(pos).getBlock();
		if(b instanceof IQuantumBindable && tag.getBoolean("isBound") && !playerIn.isSneaking()) {
			((IQuantumBindable)b).bind(new BlockLocation(worldIn, pos), new BlockLocation(tag.getString("bound")));
			return true;
		}
		if(playerIn.isSneaking() && !(b instanceof IQuantumBindable)) {
			tag.setBoolean("isBound", false);
			return true;
		}
		
		tag.setBoolean("isBound", true);
		tag.setString("bound", Utils.sterilizeLocation(worldIn, pos));
		
		Logger.info("Bound to: %s", tag.getString("bound"));
		return true;
	}

}
