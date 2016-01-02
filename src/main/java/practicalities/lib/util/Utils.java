package practicalities.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * 
 * @author TheCodeWarrior
 * @author Jotato
 * @author CoFH
 *
 */
public class Utils {
	
	public static void registerEventHandler(Object obj) {
		MinecraftForge.EVENT_BUS.register(obj);
	}
	
	/* BLOCK UTILS */
	
	public static String sterilizeLocation(World world, BlockPos pos) {
		return world.provider.getDimensionId() + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
	}
	
	public static int desterilizeWorldId(String sterilized) {
		String[] tokens = sterilized.split(":");
		return Integer.parseInt(tokens[0]);
	}
	
	public static World desterilizeWorld(String sterilized) {
		return getWorldById(desterilizeWorldId(sterilized));
	}
	
	public static World getWorldById(int id) {
		MinecraftServer server = MinecraftServer.getServer();
		if(server == null)
			return null;
		return server.worldServerForDimension(id);
	}
	
	public static BlockPos desterilizePos(String sterilized) {
		String[] tokens = sterilized.split(":");
		int x = Integer.parseInt(tokens[1]), y = Integer.parseInt(tokens[2]), z = Integer.parseInt(tokens[3]);
		return new BlockPos(x, y, z);
	}
	
	/* ITEM UTILS */
	
	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, false);
	}

	public static boolean dropItemStackIntoWorldWithVelocity(ItemStack stack, World world, double x, double y, double z) {

		return dropItemStackIntoWorld(stack, world, x, y, z, true);
	}

	public static boolean dropItemStackIntoWorld(ItemStack stack, World world, double x, double y, double z, boolean velocity) {

		if (stack == null) {
			return false;
		}
		float x2 = 0.5F;
		float y2 = 0.0F;
		float z2 = 0.5F;

		if (velocity) {
			x2 = world.rand.nextFloat() * 0.8F + 0.1F;
			y2 = world.rand.nextFloat() * 0.8F + 0.1F;
			z2 = world.rand.nextFloat() * 0.8F + 0.1F;
		}
		EntityItem entity = new EntityItem(world, x + x2, y + y2, z + z2, stack.copy());

		if (velocity) {
			entity.motionX = (float) world.rand.nextGaussian() * 0.05F;
			entity.motionY = (float) world.rand.nextGaussian() * 0.05F + 0.2F;
			entity.motionZ = (float) world.rand.nextGaussian() * 0.05F;
		} else {
			entity.motionY = -0.05F;
			entity.motionX = 0;
			entity.motionZ = 0;
		}
		world.spawnEntityInWorld(entity);

		return true;
	}
	
	@SideOnly(Side.CLIENT)
    private static double getBlockReachDistance_client() {
        return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
	
	private static double getBlockReachDistance_server(EntityPlayerMP player) {
        return player.theItemInWorldManager.getBlockReachDistance();
    }
	
	public static double getBlockReachDistance(EntityPlayer player) {
        return player.worldObj.isRemote ? getBlockReachDistance_client() :
                player instanceof EntityPlayerMP ? getBlockReachDistance_server((EntityPlayerMP) player) : 5D;
    }
}
