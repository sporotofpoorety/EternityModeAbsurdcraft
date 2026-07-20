package org.sporotofpoorety.eternitymode.entity.projectile;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileLinear;




public class EntityDarkShotLinear extends EntityProjectileLinear 
{

    public EntityDarkShotLinear(World worldIn) 
    {
        super(worldIn);

        this.setSize(1.0F, 1.0F);
    }

    public EntityDarkShotLinear(World worldIn, double x, double y, double z, 
    EntityLivingBase owner,
    int lifetimeMax, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity)
    {
        super(worldIn, x, y, z,
        owner,
        lifetimeMax, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity);

        this.setSize(1.0F, 1.0F);
    }




    @Override
    public void onUpdate()
    {
        super.onUpdate();
    }




	@Override
	public boolean canRenderOnFire()
    {
		return false;
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
