package practicalities.lib.common;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import practicalities.Log;
import practicalities.lib.util.Utils;

public class DropCapture {

	public class CaptureContext {
		private final AxisAlignedBB aabb;

		private final List<EntityItem> drops = Lists.newArrayList();

		public CaptureContext(AxisAlignedBB aabb) {
			this.aabb = aabb;
		}

		private boolean check(EntityItem item) {
			if (!item.isDead && aabb.intersectsWith(item.getEntityBoundingBox())) {
				drops.add(item);
				return true;
			}

			return false;
		}

		public List<EntityItem> stop() {
			captures.remove(this);
			return drops;
		}
	}

	public static final DropCapture instance = new DropCapture();

	private final List<CaptureContext> captures = Lists.newArrayList();

	public CaptureContext start(AxisAlignedBB aabb) {
		CaptureContext context = new CaptureContext(aabb);
		captures.add(context);
		return context;
	}

	public CaptureContext start(BlockPos pos) {
		return start(Utils.inBlockSpace(pos));
	}

	@SubscribeEvent
	public void onEntityConstruct(EntityJoinWorldEvent evt) {
		final Entity e = evt.entity;
		if (e != null
				&& e.getClass() == EntityItem.class
				&& !e.worldObj.isRemote) {
			final EntityItem ei = (EntityItem)e;

			for (CaptureContext c : captures)
				if (c.check(ei)) {
					evt.setCanceled(true);
					break;
				}
		}
	}

}