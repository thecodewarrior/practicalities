package practicalities.lib.common;

import java.util.ArrayList;
import java.util.List;

import akka.dispatch.sysmsg.Create;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import practicalities.PracticalitiesMod;
import practicalities.lib.util.Utils;
import practicalities.registers.BlockRenderQueue;

/**
 * 
 * @author TheCodeWarrior
 * @author CoFH
 */
public abstract class BlockBase extends Block {

	public static CreativeTabs currentTab = CreativeTabs.tabMisc;
	
	public static int renderPass = 0;
	public static final ArrayList<ItemStack> NO_DROP = new ArrayList<ItemStack>();

	public boolean isSpawnable = true, isCube = true;
	
	protected String name;
	
	public BlockBase(Material material, String name, Class<? extends ItemBlock> itemClass) {
		super(material);
		
		this.name = name;
		
		setStepSound(soundTypeStone);
		setUnlocalizedName(name);
		setCreativeTab(BlockBase.currentTab);
		
		if(itemClass == null) {
			GameRegistry.registerBlock(this, name);
		} else {
			GameRegistry.registerBlock(this, itemClass, name);
		}
		
		BlockRenderQueue.add(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockModel() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(Item.getItemFromBlock(this), 0, new ModelResourceLocation(PracticalitiesMod.TEXTURE_BASE + name, "inventory"));
	}

	/* CUSTOM SETTERS/GETTERS */
	public void setSpawnable() { isSpawnable = true; }
	public void setUnSpawnable() { isSpawnable = false; }
	public void setCube() { isCube = true; }
	public void setNonCube() { isCube = false; }
	
	/* STUFF */
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase) {
			TileCoFHBase theTile = (TileCoFHBase) tile;
			theTile.blockBroken();
		}
		if (tile instanceof IInventory) {
			IInventory inv = (IInventory) tile;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				Utils.dropItemStackIntoWorldWithVelocity(inv.getStackInSlot(i), world, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5);
			}
		}
		if (tile != null) {
			world.removeTileEntity(pos);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return super.getCollisionBoundingBox(world, pos, state);
	}
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
		super.harvestBlock(worldIn, player, pos, state, te);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockState(pos, Blocks.air.getDefaultState(), 7);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCoFHBase) {
			((TileCoFHBase) tile).onNeighborBlockChange();
			((TileCoFHBase) tile).blockPlaced();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase) {
			((TileCoFHBase) tile).onNeighborBlockChange();
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase) {
			((TileCoFHBase) tile).onNeighborTileChange(pos);
		}
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos) {
		return ForgeHooks.blockStrength(world.getBlockState(pos), player, world, pos);
	}
	
	@Override
	public int getComparatorInputOverride(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof TileCoFHBase ? ((TileCoFHBase) tile).getComparatorInput() : 0;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileCoFHBase && tile.getWorld() != null) {
			return ((TileCoFHBase) tile).getLightValue();
		}
		return 0;
	}

	@Override
	public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
		return isSpawnable;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return isCube;
	}

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam) {
		TileEntity tile = world.getTileEntity(pos);
		return tile != null ? tile.receiveClientEvent(eventID, eventParam) : false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		return false;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return dismantleBlock(null, world, pos, false, true);
	}
	
	
// TODO: Figure out how to do this
//	@Override
//	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
//		Item item = Item.getItemFromBlock(this);
//
//		if (item == null) {
//			return null;
//		}
//		new ItemStack( this, world.getBlockState(pos) )
//		world.getBlockState(pos).
//		int bMeta = world.getBlockState(pos);//getBlockMetadata(pos);
//		ItemStack pickBlock = new ItemStack(item, 1, bMeta);
//		pickBlock.setTagCompound(getItemStackTag(world, pos));
//
//		return pickBlock;
//	}
	
	public NBTTagCompound getItemStackTag(World world, BlockPos pos) {
		return null;
	}

	public abstract List<ItemStack> dismantleBlock(EntityPlayer player, IBlockAccess world, BlockPos pos, boolean returnDrops, boolean simulate);

}
