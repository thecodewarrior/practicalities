package practicalities.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileLaser extends TileEntity implements ITickable {

	List<Laser> oldLasers = new ArrayList<>();
	Laser currentLaser = null;
	public static class Laser {
		public double start, end;
		public EnumFacing direction;
	}
	
	public void updatePower(boolean powered) {
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
