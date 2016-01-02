package practicalities.lib.util.obj.parser;

import net.minecraftforge.common.property.IUnlistedProperty;

public enum OBJProperty implements IUnlistedProperty<OBJState>
{
    instance;
    public String getName()
    {
        return "OBJPropery";
    }

    @Override
    public boolean isValid(OBJState value)
    {
        return value instanceof OBJState;
    }

    @Override
    public Class<OBJState> getType()
    {
        return OBJState.class;
    }

    @Override
    public String valueToString(OBJState value)
    {
        return value.toString();
    }
}
