package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShotLinear;
import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileRaytrace;



public class EntityProjectileHoming extends EntityProjectileRaytrace {

    boolean isPreHoming;
    int timePreHoming;
    int timePreHomingMax;
    boolean isHoming;
    int homingTime;
    boolean homingTimeHasMax;
    int homingTimeMax;
    double homingSpeed;
    int homingMode;
    boolean isPostHoming;


    public EntityProjectileHoming(World worldIn) 
    {
        super(worldIn);

        this.setSize(0.5F, 0.5F);

        this.isPreHoming = false;
        this.timePreHoming = 0;
        this.timePreHomingMax = 0;
        this.isHoming = false;
        this.homingTime = 0;
        this.homingTimeHasMax = false;
        this.homingTimeMax = 200;
        this.homingSpeed = 0.1D;
        this.homingMode = 0;
        this.isPostHoming = false;
    }

    public EntityProjectileHoming(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ,
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage,
    int particleLifetime, int particleDensity, double particleVelocity,
    int timePreHomingMax, boolean homingTimeHasMax, int homingTimeMax, double homingSpeed, int homingMode) 
    {
        super(worldIn, owner, 
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,
        particleLifetime, particleDensity, particleVelocity);

        this.setSize(0.5F, 0.5F);

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
    }




    @Override
    public void onUpdate()
    {
//Initial speed
        this.speedApply();

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

//Acceleration
        this.accelerationApply();

        super.onUpdate();
    }


    @Override
    public void speedApply()
    {
//If projectile not shot yet
        if(!this.firstBeenShot)
        {
//Set been shot
            this.firstBeenShot = true;


//If it has a pre-homing phase, apply initial speed
            if(this.timePreHomingMax > 0)
            {
//Then set to pre-homing
                this.isPreHoming = true;
//And apply an initial speed
                this.motionX = this.speedX;
                this.motionY = this.speedY;
                this.motionZ = this.speedZ;
            }


//If it has no homing phase
            else
            {
//Set to be homing
                this.isHoming = true;
            }
        }
    }


    @Override
    public void accelerationApply()
    {
        if(this.isPreHoming)
        {
            this.motionX *= this.accelerationRate;
            this.motionY *= this.accelerationRate;
            this.motionZ *= this.accelerationRate;
        }
        if(this.isHoming)
        {
            if(this.homingMode == 0)
            {
                this.motionX *= this.accelerationCurrent;
                this.motionY *= this.accelerationCurrent;
                this.motionZ *= this.accelerationCurrent;
            }
        }
        if(this.isPostHoming)
        {
            this.motionX *= this.accelerationRate;
            this.motionY *= this.accelerationRate;
            this.motionZ *= this.accelerationRate;
        }


//Increase acceleration tracker
        this.accelerationCurrent *= this.accelerationRate;
    }




    public void projectilePreHoming()
    {
        if(++this.timePreHoming >= this.timePreHomingMax)
        {
            this.projectileHomingStart();
        }
    }


    public boolean projectileHomingConditions()
    {
        return true;
    }


    public void projectileHomingStart()
    {
        this.isPreHoming = false;
        this.isHoming = true;
    }


    public void projectileHomingAbsolute()
    {
//If owner is a living entity
        if(this.owner != null && this.owner instanceof EntityLiving
//With a target 
        && ((EntityLiving) this.owner).getAttackTarget() != null)
        {
//Get target
            EntityLivingBase ownerTarget = ((EntityLiving) this.owner).getAttackTarget();

//Get distance vector (normalized)
            Vec3d targetDistNormalized = new Vec3d
            (
                ownerTarget.posX - this.posX,
                ownerTarget.posY - this.posY,
                ownerTarget.posZ - this.posZ
            );

//Simple homing
            this.motionX = targetDistNormalized.x * this.homingSpeed;
            this.motionY = targetDistNormalized.y * this.homingSpeed;
            this.motionZ = targetDistNormalized.z * this.homingSpeed;
        }

        
        if(this.homingTimeHasMax && (++this.homingTime >= this.homingTimeMax))
        {
            this.projectilePostHoming();
        }
    }


    public void projectileHomingRelative()
    {
//If owner is a living entity
        if(this.owner != null && this.owner instanceof EntityLiving
//With a target 
        && ((EntityLiving) this.owner).getAttackTarget() != null)
        {
//Get target
            EntityLivingBase ownerTarget = ((EntityLiving) this.owner).getAttackTarget();

//Get distance
            double targetDistX = ownerTarget.posX - this.posX;
            double targetDistY = ownerTarget.posY - this.posY;
            double targetDistZ = ownerTarget.posZ - this.posZ;

//Accelerate based on how far target is
            this.motionX += targetDistX * (0.01D * this.homingSpeed);
            this.motionY += targetDistY * (0.01D * this.homingSpeed);
            this.motionZ += targetDistZ * (0.01D * this.homingSpeed);
        }

        if(this.homingTimeHasMax && (++this.homingTime > this.homingTimeMax))
        {
            this.projectilePostHoming();
        }
    }


    public void projectilePostHoming()
    {
        this.isHoming = false;
        this.isPostHoming = true;
    }




    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);


        compound.setBoolean("IsPreHoming", isPreHoming);
        compound.setInteger("TimePreHoming", timePreHoming);
        compound.setInteger("TimePreHomingMax", timePreHomingMax);
        compound.setBoolean("IsHoming", isHoming);
        compound.setInteger("HomingTime", homingTime);
        compound.setBoolean("HomingTimeHasMax", homingTimeHasMax);
        compound.setInteger("HomingTimeMax", homingTimeMax);
        compound.setDouble("HomingSpeed", homingSpeed);
        compound.setInteger("HomingMode", homingMode);
        compound.setBoolean("IsPostHoming", isPostHoming);
    }


    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("IsPreHoming")) { isPreHoming = compound.getBoolean("IsPreHoming"); }
        if (compound.hasKey("TimePreHoming")) { timePreHoming = compound.getInteger("TimePreHoming"); }
        if (compound.hasKey("TimePreHomingMax")) { timePreHomingMax = compound.getInteger("TimePreHomingMax"); }
        if (compound.hasKey("IsHoming")) { isHoming = compound.getBoolean("IsHoming"); }
        if (compound.hasKey("HomingTime")) { homingTime = compound.getInteger("HomingTime"); }
        if (compound.hasKey("HomingTimeHasMax")) { homingTimeHasMax = compound.getBoolean("HomingTimeHasMax"); }
        if (compound.hasKey("HomingTimeMax")) { homingTimeMax = compound.getInteger("HomingTimeMax"); }
        if (compound.hasKey("HomingSpeed")) { homingSpeed = compound.getDouble("HomingSpeed"); }
        if (compound.hasKey("HomingMode")) { homingMode = compound.getInteger("HomingMode"); }
        if (compound.hasKey("IsPostHoming")) { isPostHoming = compound.getBoolean("IsPostHoming"); }
    }

}
