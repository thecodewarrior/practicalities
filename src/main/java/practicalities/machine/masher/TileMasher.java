package practicalities.machine.masher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import practicalities.blocks.ModBlocks;
import practicalities.gui.InventorySingleStack;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cofh.core.block.TileCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileMasher extends TileCoFHBase implements IFluidHandler, IInventory, IEnergyReceiver{

	public double ENERGY_PER_MB = 0.5;
	public int MB_PER_TICK = 10;
	
	public int process_output_max; // start beyond the process time so it doesn't process when it's first loaded
	public int process_output;
	
	public EnergyStorage energy = new EnergyStorage(1000);
	public FluidTank tank = new FluidTank(5000);
	public InventorySingleStack<TileMasher> food = new InventorySingleStack<TileMasher>("inventory.tile.masher", this) {
		
		@Override
		public boolean validate(ItemStack stack) {
			return stack.getItem() instanceof ItemFood;
		}
		
		public void markDirty() {
			super.markDirty();
			data.markFilthy();
		};
		
	};
	
	public TileMasher() {
		food.data = this;
	}
	
	public static void initialize() {
		GameRegistry.registerTileEntity(TileMasher.class, "p2.masher");
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {
		if(this.worldObj.isRemote)
			return;
		
		if(process_output == 0 && food.get() != null && food.get().stackSize >= 1) {
			ItemStack processing = food.get();
			ItemFood foodProcessing = (ItemFood)processing.getItem();
			
			int heal = foodProcessing.func_150905_g(processing);
//			float saturation = foodProcessing.func_150906_h(processing);
			
			int out = heal*100;
			
			food.decrStackSize(0, 1);
			process_output = process_output_max = out;
			
		}
		
		if(process_output > 0) {
			int possibleFill = tank.fill(new FluidStack(ModBlocks.preChewedFood, Math.min(process_output, 10)), false);
			if(energy.getEnergyStored() >= possibleFill*ENERGY_PER_MB) {
				energy.extractEnergy( (int)( possibleFill *ENERGY_PER_MB ), false);
				process_output -= tank.fill(new FluidStack(ModBlocks.preChewedFood, Math.min(process_output, 10)), true);
			}
		}
	}
	
	public double getProgress() {
		return (process_output_max-process_output)/(double)process_output_max;
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getType() {
		return 0;
	}

	/**
	 * Call both markDirty() and worldObj.markBlockForUpdate
	 * This sends an update packet to the client
	 */
	public void markFilthy() {
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {
		return new GuiMasher(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {
		return new ContainerMasher(inventory, this);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		energy.writeToNBT(tag);
		tag.setInteger("tankAmount", tank.getFluidAmount());
		
		tag.setInteger("process_max", process_output_max);
		tag.setInteger("process", process_output);
		
		if(food.get() != null) {
			NBTTagCompound stackTag = new NBTTagCompound();
			food.get().writeToNBT(stackTag);
			tag.setTag("food", stackTag);
		} else {
			tag.removeTag("food");
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energy.readFromNBT(tag);
		if(tank.getFluid() == null) {
			tank.setFluid(new FluidStack(ModBlocks.preChewedFood, 10));
		}
		tank.getFluid().amount = tag.getInteger("tankAmount");
		
		process_output_max = tag.getInteger("process_max");
		process_output = tag.getInteger("process");
		
		if(tag.hasKey("food")) {
			food.set( ItemStack.loadItemStackFromNBT(tag.getCompoundTag("food")) );
		} else {
			food.set(null);
		}
	}
	
	
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	///\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\\\
	
	@Override
	public boolean canConnectEnergy(ForgeDirection dir) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection dir, int amt, boolean simulate) {
		if(!simulate && energy.getEnergyStored() != energy.getMaxEnergyStored()) markFilthy();
		return energy.receiveEnergy(amt, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection dir) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection dir) {
		return energy.getMaxEnergyStored();
	}

	///\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\\\
	
	@Override
	public int getSizeInventory() {
		return food.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return food.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return food.decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return food.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		food.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return "inventory.masher";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return food.isItemValidForSlot(slot, stack);
	}

	///\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\\\
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
//		if(doFill && tank.getFluidAmount() != tank.getCapacity()) markFilthy();
//		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(doDrain && tank.getFluidAmount() > 0) markFilthy();
		return tank.drain(Math.min(tank.getCapacity(), resource.amount), doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(doDrain && tank.getFluidAmount() > 0) markFilthy();
		return tank.drain(Math.min(tank.getCapacity(), maxDrain), doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == ModBlocks.preChewedFood;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
//		return fluid == ModBlocks.preChewedFood;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}
	
	///\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\\\
}
