package practicalities.machine.teslacoil;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import practicalities.ConfigMan;
import practicalities.base.TileSimpleInventory;
import practicalities.machine.inductioncoil.InductionCoilManager;
import codechicken.lib.vec.BlockCoord;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileTeslaCoil extends TileSimpleInventory implements IEnergyReceiver {

	private int storageBase = 10000;
	private EnergyStorage energy;

	public TileTeslaCoil() {
		super(1);
		energy = new EnergyStorage(storageBase, 1000);

	}

	public static void initialize() {
		GameRegistry.registerTileEntity(TileTeslaCoil.class, "p2.teslacoil");
	}

	@Override
	public String getName() {
		return getBlockType().getUnlocalizedName();
	}

	@Override
	public int getType() {
		return getBlockMetadata();
	}

	@Override
	public boolean canPlayerDismantle(EntityPlayer player) {
		return false;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		if (from == ForgeDirection.UP) {
			return false;
		}
		return true;
	}
	
	@Override
	public int receiveEnergy(ForgeDirection side, int amt, boolean simulate) {
		return energy.receiveEnergy(amt, simulate);
	}
	
	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public String getInventoryName() {
		return "";
	}
	
	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		
		int draw = 10;
		
		if(energy.getEnergyStored() < draw)
			return;
		
		List<BlockCoord> coils = InductionCoilManager.getCoilsInRange(worldObj, xCoord, yCoord, zCoord, ConfigMan.teslaRange);
		for (BlockCoord coord : coils) {
			if(energy.getEnergyStored() < draw)
				break;
			
			TileEntity entity = worldObj.getTileEntity(coord.x, coord.y, coord.z);
			if(entity instanceof IFieldReceiver) {
				IFieldReceiver coil = (IFieldReceiver)entity;
				
				if(coil.canFitRF()) {
					coil.reciveRF();
					energy.extractEnergy(draw, false);
					markDirty();
					markChunkDirty();
				}
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound energyTag = new NBTTagCompound();
		this.energy.writeToNBT(energyTag);
		tag.setTag("energy", energyTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		NBTTagCompound energyTag = tag.getCompoundTag("energy");
		this.energy.readFromNBT(energyTag);
	}

	
}
