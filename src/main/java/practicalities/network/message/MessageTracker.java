package practicalities.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import practicalities.Logger;
import practicalities.PracticalitiesMod;
import practicalities.lib.util.track.TrackerClient;
import practicalities.lib.util.track.TrackerManager;

public class MessageTracker implements IMessage, IMessageHandler<MessageTracker, IMessage>{
	
	String key, value;
	
	public MessageTracker() {}
	
	public MessageTracker(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public IMessage onMessage(MessageTracker message, MessageContext ctx) {
		IThreadListener thread = (IThreadListener) Minecraft.getMinecraft();
		thread.addScheduledTask( ()->{
				TrackerManager.client.trackPacket(message.key, message.value);
			});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		key   = ByteBufUtils.readUTF8String(buf);
		value = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, key);
		ByteBufUtils.writeUTF8String(buf, value);
	}

}
