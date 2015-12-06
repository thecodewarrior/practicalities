package practicalities.machine.playerinterface;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import practicalities.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cofh.core.block.TileCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;

public class TilePlayerInterface extends TileCoFHBase implements IInventory, IFluidHandler {

	private WeakReference<EntityPlayer> player;
	private UUID uuid;
	private String lastName;
	
	public FluidTank tank = new FluidTank(1000);
	
	private int timeout = 0;
	
	public TilePlayerInterface() {
		super();
	}
	
	public UUID getUUID() {
		return uuid;
	}
	public String getLastName() {
		return lastName;
	}
	
	public void setPlayer(EntityPlayer player) {
		uuid = player.getUniqueID();
		updatePlayer(true);
	}
	
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
		this.updatePlayer(true);
	}
	
	@SuppressWarnings("unchecked")
	public void updatePlayer(boolean force) {
		if(this.worldObj.isRemote)
			return;
		
		if(force || !hasPlayer() ) {
			List<EntityPlayer> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			
			for (EntityPlayer entityPlayer : playerList) {
				if(entityPlayer.getUniqueID().equals(uuid)) {
					player = new WeakReference<EntityPlayer>(entityPlayer);
					lastName = entityPlayer.getDisplayName();
					this.markDirty();
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					break;
				}
			}
			timeout = 10; // ten ticks before it will look again, no need to try any faster and will decrease performance impact
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(timeout > 0) {
			timeout--;
		} else {
			updatePlayer(false);
		}
		
		if(hasPlayer()) {
			FoodStats f = player.get().getFoodStats();
			if(f.getFoodLevel() < 20 && tank.getFluidAmount() > 100) {
				tank.drain(100, true);
				int food = 1;
				float sat = 0;
				
				if(f.getSaturationLevel() < 20 && tank.getFluidAmount() > 0) {
					FluidStack drained = tank.drain((int)( 20-f.getSaturationLevel() )*100, true);
					sat = (float)( drained.amount/100.0 );
				}
				
				f.addStats(food, sat);
			}
			
		}
	}
	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public String getName() {
		return getBlockType().getUnlocalizedName();
	}

	@Override
	public int getType() {
		return getBlockMetadata();
	}

	public static void initialize() {
		GameRegistry.registerTileEntity(TilePlayerInterface.class, "p2.playerinterface");
	}
	
	@Override
	public boolean canPlayerDismantle(EntityPlayer player) {
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTag) {
		super.writeToNBT(nbtTag);
		tank.writeToNBT(nbtTag);
		writeSyncableDataToNBT(nbtTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTag) {
		super.readFromNBT(nbtTag);
		tank.readFromNBT(nbtTag);
		readSyncableDataFromNBT(nbtTag);
	}
	
	public void writeSyncableDataToNBT(NBTTagCompound nbtTag) {
		nbtTag.setString("playerUUID", uuid == null ? "!NULL" : uuid.toString());
		nbtTag.setString("playerName", lastName == null ? "!NULL" : lastName);
	}
	
	public void readSyncableDataFromNBT(NBTTagCompound nbtTag) {
		uuid = null;
		lastName = null;
		String uuidString = nbtTag.getString("playerUUID");
		String lastNameString = nbtTag.getString("playerName");
		if(!uuidString.equals("!NULL")) {
			uuid = UUID.fromString(uuidString);
		}
		if(!lastNameString.equals("!NULL")) {
			lastName = lastNameString;
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeSyncableDataToNBT(syncData);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readSyncableDataFromNBT(pkt.func_148857_g());
	}
	
	@Override
	public void blockBroken() {
		player = null; // prevent drops
	}
	
	// Inventory code

	public boolean hasPlayer() {
		return player != null && player.get() != null;
	}
	
	@Override
	public int getSizeInventory() {
		if(!hasPlayer()) {
			return 0;
		}
		return player.get().inventory.getSizeInventory()-9;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		slot+=9;
		if(!hasPlayer()) {
			return null;
		}
		return player.get().inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		slot+=9;
		if(!hasPlayer()) {
			return null;
		}
		return player.get().inventory.decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		slot+=9;
		if(!hasPlayer()) {
			return;
		}
		player.get().inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		if(!hasPlayer()) {
			return 64;
		}
		return player.get().inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer playerAccessing) {
		if(!hasPlayer()) {
			return false;
		}
		return player.get().inventory.isUseableByPlayer(playerAccessing);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		slot+=9;
		if(!hasPlayer()) {
			return false;
		}
		return player.get().inventory.isItemValidForSlot(slot, stack);
	}

	// static inventory methods
	
	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public String getInventoryName() {
		return "container.practicalities.playerinterface";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	// ***************************
	
//	@Override
//	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
//		if(!hasPlayer()) return 0;
//		if(player.get().getFoodStats().needFood()) {
//			
//			FoodStats fs = player.get().getFoodStats();
//			
//			int amtNeeded = 0;
//			
//			int hamNeeded = 100*( 20 - fs.getFoodLevel() );
//			int satNeeded = (int)( 100*( 20.0 - fs.getSaturationLevel() ) );
//			
//			amtNeeded = hamNeeded+satNeeded;
//			
//			amtNeeded = Math.min(amtNeeded, resource.amount);
//			
//			if(doFill) {
//				int amtHam = Math.min(hamNeeded, amtNeeded)/100;
//				float amtSat = Math.min(satNeeded, (amtNeeded-(amtHam*100)))/100;
//				
//				fs.addStats(amtHam, amtSat);
//			}
//			return amtNeeded;
////			amtNeeded = hamPointsNeeded*100 + saturationNeeded*100;
//			
//		}
//		return 0;
//	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(doFill && tank.getFluidAmount() != tank.getCapacity()) markDirty();
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
//		if(doDrain && tank.getFluidAmount() > 0) markFilthy();
//		return tank.drain(Math.min(tank.getCapacity(), resource.amount), doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
//		if(doDrain && tank.getFluidAmount() > 0) markFilthy();
//		return tank.drain(Math.min(tank.getCapacity(), maxDrain), doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true; //fluid != null && fluid.getName() == ModBlocks.preChewedFood.getName();
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}
}
