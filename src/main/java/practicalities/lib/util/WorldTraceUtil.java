package practicalities.lib.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import practicalities.Logger;
import practicalities.lib.util.vec.ExtendedVector3;
import practicalities.lib.util.vec.Vector3;

public class WorldTraceUtil {
private static List<AxisAlignedBB> collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
	
	public static float MARGIN_OF_ERROR = 1/16f;
	
	private static void addCollidingBoundingBoxes(Entity e, World w, AxisAlignedBB bb) {
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        int i  = MathHelper.floor_double(bb.minX);
        int j  = MathHelper.floor_double(bb.maxX + 1.0D); // floor(x+1) == ceiling(x)
        int k  = MathHelper.floor_double(bb.minY);
        int l  = MathHelper.floor_double(bb.maxY + 1.0D); // floor(x+1) == ceiling(x)
        int i1 = MathHelper.floor_double(bb.minZ);
        int j1 = MathHelper.floor_double(bb.maxZ + 1.0D); // floor(x+1) == ceiling(x)
        
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = i1; l1 < j1; ++l1)
            {
                if (w.isBlockLoaded(blockpos$mutableblockpos.set(k1, 64, l1)))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        blockpos$mutableblockpos.set(k1, i2, l1);

                        IBlockState iblockstate1 = w.getBlockState(blockpos$mutableblockpos);
                        
                        iblockstate1.getBlock().addCollisionBoxesToList(w, blockpos$mutableblockpos, iblockstate1, bb, list, e);
                    }
                }
            }
        }
        
        collidingBoundingBoxes = list;
	}
	
	private static ExtendedVector3<MovingObjectPosition> traceAABB(Vector3 start, Vector3 end, AxisAlignedBB aabb) {
		if(aabb == null || start == null || end == null)
			return null;
		MovingObjectPosition mop = aabb.expand(MARGIN_OF_ERROR, MARGIN_OF_ERROR, MARGIN_OF_ERROR).calculateIntercept(start.vec3(), end.vec3());
		if(mop == null)
			return null;
		else
			return ( new ExtendedVector3<MovingObjectPosition>(mop.hitVec) ).setData(mop);
	}
	
	public static ExtendedVector3<EnumFacing> collisionRayCast(World world, Vector3 start, Vector3 offset, Entity e)
	{
	    Vector3 end   = start.copy().add(offset);

	    double minX = Math.min(start.x, end.x);
		double minY = Math.min(start.y, end.y);
		double minZ = Math.min(start.z, end.z);
		
		double maxX = Math.max(start.x, end.x);
		double maxY = Math.max(start.y, end.y);
		double maxZ = Math.max(start.z, end.z);
		
//		collidingBoundingBoxes.clear();
		List<AxisAlignedBB> scanBBs = new ArrayList<AxisAlignedBB>();
		double magS = offset.magSquared();
		if(magS > 50) {
			double count = Math.ceil( magS/50.0 ); // this is a double so the division below doesn't round
			for(int i = 0; i <= count; i++) {
				scanBBs.add(
						AxisAlignedBB.fromBounds(
								minX+( offset.x * i/count), minY+( offset.y * i/count), minZ+( offset.z * i/count),
								maxX-( offset.x * (count-i)/count), maxY-( offset.y * (count-i)/count), maxZ-( offset.z * (count-i)/count)
							).expand(1, 1, 1)
						);
			}
		} else {
			scanBBs.add(
					AxisAlignedBB.fromBounds(
							minX, minY, minZ,
							maxX, maxY, maxZ
						).expand(0.5, 0.5, 0.5)
				);
		}
		for (AxisAlignedBB aabb : scanBBs)
		{
			collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
			aabb.expand(MARGIN_OF_ERROR, MARGIN_OF_ERROR, MARGIN_OF_ERROR);
			addCollidingBoundingBoxes(e, world, aabb);
			
			Vector3 shortestHit = null;
		    double shortestMagSquared = Double.MAX_VALUE;
		    EnumFacing shortestSide = null;
		    for (int i = 0; i < collidingBoundingBoxes.size(); i++)
			{
		    	AxisAlignedBB currentBB = (AxisAlignedBB)collidingBoundingBoxes.get(i);
				ExtendedVector3<MovingObjectPosition> currentHit = traceAABB(start, end, currentBB);
				if(currentHit != null)
				{
					double currentMagS = currentHit.copy().sub(start).magSquared();
					if(currentMagS < shortestMagSquared) {
						shortestHit = currentHit;
						shortestMagSquared = currentMagS;
						shortestSide = currentHit.getData().sideHit;
					}
				}
			}
		    if(shortestHit != null) {
		    	return new ExtendedVector3<EnumFacing>(shortestHit.sub(start)).setData(shortestSide);
			}
		}
		return new ExtendedVector3<EnumFacing>(offset).setData(null);
	}
	
	public static double rayHitDistance(World w, Vector3 vec, double distance, EnumFacing direction) {
		
		AxisAlignedBB aabb = AxisAlignedBB.fromBounds(
				vec.x, vec.y, vec.z,
				vec.x+(direction.getFrontOffsetX()*distance), vec.y+(direction.getFrontOffsetY()*distance), vec.z+(direction.getFrontOffsetZ()*distance)
			);
		
		collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
		aabb.expand(MARGIN_OF_ERROR, MARGIN_OF_ERROR, MARGIN_OF_ERROR);
		addCollidingBoundingBoxes(new EntityItem(w), w, aabb);
		
		double minDistance = distance;
		for (AxisAlignedBB bb : collidingBoundingBoxes) {
			double collideDistance = collideWithSide(bb, vec, direction);
			if(collideDistance < minDistance)
				minDistance = collideDistance;
		}
		
		return minDistance;
	}
	
	public static double collideWithSide(AxisAlignedBB bb, Vector3 vec, EnumFacing dir) {
		double distance = Double.POSITIVE_INFINITY;
		switch (dir) {
		case DOWN:
			if( bb.minX <= vec.x && bb.maxX >= vec.x &&
				bb.minZ <= vec.z && bb.maxZ >= vec.z && bb.maxY <= vec.y) {
				distance = vec.y - bb.maxY;
			}
			break;
		case UP:
			if( bb.minX <= vec.x && bb.maxX >= vec.x &&
				bb.minZ <= vec.z && bb.maxZ >= vec.z && bb.minY >= vec.y) {
				distance = bb.minY - vec.y;
			}
			break;
			
		case WEST:
			if( bb.minY <= vec.y && bb.maxY >= vec.y &&
				bb.minZ <= vec.z && bb.maxZ >= vec.z && bb.maxX <= vec.x) {
				distance = vec.x - bb.maxX;
			}
			break;
		case EAST:
			if( bb.minY <= vec.y && bb.maxY >= vec.y &&
				bb.minZ <= vec.z && bb.maxZ >= vec.z && bb.minX >= vec.x) {
				distance = bb.minX - vec.x;
			}
			break;
			
		case NORTH:
			if( bb.minY <= vec.y && bb.maxY >= vec.y &&
				bb.minX <= vec.x && bb.maxX >= vec.x && bb.maxZ <= vec.z) {
				distance = vec.z - bb.maxZ;
			}
			break;
		case SOUTH:
			if( bb.minY <= vec.y && bb.maxY >= vec.y &&
				bb.minX <= vec.x && bb.maxX >= vec.x && bb.minZ >= vec.z) {
				distance = bb.minZ - vec.z;
			}
			break;
		}
		return distance;
	}
}
