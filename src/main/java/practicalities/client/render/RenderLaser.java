package practicalities.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import practicalities.Logger;
import practicalities.blocks.TileLaser;
import practicalities.lib.util.vec.Vector3;

public class RenderLaser extends TileEntitySpecialRenderer<TileLaser> {

	@Override
	public void renderTileEntityAt(TileLaser te, double x, double y, double z, float partialTicks, int destroyStage) {
		Tessellator t = Tessellator.getInstance();
		WorldRenderer r = t.getWorldRenderer();
		
		r.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		
		Vector3 origin = Vector3.fromTile(te);
		
		te.eachLaser( (TileLaser.Laser l) -> renderLaser(l, origin) ); // why? BECAUSE I CAN!!! 1.8 baby!
		
		t.draw();		
	}
	
	public void renderLaser(TileLaser.Laser laser, Vector3 origin) {
		WorldRenderer r = Tessellator.getInstance().getWorldRenderer();
		Vector3 start = origin.copy().add(laser.direction, laser.start);
		Vector3 end   = origin.copy().add(laser.direction, laser.end);

		
		r.pos(start.x, start.y, start.z).color(1, 0, 0, 1);
		r.pos(end.x,   end.y,   end.z)  .color(1, 0, 0, 1);
	}

}
