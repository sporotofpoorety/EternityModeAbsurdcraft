package org.sporotofpoorety.eternitymode.entity;


import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import electroblob.wizardry.registry.WizardryBlocks;

import org.sporotofpoorety.eternitymode.entity.EntityMeteorBlock;
import org.sporotofpoorety.eternitymode.util.ExplosionUtil;




public class EntityMeteorBlockHoming extends EntityMeteorBlock
{

    public boolean firstBeenShot;

    public boolean isPreHoming;
    public int timePreHoming;
    public int timePreHomingMax;
    boolean isHoming;
    public int homingTime;
    public boolean homingTimeHasMax;
    public int homingTimeMax;
    public double homingSpeed;
    public int homingMode;
    public boolean isPostHoming;

    public boolean homingSnapEnabled; 
    public boolean homingSnapVertical; 
    public double homingSnapThreshold;
    public double homingSnapSteps;




    public EntityMeteorBlockHoming(World worldIn)
    {
        super(worldIn);
    }

    public EntityMeteorBlockHoming(World worldIn, double x, double y, double z, 
    EntityLivingBase owner, boolean dealsDamage, float thrownBlockDamage,
    int lifetimeMax, double speedX, double speedY, double speedZ, 
    double accelerationVal, double gravitySpeed,
    float explosionPower, boolean explosionFire, boolean explosionDestruction,
    int splitProjectileCount, double splitConeRadians, int splitAimMode,
    int splitLifetime, float splitDamage, double splitSpeed, double splitAcceleration,
    boolean shouldSplit, boolean splitExplodes, float splitExplosionPower, boolean splitFire, boolean splitDestruction,
    int timePreHomingMax, boolean homingTimeHasMax, int homingTimeMax, double homingSpeed, int homingMode,
    boolean homingSnapEnabled, boolean homingSnapVertical, double homingSnapThreshold, double homingSnapSteps)
    {
        super(worldIn, x, y, z, owner, dealsDamage, thrownBlockDamage,
        lifetimeMax, speedX, speedY, speedZ, 
        accelerationVal, gravitySpeed,
        explosionPower, explosionFire, explosionDestruction,
        splitProjectileCount, splitConeRadians, splitAimMode,
        splitLifetime, splitDamage, splitSpeed, splitAcceleration,
        shouldSplit, splitExplodes, splitExplosionPower, splitFire, splitDestruction);

        this.firstBeenShot = false;

        this.isPreHoming = false;
        this.timePreHoming = 0;
        this.timePreHomingMax = timePreHomingMax;
        this.isHoming = false;
        this.homingTime = 0;
        this.homingTimeHasMax = homingTimeHasMax;
        this.homingTimeMax = homingTimeMax;
        this.homingSpeed = homingSpeed;
        this.homingMode = homingMode;
        this.isPostHoming = false;

        this.homingSnapEnabled = homingSnapEnabled;
        this.homingSnapVertical = homingSnapVertical;
        this.homingSnapThreshold = homingSnapThreshold;
        this.homingSnapSteps = homingSnapSteps;
    }


    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);


        compound.setBoolean("FirstBeenShot", this.firstBeenShot);

        compound.setBoolean("IsPreHoming", this.isPreHoming);
        compound.setInteger("TimePreHoming", this.timePreHoming);
        compound.setInteger("TimePreHomingMax", this.timePreHomingMax);
        compound.setBoolean("IsHoming", this.isHoming);
        compound.setInteger("HomingTime", this.homingTime);
        compound.setBoolean("HomingTimeHasMax", this.homingTimeHasMax);
        compound.setInteger("HomingTimeMax", this.homingTimeMax);
        compound.setDouble("HomingSpeed", this.homingSpeed);
        compound.setInteger("HomingMode", this.homingMode);
        compound.setBoolean("IsPostHoming", this.isPostHoming);

        compound.setBoolean("HomingSnapEnabled", this.homingSnapEnabled);
        compound.setBoolean("HomingSnapVertical", this.homingSnapVertical);
        compound.setDouble("HomingSnapThreshold", this.homingSnapThreshold);
        compound.setDouble("HomingSnapSteps", this.homingSnapSteps);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);


        if (compound.hasKey("FirstBeenShot")) { this.firstBeenShot = compound.getBoolean("FirstBeenShot"); }

        if (compound.hasKey("IsPreHoming")) { this.isPreHoming = compound.getBoolean("IsPreHoming"); }
        if (compound.hasKey("TimePreHoming")) { this.timePreHoming = compound.getInteger("TimePreHoming"); }
        if (compound.hasKey("TimePreHomingMax")) { this.timePreHomingMax = compound.getInteger("TimePreHomingMax"); }
        if (compound.hasKey("IsHoming")) { this.isHoming = compound.getBoolean("IsHoming"); }
        if (compound.hasKey("HomingTime")) { this.homingTime = compound.getInteger("HomingTime"); }
        if (compound.hasKey("HomingTimeHasMax")) { this.homingTimeHasMax = compound.getBoolean("HomingTimeHasMax"); }
        if (compound.hasKey("HomingTimeMax")) { this.homingTimeMax = compound.getInteger("HomingTimeMax"); }
        if (compound.hasKey("HomingSpeed")) { this.homingSpeed = compound.getDouble("HomingSpeed"); }
        if (compound.hasKey("HomingMode")) { this.homingMode = compound.getInteger("HomingMode"); }
        if (compound.hasKey("IsPostHoming")) { this.isPostHoming = compound.getBoolean("IsPostHoming"); }

        if (compound.hasKey("HomingSnapEnabled")) { this.homingSnapEnabled = compound.getBoolean("HomingSnapEnabled"); }
        if (compound.hasKey("HomingSnapVertical")) { this.homingSnapVertical = compound.getBoolean("HomingSnapVertical"); }
        if (compound.hasKey("HomingSnapThreshold")) { this.homingSnapThreshold = compound.getDouble("HomingSnapThreshold"); }
        if (compound.hasKey("HomingSnapSteps")) { this.homingSnapSteps = compound.getDouble("HomingSnapSteps"); }
    }




    public void meteorLogic()
    {
        super.meteorLogic();

//Determine homing state
        this.determineHomingState();

//Pre-homing
        if(this.isPreHoming) { this.projectilePreHoming(); }
        
//Homing
        if(this.isHoming) 
        { 
            if(this.homingMode == 0) { this.projectileHomingAbsolute(); }
            if(this.homingMode == 1) { this.projectileHomingRelative(); }
        }

//Post-homing
        if(this.isPostHoming) { this.projectilePostHoming(); }
    }


    public void determineHomingState()
    {
//If projectile not shot yet
        if(!this.firstBeenShot)
        {
//Set been shot
            this.firstBeenShot = true;


//If it has a pre-homing phase
            if(this.timePreHomingMax > 0)
            {
//Then set to pre-homing
                this.isPreHoming = true;
            }
//If it has no homing phase
            else
            {
//Set to be homing
                this.isHoming = true;
            }
        }
    }


    public void projectilePreHoming()
    {
        if(++this.timePreHoming >= this.timePreHomingMax)
        {
            this.projectileHomingStart();
        }
    }


    public void projectileHomingStart()
    {
        this.isPreHoming = false;
        this.isHoming = true;
    }


    public void projectilePostHoming()
    {
        this.isHoming = false;
        this.isPostHoming = true;
    }


    public void projectileHomingAbsolute()
    {
//If owner is a living entity
        if(this.owner != null && this.owner instanceof EntityLiving)
        {
//With a target
            EntityLivingBase ownerTarget = ((EntityLiving)owner).getAttackTarget();
            if(ownerTarget != null)
            {
//Get distance vector (normalized)
                Vec3d targetDistNormalized = new Vec3d
                (
                    ownerTarget.posX - this.posX,
                    (ownerTarget.posY + (ownerTarget.height / 2.0D) + 1.0D) - this.posY,
                    ownerTarget.posZ - this.posZ
                );

//Simple homing
                this.motionX = targetDistNormalized.x * this.homingSpeed;
                this.motionY = targetDistNormalized.y * this.homingSpeed;
                this.motionZ = targetDistNormalized.z * this.homingSpeed;
            }
        }

        
        if(this.homingTimeHasMax && (++this.homingTime >= this.homingTimeMax))
        {
            this.projectilePostHoming();
        }
    }


    public void projectileHomingRelative()
    {
//If owner is a living entity
        if(this.owner != null && this.owner instanceof EntityLiving)
        {
//With a target
            EntityLivingBase ownerTarget = ((EntityLiving)owner).getAttackTarget();
            if(ownerTarget != null)
            {
//Get distance
                double targetDistX = ownerTarget.posX - this.posX;
                double targetDistY = (ownerTarget.posY + (ownerTarget.height / 2.0D) + 1.0D) - this.posY;
                double targetDistZ = ownerTarget.posZ - this.posZ;

//Accelerate based on how far target is
                double extraX = targetDistX * (0.01D * this.homingSpeed);
                double extraY = targetDistY * (0.01D * this.homingSpeed);
                double extraZ = targetDistZ * (0.01D * this.homingSpeed);

//Can snap to target if strayed too far
                if(this.homingSnapEnabled)
                {
                    if(this.homingSnapVertical)
                    {
                        if(Math.sqrt(targetDistX * targetDistX + targetDistY * targetDistY + targetDistZ * targetDistZ) >= this.homingSnapThreshold)
                        {
                            this.motionX = extraX * this.homingSnapSteps;
                            this.motionY = extraY * this.homingSnapSteps;
                            this.motionZ = extraZ * this.homingSnapSteps;
                        }
                    }
                    else
                    {
                        if(Math.sqrt(targetDistX * targetDistX + targetDistZ * targetDistZ) >= this.homingSnapThreshold)
                        {
                            this.motionX = extraX * this.homingSnapSteps;
                            this.motionY = extraY * this.homingSnapSteps;
                            this.motionZ = extraZ * this.homingSnapSteps;
                        }
                    }
                }

                this.motionX += extraX;
                this.motionY += extraY;
                this.motionZ += extraZ;
            }
        }

        if(this.homingTimeHasMax && (++this.homingTime > this.homingTimeMax))
        {
            this.projectilePostHoming();
        }
    }

}
