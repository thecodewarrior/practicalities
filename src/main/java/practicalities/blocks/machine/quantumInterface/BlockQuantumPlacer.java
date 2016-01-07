package practicalities.blocks.machine.quantumInterface;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import practicalities.helpers.fakeplayer.PRFakePlayer;
import practicalities.helpers.fakeplayer.PRFakePlayerFactory;
import practicalities.lib.common.BlockFacingBase;
import practicalities.lib.common.BlockLocation;
import practicalities.lib.common.DropCapture;
import practicalities.lib.common.DropCapture.CaptureContext;
import practicalities.lib.util.Utils;
import practicalities.quantumNetwork.QuantumRegistry;

public class BlockQuantumPlacer extends BlockFacingBase {

	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockQuantumPlacer() {
		super(Material.rock, "quantumPlacer", null);
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
		
		if(!wasPowered && powered) {
			breakTarget(world, pos);
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.TRUE));
		} else if(wasPowered && !powered){
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.FALSE));
		}
		
		
	}
	
	public void breakTarget(World world, BlockPos pos) {
		if(!(world instanceof WorldServer)) return;
		
		EnumFacing facing = world.getBlockState(pos).getValue(BlockFacingBase.FACING);
		
		String interfaceLoc = new BlockLocation((World)world, pos.offset(facing)).getString();
		String link = QuantumRegistry.interfaceLinks.get(interfaceLoc);
		if(link == null) return;
		BlockLocation bound = new BlockLocation( link );
		
		List<EntityItem> toSearch = world.getEntitiesWithinAABB(EntityItem.class, Utils.inBlockSpace(pos.offset(facing.getOpposite())));
		
		for(EntityItem entity : toSearch) {
			ItemStack stack = entity.getEntityItem();
			if( stack.getItem() instanceof ItemBlock ) {
				ItemStack newStack = placeBlock(bound.getWorld(), bound.getPos(), stack, facing, PRFakePlayerFactory.getPlayer((WorldServer)world));
				entity.setEntityItemStack(newStack);
				break;
			}
		}
	}
	
	public ItemStack placeBlock(World world, BlockPos pos, ItemStack stack, EnumFacing facing, PRFakePlayer fakePlayer) {
		stack.onItemUse(fakePlayer, world, pos, facing, 0, 0, 0);
		return stack;
	}
}
