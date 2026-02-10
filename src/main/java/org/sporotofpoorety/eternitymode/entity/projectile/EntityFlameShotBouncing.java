package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileBouncing;
import org.sporotofpoorety.eternitymode.util.ProjectileUtil;



public class EntityFlameShotBouncing extends EntityProjectileBouncing {


    int fireDuration;
    boolean shouldExplode;  
    float explosionPower;
    boolean explosionFire;
    boolean explosionDestruction;


    public EntityFlameShotBouncing(World worldIn) 
    {
        super(worldIn);

        this.setSize(0.5F, 0.5F);

        this.fireDuration = 20;
        this.shouldExplode = false;
        this.explosionPower = 0.5F;
        this.explosionFire = false;
        this.explosionDestruction = false;
    }

    public EntityFlameShotBouncing(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity,
    boolean acceleratesVertically, boolean bounceUpExtra, double bounceUpForce,
    int fireDuration, boolean shouldExplode, float explosionPower, boolean explosionFire, boolean explosionDestruction)
    {
        super(worldIn, owner,
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity,
        acceleratesVertically, bounceUpExtra, bounceUpForce);

        this.setSize(0.5F, 0.5F);

        this.fireDuration = fireDuration;
        this.shouldExplode = shouldExplode;
        this.explosionPower = explosionPower;
        this.explosionFire = explosionFire;
        this.explosionDestruction = explosionDestruction;
    }

    public EntityFlameShotBouncing(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity,
    boolean projectileTransitionShould, int projectileTransitionLength,
    boolean acceleratesVertically, boolean bounceUpExtra, double bounceUpForce,
    int fireDuration, boolean shouldExplode, float explosionPower, boolean explosionFire, boolean explosionDestruction)
    {
        super(worldIn, owner,
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity,
        projectileTransitionShould, projectileTransitionLength,
        acceleratesVertically, bounceUpExtra, bounceUpForce);

        this.setSize(0.5F, 0.5F);

        this.fireDuration = fireDuration;
        this.shouldExplode = shouldExplode;
        this.explosionPower = explosionPower;
        this.explosionFire = explosionFire;
        this.explosionDestruction = explosionDestruction;
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




    @Override
//Block hit logic
    public void onHitBlock(RayTraceResult rayTraceResult, Block blockHit)
    {
        super.onHitBlock(rayTraceResult, blockHit);

        if(this.bounceExtraEffectCooldown < 1)
        {           
            if(this.shouldExplode)
            {
                this.world.newExplosion(this, this.posX, this.posY + (this.explosionPower / 5.0F), this.posZ, this.explosionPower, this.explosionFire, this.explosionDestruction);
            }

            this.bounceExtraEffectCooldown = 10;
        }
    }




    public void onHitEntity(RayTraceResult rayTraceResult, Entity entityHit)
    {
        entityHit.setFire(this.fireDuration);

        super.onHitEntity(rayTraceResult, entityHit);
    }


    public void onTrueSetDead()
    {
        if(this.shouldExplode)
        {
            this.world.newExplosion(this, this.posX, this.posY + (this.explosionPower / 5.0F), this.posZ, this.explosionPower, this.explosionFire, this.explosionDestruction);
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
    }


    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("FireDuration")) { this.fireDuration = compound.getInteger("FireDuration"); }
        if (compound.hasKey("ShouldExplode")) { this.shouldExplode = compound.getBoolean("ShouldExplode"); }
        if (compound.hasKey("ExplosionPower")) { this.explosionPower = compound.getFloat("ExplosionPower"); }
        if (compound.hasKey("ExplosionFire")) { this.explosionFire = compound.getBoolean("ExplosionFire"); }
        if (compound.hasKey("ExplosionDestruction")) { this.explosionDestruction = compound.getBoolean("ExplosionDestruction"); }
    }

}
