package org.sporotofpoorety.eternitymode.entity;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.EntityParticleSpiral;
import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShotLinear;



public class EntityParticleSpiralFire extends EntityParticleSpiral
{

    protected int setsFireLength;

    boolean fireballEnabled;
    int fireballAmount;
    
    int fireballLifetimeMax;

    double fireballSpeedHorizontal; 
    double fireballSpeedVertical;
    double fireballAccelerationRate; 
    double fireballGravitySpeed; 

    double fireballHitCheckSize; 
    boolean fireballProjectileStopsAtEntity; 
    boolean fireballProjectileStopsAtBlock;
    float fireballProjectileHitDamage;

    int fireballFireDuration;




    public EntityParticleSpiralFire(World world) 
    {
        super(world);
    }

    public EntityParticleSpiralFire(World world, double x, double y, double z,
    EntityLivingBase owner, 
    int lifetimeMax, int timeToArm,
    boolean doesDamage, int damageInterval, float damageAmount, double damageRadius, double damageHeight, 
    int particleLifetime, double visualRadius, double riseSpeed, int textureIndex,
    int setsFireLength, 
    boolean fireballEnabled, int fireballAmount,
    int fireballLifetimeMax, double fireballSpeedHorizontal, double fireballSpeedVertical,
    double fireballAccelerationRate, double fireballGravitySpeed,  
    double fireballHitCheckSize, boolean fireballProjectileStopsAtEntity, boolean fireballProjectileStopsAtBlock, float fireballProjectileHitDamage,
    int fireballFireDuration) 
    {
        super(world, x, y, z,
        owner, 
        lifetimeMax, timeToArm, 
        doesDamage, damageInterval, damageAmount, damageRadius, damageHeight,
        particleLifetime, visualRadius, riseSpeed, textureIndex);

        this.setsFireLength = setsFireLength;

        this.fireballEnabled = fireballEnabled;
        this.fireballAmount = fireballAmount;

        this.fireballLifetimeMax = fireballLifetimeMax;

        this.fireballSpeedHorizontal = fireballSpeedHorizontal; 
        this.fireballSpeedVertical = fireballSpeedVertical;
        this.fireballAccelerationRate = fireballAccelerationRate;
        this.fireballGravitySpeed = fireballGravitySpeed;

        this.fireballHitCheckSize = fireballHitCheckSize; 
        this.fireballProjectileStopsAtEntity = fireballProjectileStopsAtEntity; 
        this.fireballProjectileStopsAtBlock = fireballProjectileStopsAtBlock;
        this.fireballProjectileHitDamage = fireballProjectileHitDamage;

        this.fireballFireDuration = fireballFireDuration;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);

        compound.setInteger("SetsFireLength", setsFireLength);

        compound.setBoolean("FireballEnabled", this.fireballEnabled);
        compound.setInteger("FireballAmount", this.fireballAmount);

        compound.setInteger("FireballLifetimeMax", this.fireballLifetimeMax);

        compound.setDouble("FireballSpeedHorizontal", this.fireballSpeedHorizontal);
        compound.setDouble("FireballSpeedVertical", this.fireballSpeedVertical);
        compound.setDouble("FireballAccelerationRate", this.fireballAccelerationRate);
        compound.setDouble("FireballGravitySpeed", this.fireballGravitySpeed);

        compound.setDouble("FireballHitCheckSize", this.fireballHitCheckSize);
        compound.setBoolean("FireballProjectileStopsAtEntity", this.fireballProjectileStopsAtEntity);
        compound.setBoolean("FireballProjectileStopsAtBlock", this.fireballProjectileStopsAtBlock);
        compound.setFloat("FireballProjectileHitDamage", this.fireballProjectileHitDamage);

        compound.setInteger("FireballFireDuration", this.fireballFireDuration);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("SetsFireLength")) { setsFireLength = compound.getInteger("SetsFireLength"); }

        if (compound.hasKey("FireballEnabled")) { this.fireballEnabled = compound.getBoolean("FireballEnabled"); }
        if (compound.hasKey("FireballAmount")) { this.fireballAmount = compound.getInteger("FireballAmount"); }

        if (compound.hasKey("FireballLifetimeMax")) { this.fireballLifetimeMax = compound.getInteger("FireballLifetimeMax"); }

        if (compound.hasKey("FireballSpeedHorizontal")) { this.fireballSpeedHorizontal = compound.getDouble("FireballSpeedHorizontal"); }
        if (compound.hasKey("FireballSpeedVertical")) { this.fireballSpeedVertical = compound.getDouble("FireballSpeedVertical"); }
        if (compound.hasKey("FireballAccelerationRate")) { this.fireballAccelerationRate = compound.getDouble("FireballAccelerationRate"); }
        if (compound.hasKey("FireballGravitySpeed")) { this.fireballGravitySpeed = compound.getDouble("FireballGravitySpeed"); }

        if (compound.hasKey("FireballHitCheckSize")) { this.fireballHitCheckSize = compound.getDouble("FireballHitCheckSize"); } 
        if (compound.hasKey("FireballProjectileStopsAtEntity")) { this.fireballProjectileStopsAtEntity = compound.getBoolean("FireballProjectileStopsAtEntity"); }
        if (compound.hasKey("FireballProjectileStopsAtBlock")) { this.fireballProjectileStopsAtBlock = compound.getBoolean("FireballProjectileStopsAtBlock"); }
        if (compound.hasKey("FireballProjectileHitDamage")) { this.fireballProjectileHitDamage = compound.getFloat("FireballProjectileHitDamage"); }

        if (compound.hasKey("FireballFireDuration")) { this.fireballFireDuration = compound.getInteger("FireballFireDuration"); }
    }


    public void postArmLogic()
    {
        super.postArmLogic();



    }

    public void spiralDamageLogic()
    {
        List<EntityLivingBase> mobs =
        world.getEntitiesWithinAABB
        (
            EntityLivingBase.class,
            this.getEntityBoundingBox().offset(0.0D, damageHeight / 2.0D, 0.0D).grow(damageRadius, damageHeight, damageRadius)
        );
        
        for (EntityLivingBase mob : mobs) 
        {
            attackEntityFrom(DamageSource.GENERIC, this.damageAmount);
            mob.setFire(this.setsFireLength);
        }
    }

}
