package practicalities.lib.common;

import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.util.EnumFacing;

public interface IPistonListener {
	public void move(BlockLocation loc, EnumPistonType type, EnumFacing direction);
	public void destroy(BlockLocation loc);
}
