package practicalities.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.GameRegistry;
import practicalities.Logger;
import practicalities.lib.util.WorldTraceUtil;
import practicalities.lib.util.vec.Vector3;
import practicalities.network.NetHandler;
import practicalities.network.message.MessageSyncLasers;

public class TileLaser extends TileEntity implements ITickable {

	int hitCounter = 0;
	
	public EnumFacing facing = EnumFacing.UP, attached = EnumFacing.DOWN;
	
	List<Laser> oldLasers = new ArrayList<>();
	Laser currentLaser = null;
	
	public boolean powered = false;
	
	public static class Laser {
		public double start, end, lastStart, lastEnd;
		
		public BlockPos moveEnd(World w, Vector3 origin, double speed, EnumFacing direction) {
			Vector3 endVec = origin.copy().add(new Vector3(direction, this.end));
			double amountToMove = WorldTraceUtil.rayHitDistance(w, endVec, speed, direction);
			end += amountToMove;
			if(amountToMove != speed) {
				endVec = origin.copy().add(new Vector3(direction, this.end)).floor();
				
				return new BlockPos(endVec.vec3());
			}
			return null;
		}
		
		public NBTTagCompound tag() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(START_TAG, start);
			tag.setDouble(END_TAG,   end);
			return tag;
		}
		
		public static Laser fromTag(NBTTagCompound tag) {
			Laser laser = new Laser();
			laser.start = tag.getDouble(START_TAG);
			laser.end   = tag.getDouble(END_TAG);
			return laser;
		}
		
