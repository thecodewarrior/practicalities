package practicalities.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import practicalities.PracticalitiesMod;
import practicalities.blocks.TileLaser;
import practicalities.lib.client.RenderUtils;
import practicalities.lib.util.vec.Vector3;

public class RenderLaser extends TileEntitySpecialRenderer<TileLaser> {

    private final ResourceLocation laserTexture = new ResourceLocation(PracticalitiesMod.MODID, "textures/misc/laser.png");
	
	@Override
	public void renderTileEntityAt(TileLaser te, double x, double y, double z, float partialTicks, int destroyStage) {
		Tessellator t = Tessellator.getInstance();
		WorldRenderer r = t.getWorldRenderer();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		
		GlStateManager.resetColor();
		GL11.glColor3f(1, 1, 1);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(laserTexture);
		
		RenderUtils.setBrightnessByBlockLight(15);
		
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableCull();
		r.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		r.noColor();
		
		EnumFacing direction = te.facing;
		Vector3 origin = Vector3.center;
		
		te.eachLaser( (TileLaser.Laser l) -> renderLaser(l, origin, direction, partialTicks) ); // why? BECAUSE I CAN!!! Java 8 baby!
		
		t.draw();
		
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		
		GlStateManager.popMatrix();
	}
	
	public void renderLaser(TileLaser.Laser laser, Vector3 origin, EnumFacing direction, float partialTicks) {
		WorldRenderer r = Tessellator.getInstance().getWorldRenderer();
		Vector3 start = origin.copy().add(direction, laser.lastStart + ( (laser.start - laser.lastStart)*partialTicks ) );
		Vector3 end   = origin.copy().add(direction, laser.lastEnd + ( (laser.end - laser.lastEnd)*partialTicks ) );
		
		double d = 1F/8F;
		
		if(direction.getAxis() != Axis.Y) {
			r.pos(start.x, start.y-d, start.z).tex(1, 1).endVertex();
			r.pos(start.x, start.y+d, start.z).tex(1, 0).endVertex();
			r.pos(end.x,   end.y+d,   end.z)  .tex(0, 0).endVertex();
			r.pos(end.x,   end.y-d,   end.z)  .tex(0, 1).endVertex();
		}
	
		if(direction.getAxis() != Axis.Z) {
			r.pos(start.x, start.y, start.z-d).tex(1, 1).endVertex();
			r.pos(start.x, start.y, start.z+d).tex(1, 0).endVertex();
			r.pos(end.x,   end.y,   end.z+d)  .tex(0, 0).endVertex();
			r.pos(end.x,   end.y,   end.z-d)  .tex(0, 1).endVertex();
		}
		
		if(direction.getAxis() != Axis.X) {
			r.pos(start.x-d, start.y, start.z).tex(1, 1).endVertex();
			r.pos(start.x+d, start.y, start.z).tex(1, 0).endVertex();
			r.pos(end.x+d,   end.y,   end.z)  .tex(0, 0).endVertex();
			r.pos(end.x-d,   end.y,   end.z)  .tex(0, 1).endVertex();
		}
	}

}
