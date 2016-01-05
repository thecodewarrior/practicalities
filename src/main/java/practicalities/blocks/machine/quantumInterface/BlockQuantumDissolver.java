package practicalities.blocks.machine.quantumInterface;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import practicalities.Logger;
import practicalities.helpers.fakeplayer.PRFakePlayer;
import practicalities.helpers.fakeplayer.PRFakePlayerFactory;
import practicalities.lib.common.BlockFacingBase;
import practicalities.lib.common.BlockLocation;
import practicalities.lib.common.DropCapture;
import practicalities.lib.common.DropCapture.CaptureContext;
import practicalities.quantumNetwork.QuantumRegistry;

public class BlockQuantumDissolver extends BlockFacingBase {

	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockQuantumDissolver() {
		super(Material.rock, "quantumDissolver", null);
	}

	@Override
	public void setupStates(IBlockState state) {
		state.withProperty(POWERED, Boolean.FALSE);
		super.setupStates(state);
	}
	
	@Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { FACING, POWERED });
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | ( state.getValue(POWERED) ? 8 : 0);
	}
	
	@Override
	public List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops,
			boolean simulate) {
		return null;
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		if(world.isRemote)
			return;
		boolean wasPowered = state.getValue(POWERED);
		boolean powered = world.isBlockPowered(pos);
		EnumFacing facing = world.getBlockState(pos).getValue(BlockFacingBase.FACING);
		
		if(!wasPowered && powered) {
			List<EntityItem> items = breakTarget(world, pos);
			
			BlockPos itemPos = pos.offset(facing.getOpposite());
			
			if(items != null) {
				for (EntityItem item : items) {
//					MinecraftServer.getServer().getConfigurationManager().transferEntityToWorld(this, i, worldserver, worldserver1);
//					item.travelToDimension(world.provider.getDimensionId());
					item.setPosition(itemPos.getX()+0.5, itemPos.getY(), itemPos.getZ()+0.5);
					item.setWorld(world);
//					item.dimension = world.provider.getDimensionId();
					world.spawnEntityInWorld(item);
				}
			}
			
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.TRUE));
		} else if(wasPowered && !powered){
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.FALSE));
		}
		
		
	}
	
	public List<EntityItem> breakTarget(World world, BlockPos pos) {
		if(!(world instanceof WorldServer)) return null;
		
		EnumFacing facing = world.getBlockState(pos).getValue(BlockFacingBase.FACING);
		
		String interfaceLoc = new BlockLocation((World)world, pos.offset(facing)).getString();
		String link = QuantumRegistry.interfaceLinks.get(interfaceLoc);
		if(link == null) return null;
		BlockLocation bound = new BlockLocation( link );
		
		List<EntityItem> items = breakBlock(bound.getWorld(), bound.getPos().offset(facing), PRFakePlayerFactory.getPlayer((WorldServer)world));
		return items;
	}
	
	public List<EntityItem> breakBlock(World world, BlockPos pos, PRFakePlayer fakePlayer) {
		fakePlayer.inventory.currentItem = 0;
		fakePlayer.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond_pickaxe, 0, 0));

		if (!world.canMineBlockBody(fakePlayer, pos)) return Lists.newArrayList();

		final IBlockState block = world.getBlockState(pos);

		CaptureContext dropsCapturer = DropCapture.instance.start(pos);

		final List<EntityItem> drops;
		try {
			BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, block, fakePlayer);
			if (MinecraftForge.EVENT_BUS.post(event)) return Lists.newArrayList();

			boolean canHarvest = block.getBlock().canHarvestBlock(world, pos, fakePlayer);

			block.getBlock().onBlockHarvested(world, pos, block, fakePlayer);
			boolean canRemove = block.getBlock().removedByPlayer(world, pos, fakePlayer, canHarvest);

			if (canRemove) {
				block.getBlock().onBlockDestroyedByPlayer(world, pos, block);
				if (canHarvest) {
					block.getBlock().harvestBlock(world, fakePlayer, pos, block, world.getTileEntity(pos));
				}
				world.playAuxSFX(2001, pos, Block.getIdFromBlock(block.getBlock()) + (block.getBlock().getMetaFromState(block) << 12));
			}
		} finally {
			drops = dropsCapturer.stop();
		}

		return drops;
	}
}