		public static final String START_TAG = "s", END_TAG = "e";
	}
	
	public void eachLaser(Consumer<Laser> r) {
		if(currentLaser != null) r.accept(currentLaser);
		oldLasers.forEach(r);
	}
	
	public void updatePower(boolean powered) {
		if(powered == this.powered)
			return;
		
		if(powered) {
			currentLaser = new Laser();
		} else {
			oldLasers.add(currentLaser);
			currentLaser = null;
		}
		this.powered = powered;
		sync();
	}
	
	public void laserHit(boolean power) {
		if(power) {
			hitCounter++;
		} else {
			hitCounter--;
		}
		if(hitCounter > 1)
			Logger.track("HIT - " + pos, "%d", hitCounter);
		sync();
	}
	
	public boolean isPoweredByLaser() {
		return hitCounter > 0;
	}
	
	public void onBreak() {
		if(currentLaser != null)
			handleHit(currentLaser, false);
		for (Laser laser : oldLasers) {
			if(laser != null) {
				handleHit(laser, false);
			}
		}
	}
	
	boolean firstTick = true;
	public static final double RANGE = 32;
	public static final int RESYNC_INTERVAL = 3*(20); // resyncs lasers every three seconds
	public static double SPEED = 1;
	int reSyncTimer = RESYNC_INTERVAL;
	
	@Override
	public void update() {
		if(firstTick) { // yeah, yeah, I know I'm not supposed to do this, but it caused a stack overflow in onLoad.
			this.powered = worldObj.isBlockPowered(pos);
			firstTick = false;
		}
		
		SPEED = 2;
		
		IBlockState state = worldObj.getBlockState(pos);
		if(!( state.getBlock() instanceof BlockLaser))
			return;
		
		Vector3 origin = Vector3.fromTileCenter(this);
		
		double d = 0.0;
		
		if(currentLaser != null) {
			if( currentLaser.end > RANGE ) {
				currentLaser.end = RANGE;
				currentLaser.lastEnd = RANGE;
			} else if( currentLaser.end < RANGE ) {
				double dist = WorldTraceUtil.rayHitDistance(worldObj, origin.copy().add(new Vector3(facing, currentLaser.end-d)), SPEED+d, facing)-d;
				currentLaser.lastEnd = currentLaser.end;
				currentLaser.end += dist;
				if(dist != SPEED && dist != 0) {
					handleHit(currentLaser, true);
				}
			}
			
		}
		
		for (Laser laser : oldLasers) {
			if(laser == null) continue; // shouldn't happen, but just in case.
			laser.lastStart = laser.start;
			laser.start += SPEED;
			if( laser.end > RANGE ) {
				laser.end = RANGE;
				laser.lastEnd = RANGE;
			} else if( laser.end < RANGE ) {
				double dist = WorldTraceUtil.rayHitDistance(worldObj, origin.copy().add(new Vector3(facing, laser.end-d)), SPEED+d, facing)-d;
				laser.lastEnd = laser.end;
				laser.end += dist;
				if(dist != SPEED && dist != 0) {
					handleHit(laser, true);
				}
			}
		}
		
		if(currentLaser != null && currentLaser.start >= currentLaser.end) {
			handleHit(currentLaser, false);
			currentLaser = null;
		}
		for (Iterator<Laser> iterator = oldLasers.iterator(); iterator.hasNext();) {
		    Laser laser = iterator.next();
		    if (laser == null || laser.start >= laser.end) {
		    	if(laser != null) handleHit(laser, false);
		        iterator.remove();
		    }
		}
		
		if(reSyncTimer == 0) {
			sync();
		}
		reSyncTimer--;
		
	}
	
	public void handleHit(Laser laser, boolean power) {
		Vector3 hit = Vector3.fromTileCenter(this).add(new Vector3(facing, laser.end+0.1)).floor();
		
		Block hitBlock = worldObj.getBlockState(hit.blockPos()).getBlock();
		if(hitBlock instanceof ILaserReciver) {
			( (ILaserReciver) hitBlock).laserHit(worldObj, hit.blockPos(), facing.getOpposite(), pos, power);
		}
	}
	
	public void sync() {
		reSyncTimer = RESYNC_INTERVAL;
		NetHandler.network.sendToAllAround(new MessageSyncLasers(pos, currentLaser, oldLasers),
				new TargetPoint(this.worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), RANGE+16));
	}
	
	public void syncFrom(MessageSyncLasers packet) {
		this.currentLaser = packet.current;
		this.oldLasers = new ArrayList<>(packet.old);
	}
	
	AxisAlignedBB renderBox = AxisAlignedBB.fromBounds(
			-RANGE, -RANGE, -RANGE,
			 RANGE,  RANGE,  RANGE);
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return renderBox.offset(pos.getX(), pos.getY(), pos.getZ());//new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(RANGE, RANGE, RANGE);
	}
	
	@Override
	public double getMaxRenderDistanceSquared() {
		return (RANGE*3)*(RANGE*3);
	}
	
	public static void initialize() {
		Logger.info("    Registering Laser");
		GameRegistry.registerTileEntity(TileLaser.class, "p2.laser");
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		if(currentLaser != null){
			compound.setTag(CURRENT_TAG, currentLaser.tag());
		}
		
		compound.setInteger(FACING_TAG, facing.ordinal());
		compound.setInteger(ATTACHED_TAG, attached.ordinal());
		
		compound.setInteger(HITS_TAG, hitCounter);
		
		if(oldLasers.size() != 0) {
			NBTTagList list = new NBTTagList();
			for (Laser laser : oldLasers) {
				if(laser == null) continue; // don't know why this would happen, but if it does.
				list.appendTag(laser.tag());
			}
			compound.setTag(OLD_TAG, list);
		}
		super.writeToNBT(compound);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(FACING_TAG, facing.ordinal());
		return new S35PacketUpdateTileEntity(pos, 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		facing = EnumFacing.VALUES[pkt.getNbtCompound().getInteger(FACING_TAG)];
		super.onDataPacket(net, pkt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		currentLaser = null;
		oldLasers.clear();
		
		if(compound.hasKey(CURRENT_TAG)) {
			currentLaser = Laser.fromTag(compound.getCompoundTag(CURRENT_TAG));
		}
		
		facing = EnumFacing.VALUES[compound.getInteger(FACING_TAG)];
		attached = EnumFacing.VALUES[compound.getInteger(ATTACHED_TAG)];
		hitCounter = compound.getInteger(HITS_TAG);
		
		if(compound.hasKey(OLD_TAG)) {
			NBTTagList list = compound.getTagList(OLD_TAG, 10);
			for (int i = 0; i < list.tagCount(); i++) {
				oldLasers.add( Laser.fromTag(list.getCompoundTagAt(i)) );
			}
		}
		super.readFromNBT(compound);
	}
	
	public static final String CURRENT_TAG = "c", OLD_TAG = "o", FACING_TAG = "f", ATTACHED_TAG = "a", HITS_TAG = "h";

}
