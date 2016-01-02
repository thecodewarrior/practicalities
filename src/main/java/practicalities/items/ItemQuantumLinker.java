package practicalities.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import practicalities.Logger;
import practicalities.blocks.machine.quantumBinding.BlockQuantumBinding;
import practicalities.blocks.machine.quantumBinding.BlockQuantumBinding.BlockLocation;
import practicalities.lib.util.Utils;
import practicalities.registers.BlockRegister;

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
		
		if(playerIn.isSneaking()) {
			tag.setBoolean("isBound", false);
			return true;
		}
		
		if(worldIn.getBlockState(pos).getBlock() == BlockRegister.quantumBinding && tag.getBoolean("isBound")) {
			Logger.info("Binding set to %s", tag.getString("bound"));
			BlockQuantumBinding.links.put( new BlockLocation(worldIn, pos).getString(), tag.getString("bound") );
			return true;
		}
		
		tag.setBoolean("isBound", true);
		tag.setString("bound", Utils.sterilizeLocation(worldIn, pos));
		
		Logger.info("Bound to: %s", tag.getString("bound"));
		return true;
	}

}
