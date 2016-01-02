package practicalities.lib.util.obj.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;

public class OBJBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel
{
    private final OBJModel model;
    private IModelState state;
    private final VertexFormat format;
    private Set<BakedQuad> quads;
    private ImmutableMap<String, TextureAtlasSprite> textures;
    private TextureAtlasSprite sprite = ModelLoader.White.instance;

    public OBJBakedModel(OBJModel model, IModelState state, VertexFormat format, ImmutableMap<String, TextureAtlasSprite> textures)
    {
        this.model = model;
        this.state = state;
        if (this.state instanceof OBJState) this.updateStateVisibilityMap((OBJState) this.state);
        this.format = format;
        this.textures = textures;
    }

    public void scheduleRebake()
    {
        this.quads = null;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side)
    {
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getGeneralQuads()
    {
        if (quads == null)
        {
            quads = Collections.synchronizedSet(new LinkedHashSet<BakedQuad>());
            Set<Face> faces = Collections.synchronizedSet(new LinkedHashSet<Face>());
            Optional<TRSRTransformation> transform = Optional.absent();
            for (Group g : this.model.getMatLib().getGroups().values())
            {
//                g.minUVBounds = this.model.getMatLib().minUVBounds;
//                g.maxUVBounds = this.model.getMatLib().maxUVBounds;
//                FMLLog.info("Group: %s u: [%f, %f] v: [%f, %f]", g.name, g.minUVBounds[0], g.maxUVBounds[0], g.minUVBounds[1], g.maxUVBounds[1]);

                if (this.state instanceof OBJState)
                {
                    OBJState state = (OBJState) this.state;
                    if (state.parent != null)
                    {
                        transform = state.parent.apply(Optional.<IModelPart>absent());
                    }
                    //TODO: can this be replaced by updateStateVisibilityMap(OBJState)?
                    if (state.getGroupNamesFromMap().contains(Group.ALL))
                    {
                        state.visibilityMap.clear();
                        for (String s : this.model.getMatLib().getGroups().keySet())
                        {
                            state.visibilityMap.put(s, state.operation.performOperation(true));
                        }
                    }
                    else if (state.getGroupNamesFromMap().contains(Group.ALL_EXCEPT))
                    {
                        List<String> exceptList = state.getGroupNamesFromMap().subList(1, state.getGroupNamesFromMap().size());
                        state.visibilityMap.clear();
                        for (String s : this.model.getMatLib().getGroups().keySet())
                        {
                            if (!exceptList.contains(s))
                            {
                                state.visibilityMap.put(s, state.operation.performOperation(true));
                            }
                        }
                    }
                    else
                    {
                        for (String s : state.visibilityMap.keySet())
                        {
                            state.visibilityMap.put(s, state.operation.performOperation(state.visibilityMap.get(s)));
                        }
                    }
                    if (state.getGroupsWithVisibility(true).contains(g.getName()))
                    {
                        faces.addAll(g.applyTransform(transform));
                    }
                }
                else
                {
                    transform = state.apply(Optional.<IModelPart>absent());
                    faces.addAll(g.applyTransform(transform));
                }
            }
            for (Face f : faces)
            {
                if (this.model.getMatLib().materials.get(f.getMaterialName()).isWhite())
                {
                    for (Vertex v : f.getVertices())
                    {//update material in each vertex
                        if (!v.getMaterial().equals(this.model.getMatLib().getMaterial(v.getMaterial().getName())))
                        {
                            v.setMaterial(this.model.getMatLib().getMaterial(v.getMaterial().getName()));
                        }
                    }
                    sprite = ModelLoader.White.instance;
                } else sprite = this.textures.get(f.getMaterialName());
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
                builder.setQuadOrientation(EnumFacing.getFacingFromVector(f.getNormal().x, f.getNormal().y, f.getNormal().z));
                builder.setQuadColored();
                Normal faceNormal = f.getNormal();
                putVertexData(builder, f.verts[0], faceNormal, TextureCoordinate.getDefaultUVs()[0], sprite);
                putVertexData(builder, f.verts[1], faceNormal, TextureCoordinate.getDefaultUVs()[1], sprite);
                putVertexData(builder, f.verts[2], faceNormal, TextureCoordinate.getDefaultUVs()[2], sprite);
                putVertexData(builder, f.verts[3], faceNormal, TextureCoordinate.getDefaultUVs()[3], sprite);
                quads.add(builder.build());
            }
        }
        List<BakedQuad> quadList = Collections.synchronizedList(Lists.newArrayList(quads));
        return quadList;
    }

    private final void putVertexData(UnpackedBakedQuad.Builder builder, Vertex v, Normal faceNormal, TextureCoordinate defUV, TextureAtlasSprite sprite)
    {
        for (int e = 0; e < format.getElementCount(); e++)
        {
            switch (format.getElement(e).getUsage())
            {
                case POSITION:
                    builder.put(e, v.getPos().x, v.getPos().y, v.getPos().z, v.getPos().w);
                    break;
                case COLOR:
                    float d;
                    if (v.hasNormal())
                        d = LightUtil.diffuseLight(v.getNormal().x, v.getNormal().y, v.getNormal().z);
                    else
                        d = LightUtil.diffuseLight(faceNormal.x, faceNormal.y, faceNormal.z);

                    if (v.getMaterial() != null)
                        builder.put(e,
                                d * v.getMaterial().getColor().x,
                                d * v.getMaterial().getColor().y,
                                d * v.getMaterial().getColor().z,
                                v.getMaterial().getColor().w);
                    else
                        builder.put(e, d, d, d, 1);
                    break;
                case UV:
                    if (!v.hasTextureCoordinate())
                        builder.put(e,
                                sprite.getInterpolatedU(defUV.u * 16),
                                sprite.getInterpolatedV((model.customData.flipV ? 1 - defUV.v: defUV.v) * 16),
                                0, 1);
                    else
                        builder.put(e,
                                sprite.getInterpolatedU(v.getTextureCoordinate().u * 16),
                                sprite.getInterpolatedV((model.customData.flipV ? 1 - v.getTextureCoordinate().v : v.getTextureCoordinate().v) * 16),
                                0, 1);
                    break;
                case NORMAL:
                    if (!v.hasNormal())
                        builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                    else
                        builder.put(e, v.getNormal().x, v.getNormal().y, v.getNormal().z, 0);
                    break;
                default:
                    builder.put(e);
            }
        }
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return model != null ? model.customData.ambientOcclusion : true;
    }

    @Override
    public boolean isGui3d()
    {
        return model != null ? model.customData.gui3d : true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture()
    {
        return this.sprite;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public VertexFormat getFormat()
    {
        return format;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack)
    {
        return this;
    }

    @Override
    public OBJBakedModel handleBlockState(IBlockState state)
    {
        if (state instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState) state;
            if (exState.getUnlistedNames().contains(OBJProperty.instance))
            {
                OBJState s = exState.getValue(OBJProperty.instance);
                if (s != null)
                {
                    if (s.visibilityMap.containsKey(Group.ALL) || s.visibilityMap.containsKey(Group.ALL_EXCEPT))
                    {
                        this.updateStateVisibilityMap(s);
                    }
                    return getCachedModel(s);
                }
            }
        }
        return this;
    }

    private void updateStateVisibilityMap(OBJState state)
    {
        if (state.visibilityMap.containsKey(Group.ALL))
        {
            boolean operation = state.visibilityMap.get(Group.ALL);
            state.visibilityMap.clear();
            for (String s : this.model.getMatLib().getGroups().keySet())
            {
                state.visibilityMap.put(s,  state.operation.performOperation(operation));
            }
        }
        else if (state.visibilityMap.containsKey(Group.ALL_EXCEPT))
        {
            List<String> exceptList = state.getGroupNamesFromMap().subList(1, state.getGroupNamesFromMap().size());
            state.visibilityMap.remove(Group.ALL_EXCEPT);
            for (String s : this.model.getMatLib().getGroups().keySet())
            {
                if (!exceptList.contains(s))
                {
                    state.visibilityMap.put(s, state.operation.performOperation(state.visibilityMap.get(s)));
                }
            }
        }
        else
        {
            for (String s : state.visibilityMap.keySet())
            {
                state.visibilityMap.put(s, state.operation.performOperation(state.visibilityMap.get(s)));
            }
        }
    }

    private final Map<IModelState, OBJBakedModel> cache = new HashMap<IModelState, OBJBakedModel>();

    public OBJBakedModel getCachedModel(IModelState state)
    {
        if (!cache.containsKey(state))
        {
            cache.put(state, new OBJBakedModel(this.model, state, this.format, this.textures));
        }
        return cache.get(state);
    }

    public OBJModel getModel()
    {
        return this.model;
    }

    public IModelState getState()
    {
        return this.state;
    }

    public OBJBakedModel getBakedModel()
    {
        return new OBJBakedModel(this.model, this.state, this.format, this.textures);
    }

    @Override
    public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
    {
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, state, cameraTransformType);
    }

    @Override
    public String toString()
    {
        return this.model.modelLocation.toString();
    }
}
