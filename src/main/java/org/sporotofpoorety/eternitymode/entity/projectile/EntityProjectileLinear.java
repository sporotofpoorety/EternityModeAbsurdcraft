package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileRaytrace;



public class EntityProjectileLinear extends EntityProjectileRaytrace {


    public EntityProjectileLinear(World worldIn) 
    {
        super(worldIn);

        this.setSize(0.5F, 0.5F);
    }

    public EntityProjectileLinear(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity) 
    {
        super(worldIn, owner,
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity);

        this.setSize(0.5F, 0.5F);
    }




    @Override
    public void onUpdate()
    {
        if(!this.firstBeenShot)
        {
            this.speedApply();

            this.firstBeenShot = true;
        }

        this.accelerationApply();

        super.onUpdate();
    }


    @Override
    public void speedApply()
    {
        motionX = this.speedX;
        motionY = this.speedY;
        motionZ = this.speedZ;
    }


    @Override
    public void accelerationApply()
    {
        motionX *= this.accelerationRate;
        motionY *= this.accelerationRate;
        motionZ *= this.accelerationRate;


//Increase acceleration tracker
        this.accelerationCurrent *= this.accelerationRate;
    }




    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);
    }


    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);
    }

}
