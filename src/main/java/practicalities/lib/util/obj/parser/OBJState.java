package practicalities.lib.util.obj.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.TRSRTransformation;

public class OBJState implements IModelState
{
    protected Map<String, Boolean> visibilityMap = new HashMap<String, Boolean>();
    public IModelState parent;
    protected Operation operation = Operation.SET_TRUE;

    public OBJState(List<String> visibleGroups, boolean visibility)
    {
        this(visibleGroups, visibility, TRSRTransformation.identity());
    }

    public OBJState(List<String> visibleGroups, boolean visibility, IModelState parent)
    {
        this.parent = parent;
        for (String s : visibleGroups) this.visibilityMap.put(s, visibility);
    }

    public IModelState getParent(IModelState parent)
    {
        if (parent == null) return null;
        else if (parent instanceof OBJState) return ((OBJState) parent).parent;
        return parent;
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if (parent != null) return parent.apply(part);
        return Optional.absent();
    }

    public Map<String, Boolean> getVisibilityMap()
    {
        return this.visibilityMap;
    }

    public List<String> getGroupsWithVisibility(boolean visibility)
    {
        List<String> ret = Lists.newArrayList();
        for (Map.Entry<String, Boolean> e : this.visibilityMap.entrySet())
        {
            if (e.getValue() == visibility)
            {
                ret.add(e.getKey());
            }
        }
        return ret;
    }

    public List<String> getGroupNamesFromMap()
    {
        return Lists.newArrayList(this.visibilityMap.keySet());
    }

    public void changeGroupVisibilities(List<String> names, Operation operation)
    {
        if (names == null || names.isEmpty()) return;
        this.operation = operation;
        if (names.get(0).equals(Group.ALL))
        {
            for (String s : this.visibilityMap.keySet())
            {
                this.visibilityMap.put(s, this.operation.performOperation(this.visibilityMap.get(s)));
            }
        }
        else if (names.get(0).equals(Group.ALL_EXCEPT))
        {
            for (String s : this.visibilityMap.keySet())
            {
                if (!names.subList(1,  names.size()).contains(s))
                {
                    this.visibilityMap.put(s,  this.operation.performOperation(this.visibilityMap.get(s)));
                }
            }
        }
        else
        {
            for (String s : names)
            {
                this.visibilityMap.put(s, this.operation.performOperation(this.visibilityMap.get(s)));
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("OBJState: ");
        builder.append(String.format("%n    parent: %s%n", this.parent.toString()));
        builder.append(String.format("    visibility map: %n"));
        for (Map.Entry<String, Boolean> e : this.visibilityMap.entrySet())
        {
            builder.append(String.format("        name: %s visible: %b%n", e.getKey(), e.getValue()));
        }
        return builder.toString();
    }

    public enum Operation
    {
        SET_TRUE,
        SET_FALSE,
        TOGGLE;

        Operation(){}

        public boolean performOperation(boolean valueToToggle)
        {
            switch(this)
            {
            default:
            case SET_TRUE: return true;
            case SET_FALSE: return false;
            case TOGGLE: return !valueToToggle;
            }
        }
    }
}
