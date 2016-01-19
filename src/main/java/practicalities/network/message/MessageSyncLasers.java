package practicalities.network.message;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import practicalities.blocks.TileLaser;
import practicalities.blocks.TileLaser.Laser;
import practicalities.lib.util.math.MathHelper;

public class MessageSyncLasers implements IMessage, IMessageHandler<MessageSyncLasers, IMessage>{
	
	BlockPos pos;
	public Laser current;
	public List<Laser> old;
	
	public MessageSyncLasers() {}
	
	public MessageSyncLasers(BlockPos pos, Laser current, List<Laser> old) {
		this.pos = pos;
		this.current = current;
		this.old = old;
	}
	
	@Override
	public IMessage onMessage(MessageSyncLasers message, MessageContext ctx) {
		IThreadListener thread = (IThreadListener) Minecraft.getMinecraft();
		thread.addScheduledTask( ()->{
			
			World world = Minecraft.getMinecraft().theWorld;
			TileEntity te = world.getTileEntity(message.pos);
			if(te != null && te instanceof TileLaser) { // in case for some reason we get a packet for a world other than theWorld
				TileLaser tileLaser = (TileLaser) te;
				tileLaser.syncFrom(message);
			}
			
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		old = new ArrayList<>();
		
		pos = BlockPos.fromLong(buf.readLong());
		boolean hasCurrent = buf.readBoolean();
		if(hasCurrent) {
			current = decodeLaser(buf);
		}
		int count = buf.readByte();
		for (int i = 0; i < count; i++) {
			old.add(decodeLaser(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(current != null);
		if(current != null) {
			encodeLaser(current, buf);
		}
		int size = old.size();
		for (Laser laser : old) {
			if(laser == null) size--;
		}
		buf.writeByte(size);
		for (Laser laser : old) {
			if(laser == null) continue;
			encodeLaser(laser, buf);
		}
	}
	
	public void encodeLaser(Laser laser, ByteBuf buf) {
		// the client only needs an approximation, so 16 bits is enough. We don't need to send 64 bits for each.
		buf.writeShort(MathHelper.floatToShort((float)laser.start));
		buf.writeShort(MathHelper.floatToShort((float)laser.end  ));
	}
	
	public Laser decodeLaser(ByteBuf buf) {
		Laser laser = new Laser();
		
		// the client only needs an approximation, so 16 bits is enough. We don't need to send 64 bits for each.
		laser.lastStart = laser.start = MathHelper.shortToFloat(buf.readShort());
		laser.lastEnd = laser.end = MathHelper.shortToFloat(buf.readShort());
		
		return laser;
	}

}
