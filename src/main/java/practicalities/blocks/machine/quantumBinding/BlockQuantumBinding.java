package practicalities.blocks.machine.quantumBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import practicalities.Logger;
import practicalities.PracticalitiesMod;
import practicalities.lib.common.BlockBase;
import practicalities.lib.util.Utils;

public class BlockQuantumBinding extends BlockBase {

	public BlockQuantumBinding() {
		super(Material.gourd, "quantumBinding", null);
		
		Utils.registerEventHandler(this);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops, boolean simulate) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		BlockLocation loc = new BlockLocation(world, pos);
		loc.data = 0;
		blocksMoved.add(loc);
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		WorldData.get(event.world);
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		WorldData.get(event.world).markDirty();
	}
	
	public static Map<String, String> links = new HashMap<>();
	public static List<BlockLocation> blocksMoved = new ArrayList<>();
	
	public static class WorldData extends WorldSavedData {

		private static final String ID = PracticalitiesMod.TEXTURE_BASE + "Quantum";
		
		public WorldData(String name) {
			super(name);
			// TODO Auto-generated constructor stub
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
			BlockQuantumBinding.links.clear();

			for(String key : nbt.getKeySet()) {
				NBTBase tag = nbt.getTag(key);
				if(tag instanceof NBTTagString) {
					String value = ((NBTTagString) tag).getString();
					BlockQuantumBinding.links.put(key, value);
				}
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {
			for (String key : BlockQuantumBinding.links.keySet()) {
				nbt.setString(key, BlockQuantumBinding.links.get(key));
			}
		}
	}
	
	public void save(World world) {
		WorldData data = WorldData.get(world);
		if(data != null)
			data.markDirty();
	}
	
	public static class BlockLocation {
		
		private World world;
		private BlockPos pos;
		
		public int data;
		
		public World getWorld() { return world; }
		public BlockPos getPos() { return pos; }
		
		public void setWorld(World w) { world = w; }
		public void setPos(BlockPos p) { pos = p; }
		
		public BlockLocation(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}
		protected BlockLocation() {}
		
		public String getString() {
			return Utils.sterilizeLocation(getWorld(), getPos());
		}
	}
	
	@SubscribeEvent
	public void tickEnd(TickEvent event) {
		if(event.type == Type.SERVER && event.phase == Phase.END) {
			for(BlockLocation loc : blocksMoved) {
				if(--loc.data <= 0) {
					processBlockMovement(loc);
				}
			}
			List<BlockLocation> blocksCopy = new ArrayList<>(blocksMoved);
			for(BlockLocation loc : blocksCopy) {
				if(loc.data <= 0) {
					blocksMoved.remove(loc);
				}
			}
		}
	}
	
	public void processBlockMovement(BlockLocation loc) {
		World world = loc.getWorld();
		IBlockState state = world.getBlockState(loc.getPos());
		String origPosStr = loc.getString();
		String newPosStr = origPosStr;
		
		if( state.getBlock() == Blocks.piston_extension ) {
			EnumFacing dir = state.getValue(BlockPistonExtension.FACING);
			BlockPistonExtension.EnumPistonType type = state.getValue(BlockPistonExtension.TYPE);
			
			BlockPos newPos = loc.getPos().offset(dir);
			
			if(world.isAirBlock(newPos)) {
				world.setBlockState(newPos, this.getDefaultState());
				loc.setPos(newPos);
			} else if(!world.isRemote) {
				ItemStack stack = new ItemStack(this);
				Utils.dropItemStackIntoWorld(stack, world, newPos.getX()+.5, newPos.getY()+.5, newPos.getZ()+.5);
			}
			
			newPosStr = loc.getString();
			
			boolean contains = links.containsKey(origPosStr);
			boolean isSticky = type == EnumPistonType.STICKY;
			if( !isSticky && contains ) {
				if(links.get(origPosStr) == null) {
					links.remove(origPosStr);
					return;
				}
				String posStr = links.get(origPosStr);
				Logger.info("Moving %s to the %s", posStr, dir.getName());
				World linkWorld = Utils.desterilizeWorld(posStr);
				BlockPos linkPos = Utils.desterilizePos(posStr);
				
				if( moveBlocks(linkWorld, linkPos, dir) ) {
					String newLinkPosStr = Utils.sterilizeLocation(linkWorld, linkPos.offset(dir));
//					for (String pos : links.keySet()) {
//						String linkStr = links.get(pos);
//						if(linkStr.equals(posStr)) {
//							links.put(pos, newLinkPosStr);
//						}
//					}
					links.put(origPosStr, newLinkPosStr);
				}
			}
			links.put(newPosStr,  links.get(origPosStr));
			links.remove(origPosStr);
		} else {
			links.remove(origPosStr);
		}
		
		save(world);
	}
	
	public boolean moveBlocks(World world, BlockPos from, EnumFacing dir) {
//		if( canMove(world, from, dir) ) {
			
		boolean isBlocked = false;
		int toMove = 0;
		
		for (int i = 0; i <= 12; i++) {
			BlockPos test = from.offset(dir, i);
			if(canMoveOver(world, test, dir)) {
				break;
			}
			if(! canMove(world, test, dir) ) {
				isBlocked = true;
				break;
			}
			toMove++;
		}
		
		if(isBlocked)
			return false;
		
		for (int i = toMove; i >= 0; i--) {
			BlockPos test = from.offset(dir, i);
			moveBlock(world, test, dir);
		}
		
		return true;
//		}
//		return false;
	}
	
	public void moveBlock(World world, BlockPos pos, EnumFacing dir) {
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos.offset(dir), state);
		world.setBlockToAir(pos);
	}
	
	public boolean canMove(World world, BlockPos pos, EnumFacing dir) {
		IBlockState state = world.getBlockState(pos);
		Material material = state.getBlock().getMaterial();
		TileEntity tile = world.getTileEntity(pos);
		
		return tile == null && material.getMaterialMobility() == 0 && state.getBlock().getBlockHardness(world, pos) != -1;
	}
	
	public boolean canMoveOver(World world, BlockPos pos, EnumFacing dir) {
		IBlockState state = world.getBlockState(pos);
		Material material = state.getBlock().getMaterial();
		
		return  world.isAirBlock(pos) || material.isReplaceable();
	}
}
