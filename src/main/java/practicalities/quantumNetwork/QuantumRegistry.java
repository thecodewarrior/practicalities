package practicalities.quantumNetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import practicalities.PracticalitiesMod;
import practicalities.blocks.machine.quantumInterface.BlockQuantumInterface;
import practicalities.lib.common.BlockLocation;
import practicalities.registers.BlockRegister;

public class QuantumRegistry {
	public static final QuantumRegistry INSTANCE = new QuantumRegistry();
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		WorldData.get(event.world);
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		WorldData.get(event.world).markDirty();
	}
	
	@SubscribeEvent
	public void update(NeighborNotifyEvent event) {
		for (EnumFacing side : event.getNotifiedSides()) {
			checkForUpdates(event.world, event.pos.offset(side));
		}
	}
	
	public void checkForUpdates(World world, BlockPos pos) {
		String posName = new BlockLocation(world, pos).getString();
		for (Entry<String, String> loc : interfaceLinks.entrySet()) {
			if(posName.equals(loc.getValue())) {
				BlockLocation block = new BlockLocation( loc.getKey() );
				IBlockState state = block.getWorld().getBlockState(block.getPos());
				if(state.getBlock() == BlockRegister.quantumInterface) {
					block.getWorld().notifyNeighborsOfStateChange(block.getPos(), state.getBlock());
				}
			}
		}
	}
	
	public static Map<String, String> links = new HashMap<>();
	public static Map<String, String> interfaceLinks = new HashMap<>();
	
	public static class WorldData extends WorldSavedData {

		private static final String ID = PracticalitiesMod.TEXTURE_BASE + "Quantum";
		
		public WorldData(String name) {
			super(name);
		}
		
		public static WorldData get(World world) {
			if(world.getMapStorage() == null)
				return null;

			WorldData data = (WorldData) world.getMapStorage().loadData(WorldData.class, ID);

			if (data == null) {
				data = new WorldData(ID);
				data.markDirty();
				world.getMapStorage().setData(ID, data);
			}
			
			return data;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			links.clear();
			interfaceLinks.clear();
			
			NBTTagCompound linksTag = nbt.getCompoundTag("links");
			
			for(String key : linksTag.getKeySet()) {
				NBTBase tag = linksTag.getTag(key);
				if(tag instanceof NBTTagString) {
					String value = ((NBTTagString) tag).getString();
					links.put(key, value);
				}
			}
			
			NBTTagCompound interfaceTag = nbt.getCompoundTag("interfaceLinks");
			
			for(String key : interfaceTag.getKeySet()) {
				NBTBase tag = interfaceTag.getTag(key);
				if(tag instanceof NBTTagString) {
					String value = ((NBTTagString) tag).getString();
					interfaceLinks.put(key, value);
				}
			}
			

		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {
			NBTTagCompound linksTag = new NBTTagCompound();
			NBTTagCompound interfacesTag = new NBTTagCompound();
			
			
			for (String key : links.keySet()) {
				linksTag.setString(key, links.get(key));
			}
			for (String key : interfaceLinks.keySet()) {
				interfacesTag.setString(key, interfaceLinks.get(key));
			}
			
			nbt.setTag("links", linksTag);
			nbt.setTag("interfaceLinks", interfacesTag);
		}
	}
	
	public static void save(World world) {
		WorldData data = WorldData.get(world);
		if(data != null)
			data.markDirty();
	}
	
	public static void moveLinkLocation(String from, String to) {
		links.put(to, links.get(from));
		links.remove(from);
	}
	
	public static void moveInterfaceLocation(String from, String to) {
		interfaceLinks.put(to, interfaceLinks.get(from));
		interfaceLinks.remove(from);
	}
}
