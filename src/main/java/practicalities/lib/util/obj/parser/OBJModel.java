package practicalities.lib.util.obj.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.fml.common.FMLLog;
@SuppressWarnings("deprecation")
public class OBJModel implements IRetexturableModel, IModelCustomData {
    //private Gson GSON = new GsonBuilder().create();
    public MaterialLibrary matLib;
    public final ResourceLocation modelLocation;
    public CustomData customData;
    public EnumVendor vendor = EnumVendor.NONE;
    
    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation)
    {
        this(matLib, modelLocation, new CustomData());
    }

    public OBJModel(MaterialLibrary matLib, ResourceLocation modelLocation, CustomData customData)
    {
        this.matLib = matLib;
        this.modelLocation = modelLocation;
        this.customData = customData;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        Iterator<Material> materialIterator = this.matLib.materials.values().iterator();
        List<ResourceLocation> textures = Lists.newArrayList();
        while (materialIterator.hasNext())
        {
            Material mat = materialIterator.next();
            ResourceLocation textureLoc = new ResourceLocation(mat.getTexture().getPath());
            if (!textures.contains(textureLoc) && !mat.isWhite())
                textures.add(textureLoc);
        }
        return textures;
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
        builder.put(ModelLoader.White.loc.toString(), ModelLoader.White.instance);
        TextureAtlasSprite missing = bakedTextureGetter.apply(new ResourceLocation("missingno"));
        for (Map.Entry<String, Material> e : matLib.materials.entrySet())
        {
            if (e.getValue().getTexture().getTextureLocation().getResourcePath().startsWith("#"))
            {
                FMLLog.severe("OBJLoader: Unresolved texture '%s' for obj model '%s'", e.getValue().getTexture().getTextureLocation().getResourcePath(), modelLocation);
                builder.put(e.getKey(), missing);
            }
            else
            {
                builder.put(e.getKey(), bakedTextureGetter.apply(e.getValue().getTexture().getTextureLocation()));
            }
        }
        builder.put("missingno", missing);
        return new OBJBakedModel(this, state, format, builder.build());
    }

    public MaterialLibrary getMatLib()
    {
        return this.matLib;
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData)
    {
        OBJModel ret = new OBJModel(this.matLib, this.modelLocation, new CustomData(this.customData, customData));
        return ret;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures)
    {
        OBJModel ret = new OBJModel(this.matLib.makeLibWithReplacements(textures), this.modelLocation, this.customData);
        return ret;
    }

    static class CustomData
    {
        public boolean ambientOcclusion = true;
        public boolean gui3d = true;
        // should be an enum, TODO
        //public boolean modifyUVs = false;
        public boolean flipV = false;

        public CustomData(CustomData parent, ImmutableMap<String, String> customData)
        {
            this.ambientOcclusion = parent.ambientOcclusion;
            this.gui3d = parent.gui3d;
            this.flipV = parent.flipV;
            this.process(customData);
        }

        public CustomData() {}

        public void process(ImmutableMap<String, String> customData)
        {
            for (Map.Entry<String, String> e : customData.entrySet())
            {
                if (e.getKey().equals("ambient"))
                    this.ambientOcclusion = Boolean.valueOf(e.getValue());
                else if (e.getKey().equals("gui3d"))
                    this.gui3d = Boolean.valueOf(e.getValue());
                /*else if (e.getKey().equals("modifyUVs"))
                    this.modifyUVs = Boolean.valueOf(e.getValue());*/
                else if (e.getKey().equals("flip-v"))
                    this.flipV = Boolean.valueOf(e.getValue());
            }
        }
    }
    
    @SuppressWarnings("serial")
    public static class UVsOutOfBoundsException extends RuntimeException
    {
        public ResourceLocation modelLocation;

        public UVsOutOfBoundsException(ResourceLocation modelLocation)
        {
            super(String.format("Model '%s' has UVs ('vt') out of bounds 0-1! The missing model will be used instead. Support for UV processing will be added to the OBJ loader in the future.", modelLocation));
            this.modelLocation = modelLocation;
        }
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }
}