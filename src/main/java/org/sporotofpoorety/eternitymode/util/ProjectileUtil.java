package org.sporotofpoorety.eternitymode.util;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

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

			double dx = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
			double dy = (fireball.world.rand.nextDouble() - 0.5) * fireball.height + (fireball.height / 2) - 0.1; // -0.1 because flames aren't centred
			double dz = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
			double v = particleVelocity;
			ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
					.pos(fireball.getPositionVector().add(dx - fireball.motionX / 2, dy, dz - fireball.motionZ / 2))
					.vel(-v * dx, -v * dy, -v * dz).scale(fireball.width * 2).time(particleLifetime).spawn(fireball.world);

			if(fireball.ticksExisted > 1)
            {
				dx = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
				dy = (fireball.world.rand.nextDouble() - 0.5) * fireball.height + (fireball.height / 2) - 0.1;
				dz = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
						.pos(fireball.getPositionVector().add(dx - fireball.motionX, dy, dz - fireball.motionZ))
					    .vel(-v * dx, -v * dy, -v * dz).scale(fireball.width * 2).time(particleLifetime).spawn(fireball.world);
			}
		}
    }




    public static ArrayList<Vec3d> aimedFibonacciSpread
    (double aimX, double aimY, double aimZ, double targetX, double targetY, double targetZ,
    int pointCount, double coneRadians)
    {
//Fibonacci spread aimed from one point to another
        return AbsurdcraftMathUtils.fibonacciDirectionalSpread
            (new Vec3d(targetX - aimX, targetY - aimY, targetZ - aimZ), 
                pointCount, coneRadians);     
    }


    public static ArrayList<Vec3d> predictiveFibonacciSpread(double aimX, double aimY, double aimZ,
    Entity vecTarget, double aimerSpeedFactor,
    int pointCount, double coneRadians)
    {
        Vec3d predictiveVec = AbsurdcraftMathUtils.generatePredictiveAimVectorNoVertical(aimX, aimY, aimZ, vecTarget, aimerSpeedFactor);

//Fibonacci spread aimed from 
//aim pos to target entity's predicted movement
        return AbsurdcraftMathUtils.fibonacciDirectionalSpread
            (new Vec3d(predictiveVec.x, predictiveVec.y, predictiveVec.z), 
                pointCount, coneRadians);
    }




//Shotgun with entity-based position to aim from
    public static ArrayList<Vec3d> flexibleFibonnaciShotgunEntity
    (Entity aimerEntity, Entity targetEntity,
    int projectileCount, double coneRadians, int aimMode, double shotSpeed)
    {
//Directions to shoot at
        ArrayList<Vec3d> spreadDirections = new ArrayList<>();


//If target is valid
        if(targetEntity != null)
        {
//Direct aim
            if(aimMode == 0) 
            { 
                spreadDirections.addAll(aimedFibonacciSpread(
                    aimerEntity.posX, aimerEntity.posY, aimerEntity.posZ, 
                    targetEntity.posX, targetEntity.posY, targetEntity.posZ, projectileCount, coneRadians)); 
            }
//Predictive aim
            if(aimMode == 1) 
            { 
                spreadDirections.addAll(predictiveFibonacciSpread(
                    aimerEntity.posX, aimerEntity.posY, aimerEntity.posZ, targetEntity, shotSpeed, projectileCount, coneRadians)); 
            }
        }
//If no valid target
        else
        {
//Random base direction
            Vec3d randomDirection = new Vec3d(
                aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble());

//Make fibonacci spread
            spreadDirections.addAll(AbsurdcraftMathUtils.fibonacciDirectionalSpread(randomDirection, projectileCount, coneRadians));
        }


//Return shotgun vectors
        return spreadDirections;
    }


//Shotgun with manual position to aim from
    public static ArrayList<Vec3d> flexibleFibonnaciShotgunCoord
    (double aimX, double aimY, double aimZ, 
    Entity aimerEntity, Entity targetEntity,
    int projectileCount, double coneRadians, int aimMode, double shotSpeed)
    {
//Directions to shoot at
        ArrayList<Vec3d> spreadDirections = new ArrayList<>();


//If target is valid
        if(targetEntity != null)
        {
//Direct aim
            if(aimMode == 0) 
            { 
                spreadDirections.addAll(aimedFibonacciSpread(
                    aimX, aimY, aimZ,
                    targetEntity.posX, targetEntity.posY, targetEntity.posZ, projectileCount, coneRadians)); 
            }
//Predictive aim
            if(aimMode == 1) 
            { 
                spreadDirections.addAll(predictiveFibonacciSpread(
                    aimX, aimY, aimZ, targetEntity, shotSpeed, projectileCount, coneRadians)); 
            }
        }
//If no valid target
        else
        {
//Random base direction
            Vec3d randomDirection = new Vec3d(
                aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble());

//Make fibonacci spread
            spreadDirections.addAll(AbsurdcraftMathUtils.fibonacciDirectionalSpread(randomDirection, projectileCount, coneRadians));
        }


//Return shotgun vectors
        return spreadDirections;
    }




//Shoot aimer-based shotgun
    public static void shootAimedFireballSpreadEntity(EntityLivingBase ownerEntity, Entity aimerEntity, Entity targetEntity,
    int projectileCount, double coneRadians, int aimMode, 
    float shotDamage, double shotSpeed, double shotAcceleration, 
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
            EntityFlameShotLinear entitySplit = new EntityFlameShotLinear(aimerEntity.world, ownerEntity,
            aimerEntity.posX, aimerEntity.posY, aimerEntity.posZ,
            100, 
            currentDirection.x * shotSpeed, currentDirection.y * shotSpeed, currentDirection.z * shotSpeed,
            shotAcceleration, 0.0D,
            0.3D, true, true, shotDamage,
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
    public static void shootAimedFireballSpreadCoord(EntityLivingBase ownerEntity, Entity aimerEntity, Entity targetEntity,
    double manualX, double manualY, double manualZ, 
    int projectileCount, double coneRadians, int aimMode, 
    float shotDamage, double shotSpeed, double shotAcceleration, 
    boolean shotExplodes, float shotExplosionPower, boolean shotFire, boolean shotDestruction)
    {
//Get shotgun vectors
        ArrayList<Vec3d> spreadDirections = flexibleFibonnaciShotgunCoord(manualX, manualY, manualZ, aimerEntity, targetEntity, projectileCount, coneRadians, aimMode, shotSpeed);


//Now for each projectile vector generated
        for(int projectileAt = 0; projectileAt < projectileCount; projectileAt++)
        {
//Get its direction
            Vec3d currentDirection = spreadDirections.get(projectileAt);

//Make new entity
            EntityFlameShotLinear entitySplit = new EntityFlameShotLinear(aimerEntity.world, ownerEntity,
            manualX, manualY, manualZ,
            100,
            currentDirection.x * shotSpeed, currentDirection.y * shotSpeed, currentDirection.z * shotSpeed,
            shotAcceleration, 0.0D,
            0.3D, true, true, shotDamage,
            2, 2, 0.06D,
            20, shotExplodes, shotExplosionPower, shotFire, shotDestruction);

//(Bugfix) should spawn at aimer entity now
            if(aimerEntity != null)
            {
                entitySplit.setPosition(manualX, manualY, manualZ);
            }

//Spawn it
            aimerEntity.world.spawnEntity(entitySplit);
        }
    }
}
