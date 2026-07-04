package org.sporotofpoorety.eternitymode.util;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import electroblob.wizardry.util.ParticleBuilder;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShotLinear;
import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileLinear;
import org.sporotofpoorety.eternitymode.util.AbsurdcraftMathUtils;



public final class ProjectileUtil {


    public static void particlesFireball(Entity fireball,
    int particleLifetime, int particleDensity, double particleVelocity)
    {
    	for(int i = 0; i < particleDensity; i++)
        {
/*
			double dx = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
			double dy = (fireball.world.rand.nextDouble() - 0.5) * fireball.height + (fireball.height / 2) - 0.1; // -0.1 because flames aren't centred
			double dz = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
			double v = particleVelocity;
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
					.pos(fireball.getPositionVector().add(dx - fireball.motionX / 2, dy, dz - fireball.motionZ / 2))
					.vel(-v * dx, -v * dy, -v * dz).scale(fireball.width * 2).time(particleLifetime).spawn(fireball.world);
*/

			if(fireball.ticksExisted > 1)
            {
				double dx = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
				double dy = (fireball.world.rand.nextDouble() - 0.5) * fireball.height + (fireball.height / 2) - 0.1;
				double dz = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
			    double v = particleVelocity;
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
						.pos(fireball.getPositionVector().add(dx - fireball.motionX, dy, dz - fireball.motionZ))
					    .vel(-v * dx, -v * dy, -v * dz).scale(fireball.width * 2).time(particleLifetime).spawn(fireball.world);
			}
		}
    }




//Spherical shotgun (linear)
    public static ArrayList<Vec3d> fibonacciSpreadAimed
    (double aimX, double aimY, double aimZ, 
    double targetX, double targetY, double targetZ,
    int pointCount, double coneRadians)
    {
//From one point to another
        return AbsurdcraftMathUtils.fibonacciDirectionalSpread
            (new Vec3d(targetX - aimX, targetY - aimY, targetZ - aimZ), 
                pointCount, coneRadians);     
    }


//Spherical shotgun (predictive)
    public static ArrayList<Vec3d> fibonacciSpreadPredictive
    (World worldIn, Entity aimerEntity, Entity vecTarget, 
    double aimX, double aimY, double aimZ, double projVel,
    int pointCount, double coneRadians,
    boolean canPredictVertical, double verticalThreshold)
    {
//If target is valid
        if(vecTarget != null)
        {
//Get prediction vector
            Vec3d predictiveVec = AbsurdcraftMathUtils.calcPredictiveAimDynamicVertical
                (new Vec3d(vecTarget.posX - aimX, vecTarget.posY - aimY, vecTarget.posZ - aimZ), vecTarget, projVel, canPredictVertical, verticalThreshold);

//Fibonacci spread aimed from 
//aim pos to target entity's predicted movement
            return AbsurdcraftMathUtils.fibonacciDirectionalSpread
                (new Vec3d(predictiveVec.x, predictiveVec.y, predictiveVec.z), 
                    pointCount, coneRadians);
        }
        else
        {
//Random base direction
            Vec3d randomDirection = new Vec3d(
                aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble());

//Make fibonacci spread
            return AbsurdcraftMathUtils.fibonacciDirectionalSpread(randomDirection, pointCount, coneRadians);
        }
    }




/*
//Shoot aimer-based shotgun
    public static void shootAimedFireballSpreadEntity(EntityLivingBase ownerEntity, Entity aimerEntity, Entity targetEntity,
    int projectileCount, double coneRadians, int aimMode, 
    int shotLifetime, float shotDamage, double shotSpeed, double shotAcceleration, 
    boolean shotExplodes, float shotExplosionPower, boolean shotFire, boolean shotDestruction)
    {
//Get shotgun vectors
        ArrayList<Vec3d> spreadDirections = flexibleFibonnaciShotgunEntity(aimerEntity, targetEntity, projectileCount, coneRadians, aimMode, shotSpeed);


//Now for each projectile vector generated
        for(int projectileAt = 0; projectileAt < projectileCount; projectileAt++)
        {
//Get its direction
            Vec3d currentDirection = spreadDirections.get(projectileAt);

//Make new entity
            EntityFlameShotLinear entitySplit = new EntityFlameShotLinear(aimerEntity.world, aimerEntity.posX, aimerEntity.posY, aimerEntity.posZ,
            ownerEntity,
            shotLifetime, 
            currentDirection.x * shotSpeed, currentDirection.y * shotSpeed, currentDirection.z * shotSpeed,
            shotAcceleration, 0.0D,
            0.6D, true, true, shotDamage,
            2, 2, 0.06D,
            20, shotExplodes, shotExplosionPower, shotFire, shotDestruction);

//(Bugfix) should spawn at aimer entity now
            if(aimerEntity != null)
            {
                entitySplit.setPosition(aimerEntity.posX, aimerEntity.posY, aimerEntity.posZ);
            }

//Spawn it
            aimerEntity.world.spawnEntity(entitySplit);
        }
    }


//Shoot coord-origin-based shotgun
    public static void shootAimedFireballSpreadCoord(double x, double y, double z, 
    EntityLivingBase ownerEntity, Entity aimerEntity, Entity targetEntity,
    int projectileCount, double coneRadians, int aimMode, 
    int shotLifetime, float shotDamage, double shotSpeed, double shotAcceleration, 
    boolean shotExplodes, float shotExplosionPower, boolean shotFire, boolean shotDestruction)
    {
//Get shotgun vectors
        ArrayList<Vec3d> spreadDirections = flexibleFibonnaciShotgunCoord(x, y, z, aimerEntity, targetEntity, projectileCount, coneRadians, aimMode, shotSpeed);


//Now for each projectile vector generated
        for(int projectileAt = 0; projectileAt < projectileCount; projectileAt++)
        {
//Get its direction
            Vec3d currentDirection = spreadDirections.get(projectileAt);

//Make new entity
            EntityFlameShotLinear entitySplit = new EntityFlameShotLinear(aimerEntity.world, x, y, z,
            ownerEntity,
            shotLifetime,
            currentDirection.x * shotSpeed, currentDirection.y * shotSpeed, currentDirection.z * shotSpeed,
            shotAcceleration, 0.0D,
            0.6D, true, true, shotDamage,
            1, 2, 0.06D,
            20, shotExplodes, shotExplosionPower, shotFire, shotDestruction);

//(Bugfix) should spawn at aimer entity now
            if(aimerEntity != null)
            {
                entitySplit.setPosition(x, y, z);
            }

//Spawn it
            aimerEntity.world.spawnEntity(entitySplit);
        }
    }
*/
}
