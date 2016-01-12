package practicalities.blocks.machine.redstoneLaser;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import practicalities.lib.common.TileCoFHBase;
import practicalities.lib.util.Utils;

public class TileEntityRedstoneLaser extends TileCoFHBase implements ITickable {
	
	List<Laser> lasers = new ArrayList<Laser>();
	
	public TileEntityRedstoneLaser() {
		
	}
	
	@Override
	public void update() {
		boolean powered = false;
		IBlockState state = worldObj.getBlockState(pos);
		if(worldObj.isBlockPowered(pos)) {
			powered = true;
		}
		
		int speed = 2;
		
		for (Laser laser : lasers) {
			BlockPos pos = this.pos.offset(laser.direction, laser.end);
			
			for (int i = 1; i <= speed; i++) {
				BlockPos checkPos = pos.offset(laser.direction, i);
				IBlockState checkState = worldObj.getBlockState(checkPos);
				List<AxisAlignedBB> checkCollisionBoxes = new ArrayList<>();
				checkState.getBlock().addCollisionBoxesToList(worldObj, checkPos, checkState, Utils.inBlockSpace(checkPos), checkCollisionBoxes, null);
				if(checkCollisionBoxes.size() > 0) {
					laser.beginHit(worldObj, checkPos);
				}
			}
			
		}
		
		if(state.getValue(BlockRedstoneLaser.POWERED) != powered) {
			worldObj.setBlockState(pos, state.withProperty(BlockRedstoneLaser.POWERED, powered));
		}
	}

	@Override
	public void writeSyncableDataToNBT(NBTTagCompound tag) {
		
	}

	@Override
	public void readSyncableDataFromNBT(NBTTagCompound tag) {
		
	}

	@Override
	public void writeServerDataToNBT(NBTTagCompound tag) {
		
	}

	@Override
	public void readServerDataFromNBT(NBTTagCompound tag) {
		
	}
	
	
	
}
