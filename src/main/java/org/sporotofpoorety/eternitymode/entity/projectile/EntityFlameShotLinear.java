package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileLinear;
import org.sporotofpoorety.eternitymode.util.ParticleUtil;



public class EntityFlameShotLinear extends EntityProjectileLinear {


    int fireDuration;
    boolean shouldExplode;  
    float explosionPower;
    boolean explosionFire;
    boolean explosionDestruction;


    public EntityFlameShotLinear(World worldIn) 
    {
        super(worldIn);
    }

    public EntityFlameShotLinear(World worldIn, double x, double y, double z, 
    EntityLivingBase owner,
    int lifetimeMax, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity,
    int fireDuration, boolean shouldExplode, float explosionPower, boolean explosionFire, boolean explosionDestruction)
    {
        super(worldIn, x, y, z,
        owner,
        lifetimeMax, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity);

        this.setSize(1.0F, 1.0F);

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
            ParticleUtil.particlesFireball(this, this.particleLifetime, this.particleDensity, this.particleVelocity);
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
