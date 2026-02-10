package org.sporotofpoorety.eternitymode.entity.projectile;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShotLinear;
import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileHoming;
import org.sporotofpoorety.eternitymode.util.AbsurdcraftMathUtils;
import org.sporotofpoorety.eternitymode.util.ProjectileUtil;



public class EntityFlameShotHoming extends EntityProjectileHoming {


    int fireDuration;
    boolean shouldExplode;  
    float explosionPower;
    boolean explosionFire;
    boolean explosionDestruction;

    boolean shouldSplit;

    boolean splitExplodes; 
    float splitExplosionPower;
    boolean splitFire;
    boolean splitDestruction;


    public EntityFlameShotHoming(World worldIn) 
    {
        super(worldIn);

        this.setSize(2.0F, 2.0F);

        this.fireDuration = 20;
        this.shouldExplode = true;
        this.explosionPower = 0.5F;
        this.explosionFire = false;
        this.explosionDestruction = false;

        this.shouldSplit = false;

        this.splitExplodes = true;
        this.splitExplosionPower = 0.5F;
        this.splitFire = false;
        this.splitDestruction = false;
    }

    public EntityFlameShotHoming(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage,
    int particleLifetime, int particleDensity, double particleVelocity,
    int timePreHomingMax, boolean homingTimeHasMax, int homingTimeMax, double homingSpeed, int homingMode, 
    int fireDuration, boolean shouldExplode, float explosionPower, boolean explosionFire, boolean explosionDestruction)
    {
        super(worldIn, owner,
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,
        particleLifetime, particleDensity, particleVelocity,
        timePreHomingMax, homingTimeHasMax, homingTimeMax, homingSpeed, homingMode);

        this.setSize(0.5F, 0.5F);

        this.fireDuration = fireDuration;
        this.shouldExplode = shouldExplode;
        this.explosionPower = explosionPower;
        this.explosionFire = explosionFire;
        this.explosionDestruction = explosionDestruction;

        this.shouldSplit = false;

        this.splitExplodes = true;
        this.splitExplosionPower = 0.5F;
        this.splitFire = false;
        this.splitDestruction = false;
    }

    public EntityFlameShotHoming(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage,
    int particleLifetime, int particleDensity, double particleVelocity,
    int splitProjectileCount, double splitConeRadians, int splitAimMode,
    float splitDamage, double splitSpeed, double splitAcceleration,
    int timePreHomingMax, boolean homingTimeHasMax, int homingTimeMax, double homingSpeed, int homingMode, 
    int fireDuration, boolean shouldExplode, float explosionPower, boolean explosionFire, boolean explosionDestruction,
    boolean shouldSplit, boolean splitExplodes, float splitExplosionPower, boolean splitFire, boolean splitDestruction)
    {
        super(worldIn, owner,
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,
        particleLifetime, particleDensity, particleVelocity,
        timePreHomingMax, homingTimeHasMax, homingTimeMax, homingSpeed, homingMode);

        this.setSize(0.5F, 0.5F);

        this.splitProjectileCount = splitProjectileCount; 
        this.splitConeRadians = splitConeRadians; 
        this.splitAimMode = splitAimMode;
        this.splitDamage = splitDamage; 
        this.splitSpeed = splitSpeed; 
        this.splitAcceleration = splitAcceleration;

        this.fireDuration = fireDuration;
        this.shouldExplode = shouldExplode;
        this.explosionPower = explosionPower;
        this.explosionFire = explosionFire;
        this.explosionDestruction = explosionDestruction;

        this.shouldSplit = shouldSplit;

        this.splitExplodes = splitExplodes;
        this.splitExplosionPower = splitExplosionPower;
        this.splitFire = splitFire;
        this.splitDestruction = splitDestruction;
    }




    @Override
    public void onUpdate()
    {
        super.onUpdate();


		if(world.isRemote)
        {
            ProjectileUtil.particlesFireball(this, this.particleLifetime, this.particleDensity, this.particleVelocity);
		}
    }




    public void onHitEntity(RayTraceResult rayTraceResult, Entity entityHit)
    {
        entityHit.setFire(this.fireDuration);

        super.onHitEntity(rayTraceResult, entityHit);
    }


    public void onTrueSetDead()
    {
        if(this.shouldSplit) { this.randomKaboom(); }

        if(this.shouldExplode)
        {
            this.world.newExplosion(this, this.posX, this.posY + (0.5F / 5.0F), this.posZ, this.explosionPower, this.explosionFire, this.explosionDestruction);
        }
    }




    public void randomKaboom()
    {
        if(this.owner == null || !(this.owner instanceof EntityLiving)) 
        {
            ProjectileUtil.shootAimedFireballSpreadCoord(null, this, null,
            this.posX, this.posY, this.posZ, 
            this.splitProjectileCount, this.splitConeRadians, this.splitAimMode,
            this.splitDamage, this.splitSpeed, this.splitAcceleration,
            this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);  
        }
        else 
        {
/*
            ProjectileUtil.shootAimedFireballSpreadEntity(this.owner, this, ((EntityLiving) this.owner).getAttackTarget(), 
            this.splitProjectileCount, this.splitConeRadians, this.splitAimMode,
            this.splitDamage, this.splitSpeed, this.splitAcceleration,
            this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);
*/
            ProjectileUtil.shootAimedFireballSpreadCoord(this.owner, this, ((EntityLiving) this.owner).getAttackTarget(),
            this.posX, this.posY + 0.5D, this.posZ, 
            this.splitProjectileCount, this.splitConeRadians, this.splitAimMode,
            this.splitDamage, this.splitSpeed, this.splitAcceleration,
            this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);      
        }
    }



	@Override
	public boolean canRenderOnFire()
    {
		return false;
	}




    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);

        compound.setInteger("FireDuration", this.fireDuration);
        compound.setBoolean("ShouldExplode", this.shouldExplode);
        compound.setFloat("ExplosionPower", this.explosionPower);
        compound.setBoolean("ExplosionFire", this.explosionFire);
        compound.setBoolean("ExplosionDestruction", this.explosionDestruction);

        compound.setBoolean("ShouldSplit", this.shouldSplit);

        compound.setBoolean("SplitExplodes", this.splitExplodes);
        compound.setFloat("SplitExplosionPower", this.splitExplosionPower);
        compound.setBoolean("SplitFire", this.splitFire);
        compound.setBoolean("SplitDestruction", this.splitDestruction);
    }


    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("FireDuration")) { this.fireDuration = compound.getInteger("FireDuration"); }
        if (compound.hasKey("ShouldExplode")) { this.shouldExplode = compound.getBoolean("ShouldExplode"); }
        if (compound.hasKey("ExplosionPower")) { this.explosionPower = compound.getFloat("ExplosionPower"); }
        if (compound.hasKey("ExplosionFire")) { this.explosionFire = compound.getBoolean("ExplosionFire"); }
        if (compound.hasKey("ExplosionDestruction")) { this.explosionDestruction = compound.getBoolean("ExplosionDestruction"); }

        if (compound.hasKey("ShouldSplit")) { this.shouldSplit = compound.getBoolean("ShouldSplit"); }

        if (compound.hasKey("SplitExplodes")) { this.splitExplodes = compound.getBoolean("SplitExplodes"); }
        if (compound.hasKey("SplitExplosionPower")) { this.splitExplosionPower = compound.getFloat("SplitExplosionPower"); }
        if (compound.hasKey("SplitFire")) { this.splitFire = compound.getBoolean("SplitFire"); }
        if (compound.hasKey("SplitDestruction")) { this.splitDestruction = compound.getBoolean("SplitDestruction"); }
    }

}
