package practicalities.client.render;

import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

@SuppressWarnings("deprecation")
public class LaserModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel {
	
	public static final ResourceLocation modelResourceLocation = new ResourceLocation("practicalities:crystal.obj");
	
	Object m;
	
	public LaserModel(Object existingModel) {
		this.m = existingModel;
	}
	
	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
		if(m instanceof IBakedModel) {
			return ((IBakedModel) m).getFaceQuads(p_177551_1_);
		}
		return null;
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		if(m instanceof IBakedModel) {
			return ((IBakedModel) m).getGeneralQuads();
		}
		return null;
	}

	@Override
	public boolean isAmbientOcclusion() {
		if(m instanceof IBakedModel) {
			return ((IBakedModel) m).isAmbientOcclusion();
		}
		return false;
	}

	@Override
	public boolean isGui3d() {
		if(m instanceof IBakedModel) {
			return ((IBakedModel) m).isGui3d();
		}
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		if(m instanceof IBakedModel) {
			return ((IBakedModel) m).isBuiltInRenderer();
		}
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		if(m instanceof IBakedModel) {
			return ((IBakedModel) m).getParticleTexture();
		}
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		if(m instanceof IBakedModel) {
			return ((IBakedModel) m).getItemCameraTransforms();
		}
		return null;
	}

	// ISMARTBLOCKMODEL
	
	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		if(m instanceof ISmartBlockModel) {
			return ((ISmartBlockModel) m).handleBlockState(state);
		}
		return null;
	}

	@Override
	public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		if(m instanceof IPerspectiveAwareModel) {
			return ((IPerspectiveAwareModel) m).handlePerspective(cameraTransformType);
		}
		return null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		if(m instanceof ISmartItemModel) {
			return ((ISmartItemModel) m).handleItemState(stack);
		}
		return null;
	}

	@Override
	public VertexFormat getFormat() {
		if(m instanceof IFlexibleBakedModel) {
			return ((IFlexibleBakedModel) m).getFormat();
		}
		return null;
	}

}
