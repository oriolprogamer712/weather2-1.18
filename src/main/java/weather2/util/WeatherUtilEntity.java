package weather2.util;

import com.corosus.coroutil.util.CoroUtilEntOrParticle;
import extendedrenderer.particle.entity.EntityRotFX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;

public class WeatherUtilEntity {
	
    public static float getWeight(Object entity1, boolean forTornado)
    {
    	Level world = CoroUtilEntOrParticle.getWorld(entity1);

        if (world == null) {
            return 1F;
        }

		if (isParticleRotServerSafe(world, entity1))
		{
			float var = WeatherUtilParticle.getParticleWeight((EntityRotFX)entity1);

			if (var != -1)
			{
				return var;
			}
		}

		if (entity1 instanceof Squid)
		{
			return 400F;
		}

        if (entity1 instanceof LivingEntity)
        {
        	LivingEntity livingEnt = (LivingEntity) entity1;
        	int airTime = livingEnt.getPersistentData().getInt("timeInAir");
        	if (livingEnt.isOnGround() || livingEnt.isInWater())
            {
                airTime = 0;
            }
            else {
            	airTime++;
            }
        	
        	livingEnt.getPersistentData().putInt("timeInAir", airTime);

			if (entity1 instanceof Player) {
				if (((Player) entity1).abilities.instabuild) return 99999999F;
				return 5.0F + airTime / 400.0F;
			} else {
				return 500.0F + (livingEnt.isOnGround() ? 2.0F : 0.0F) + (airTime / 400.0F);
			}
        }

        if (entity1 instanceof Boat || entity1 instanceof ItemEntity || entity1 instanceof FishingHook)
        {
            return 4000F;
        }

        if (entity1 instanceof AbstractMinecart)
        {
            return 80F;
        }

        return 1F;
    }



	public static float getWeight(Object entity1)
	{
		Level world = CoroUtilEntOrParticle.getWorld(entity1);

		if (world == null) {
			return 1F;
		}

		if (isParticleRotServerSafe(world, entity1))
		{
			float var = WeatherUtilParticle.getParticleWeight((EntityRotFX)entity1);

			if (var != -1)
			{
				return var;
			}
		}

		if (entity1 instanceof Squid)
		{
			return 400F;
		}

		if (entity1 instanceof LivingEntity)
		{
			LivingEntity livingEnt = (LivingEntity) entity1;
			int airTime = livingEnt.getPersistentData().getInt("timeInAir");
			if (livingEnt.isOnGround() || livingEnt.isInWater())
			{
				airTime = 0;
			}
			else {
				airTime++;
			}

			livingEnt.getPersistentData().putInt("timeInAir", airTime);

			if (entity1 instanceof Player) {
				if (((Player) entity1).abilities.instabuild) return 99999999F;
				return 5.0F + airTime / 400.0F;
			} else {
				return 500.0F + (livingEnt.isOnGround() ? 2.0F : 0.0F) + (airTime / 400.0F);
			}
		}

		if (entity1 instanceof Boat || entity1 instanceof ItemEntity || entity1 instanceof FishingHook)
		{
			return 4000F;
		}

		if (entity1 instanceof AbstractMinecart)
		{
			return 80F;
		}

		return 1F;
	}
    
    public static boolean isParticleRotServerSafe(Level world, Object obj) {
    	if (EffectiveSide.get().equals(LogicalSide.SERVER)) {
    		return false;
    	}
    	if (!world.isClientSide) return false;
    	return isParticleRotClientCheck(obj);
    }
    
    public static boolean isParticleRotClientCheck(Object obj) {
    	return obj instanceof EntityRotFX;
    }
    
    public static double getDistanceSqEntToPos(Entity ent, BlockPos pos) {
    	return ent.position().distanceToSqr(Vec3.atCenterOf(pos));
    }

	public static boolean isEntityOutside(Entity parEnt) {
		return isEntityOutside(parEnt, false);
	}

	public static boolean isEntityOutside(Entity parEnt, boolean cheapCheck) {
		return isPosOutside(parEnt.level, parEnt.position(), cheapCheck);
	}

	public static boolean isPosOutside(Level parWorld, Vec3 parPos) {
		return isPosOutside(parWorld, parPos, false);
	}

	public static boolean isPosOutside(Level parWorld, Vec3 parPos, boolean cheapCheck) {
		int rangeCheck = 5;
		int yOffset = 1;

		if (WeatherUtilBlock.getPrecipitationHeightSafe(parWorld, new BlockPos(Mth.floor(parPos.x), 0, Mth.floor(parPos.z))).getY() < parPos.y+1) return true;

		if (cheapCheck) return false;

		Vec3 vecTry = new Vec3(parPos.x + Direction.NORTH.getStepX()*rangeCheck, parPos.y+yOffset, parPos.z + Direction.NORTH.getStepZ()*rangeCheck);
		if (checkVecOutside(parWorld, parPos, vecTry)) {
			return true;
		}

		vecTry = new Vec3(parPos.x + Direction.SOUTH.getStepX()*rangeCheck, parPos.y+yOffset, parPos.z + Direction.SOUTH.getStepZ()*rangeCheck);
		if (checkVecOutside(parWorld, parPos, vecTry)) {
			return true;
		}

		vecTry = new Vec3(parPos.x + Direction.EAST.getStepX()*rangeCheck, parPos.y+yOffset, parPos.z + Direction.EAST.getStepZ()*rangeCheck);
		if (checkVecOutside(parWorld, parPos, vecTry)) {
			return true;
		}

		vecTry = new Vec3(parPos.x + Direction.WEST.getStepX()*rangeCheck, parPos.y+yOffset, parPos.z + Direction.WEST.getStepZ()*rangeCheck);
		if (checkVecOutside(parWorld, parPos, vecTry)) {
			return true;
		}

		return false;
	}

	public static boolean checkVecOutside(Level parWorld, Vec3 parPos, Vec3 parCheckPos) {
		BlockHitResult blockhitresult = parWorld.clip(new ClipContext(parPos, parCheckPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
		if (blockhitresult.getType() == HitResult.Type.MISS) {
			if (WeatherUtilBlock.getPrecipitationHeightSafe(parWorld, new BlockPos(Mth.floor(parCheckPos.x), 0, Mth.floor(parCheckPos.z))).getY() < parCheckPos.y) return true;
		}
		return false;
	}

	public static boolean isPlayerSheltered(Entity player) {
		int x = Mth.floor(player.getX());
		int y = Mth.floor(player.getY() + player.getEyeHeight());
		int z = Mth.floor(player.getZ());
		return player.level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) > y;
	}
}
