package practicalities.lib.util.vec;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class ExtendedVector3<D> extends Vector3
{
	public ExtendedVector3() { }

    public ExtendedVector3(double d, double d1, double d2) { super(d, d1, d2); }

    public ExtendedVector3(Vector3 vec) { super(vec); }

    public ExtendedVector3(double[] da) { super(da); }

    public ExtendedVector3(Vec3 vec) { super(vec); }

    public ExtendedVector3(BlockPos coord) { super(coord); }
	
	D data;
	
	public D getData()
	{
		return data;
	}
	public ExtendedVector3<D> setData(D data)
	{
		this.data = data;
		return this;
	}
}
