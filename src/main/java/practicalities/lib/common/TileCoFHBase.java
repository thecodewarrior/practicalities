package practicalities.lib.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import practicalities.lib.util.Utils;

public abstract class TileCoFHBase extends TileEntity {

	@Override
	public void onChunkUnload() {

		if (!tileEntityInvalid) {
			invalidate(); // this isn't called when a tile unloads. guard incase it is in the future
		}
	}

	public abstract String getName();

	public abstract int getType();

	public void blockBroken() {

	}

	public void blockDismantled() {

		blockBroken();
	}

	public void blockPlaced() {

	}

	public void markChunkDirty() {
		worldObj.markChunkDirty(pos, this);
	}

	public void callNeighborBlockChange() {
		worldObj.notifyNeighborsOfStateChange(pos, blockType);
	}

	public void onNeighborBlockChange() {

	}

	public void onNeighborTileChange(BlockPos pos) {

	}

	public int getComparatorInput() {
		return 0;
	}

	public int getLightValue() {

		return 0;
	}

	public boolean canPlayerAccess(EntityPlayer player) {
		return true;
	}

	public boolean canPlayerDismantle(EntityPlayer player) {
		return true;
	}

	public boolean isUseable(EntityPlayer player) {
		double reach = Utils.getBlockReachDistance(player);
		return player.getDistanceSq(pos) <= reach*reach && worldObj.getTileEntity(pos) == this;
	}

	public boolean onWrench(EntityPlayer player, int hitSide) {

		return false;
	}
	
	public abstract void writeSyncableDataToNBT(NBTTagCompound tag);
	public abstract void readSyncableDataFromNBT(NBTTagCompound tag);
	
	public abstract void writeServerDataToNBT(NBTTagCompound tag);
	public abstract void readServerDataFromNBT(NBTTagCompound tag);

	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(pos, 1, syncData);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readSyncableDataFromNBT(pkt.getNbtCompound());
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		writeServerDataToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		readServerDataFromNBT(tag);
	}

	/* GUI METHODS */
	public Object getGuiClient(InventoryPlayer inventory) {

		return null;
	}

	public Object getGuiServer(InventoryPlayer inventory) {

		return null;
	}

	public int getInvSlotCount() {

		return 0;
	}

	public boolean openGui(EntityPlayer player) {

		return false;
	}

	public void receiveGuiNetworkData(int i, int j) {

	}

	public void sendGuiNetworkData(Container container, ICrafting player) {

	}

}
