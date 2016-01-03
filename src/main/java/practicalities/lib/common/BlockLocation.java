package practicalities.lib.common;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import practicalities.lib.util.Utils;

public class BlockLocation {
	
	private World world;
	private BlockPos pos;
	
	public int data;
	
	public World getWorld() { return world; }
	public BlockPos getPos() { return pos; }
	
	public void setWorld(World w) { world = w; }
	public void setPos(BlockPos p) { pos = p; }
	
	public BlockLocation(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}
	public BlockLocation(String parse) {
		this(Utils.desterilizeWorld(parse), Utils.desterilizePos(parse));
	}
	
	protected BlockLocation() {}
	
	public String getString() {
		return Utils.sterilizeLocation(getWorld(), getPos());
	}
}
