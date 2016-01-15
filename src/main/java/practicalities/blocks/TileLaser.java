package practicalities.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import practicalities.Logger;
import practicalities.lib.util.WorldTraceUtil;
import practicalities.lib.util.vec.Vector3;

public class TileLaser extends TileEntity implements ITickable {

	int hitCounter = 0;
	
	List<Laser> oldLasers = new ArrayList<>();
	Laser currentLaser = null;
	
	public boolean powered = false;
	
	public static class Laser {
		public double start, end;
		public EnumFacing direction;
		
		public BlockPos moveEnd(World w, Vector3 origin, double speed) {
			Vector3 endVec = origin.copy().add(new Vector3(this.direction, this.end));
			double amountToMove = WorldTraceUtil.rayHitDistance(w, endVec, speed, this.direction);
			end += amountToMove;
			if(amountToMove != speed) {
				endVec = origin.copy().add(new Vector3(this.direction, this.end)).floor();
				
				return new BlockPos(endVec.vec3());
			}
			return null;
		}
	}
	
	public void eachLaser(Consumer<Laser> r) {
		if(currentLaser != null) r.accept(currentLaser);
		oldLasers.forEach(r);
	}
	
	public void updatePower(boolean powered) {
		if(powered) {
			currentLaser = new Laser();
			currentLaser.direction = worldObj.getBlockState(pos).getValue(BlockLaser.FACING);
		} else {
			oldLasers.add(currentLaser);
			currentLaser = null;
		}
		this.powered = powered;
	}
	
	public void laserHit(boolean power) {
		if(power) {
			hitCounter++;
		} else {
			hitCounter--;
		}
	}
	
	public boolean isPoweredByLaser() {
		return hitCounter > 0;
	}
	
	public void onBreak() {
		
	}
	
	boolean firstTick = true;
	
	@Override
	public void update() {
		if(firstTick) { // yeah, yeah, I know I'm not supposed to do this, but it caused a stack overflow in onLoad.
			this.powered = worldObj.isBlockPowered(pos);
			firstTick = false;
		}
		
		Vector3 origin = Vector3.fromTileCenter(this);
		
		double speed = .1;
		
		
		if(currentLaser != null) {
			currentLaser.end += WorldTraceUtil.rayHitDistance(worldObj, origin.copy().add(new Vector3(currentLaser.direction, currentLaser.end)), speed, currentLaser.direction);
		}
		
		for (Laser laser : oldLasers) {
			laser.start += speed;
			laser.end += WorldTraceUtil.rayHitDistance(worldObj, origin.copy().add(new Vector3(laser.direction, laser.end)), speed, laser.direction);
		}
		
		for (Iterator<Laser> iterator = oldLasers.iterator(); iterator.hasNext();) {
		    Laser laser = iterator.next();
		    if (laser.start >= laser.end) {
		        iterator.remove();
		    }
		}
		
	}
	
	public static void initialize() {
		Logger.info("    Registering Laser");
		GameRegistry.registerTileEntity(TileLaser.class, "p2.laser");
	}

}
