package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileLinear;
import org.sporotofpoorety.eternitymode.util.ProjectileUtil;



public class EntityProjectileBouncing extends EntityProjectileLinear {


    boolean acceleratesVertically;
    boolean isBouncing;
    boolean bounceUpExtra;
    double bounceUpForce;
    int bounceExtraEffectCooldown;


    public EntityProjectileBouncing(World worldIn) 
    {
        super(worldIn);

        this.setSize(0.5F, 0.5F);

        this.acceleratesVertically = true;
        this.isBouncing = false;
        this.bounceUpExtra = false;
        this.bounceUpForce = 0.0D;  
        this.bounceExtraEffectCooldown = 0;
    }

    public EntityProjectileBouncing(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity,
    boolean acceleratesVertically, boolean bounceUpExtra, double bounceUpForce)
    {
        super(worldIn, owner,
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity);

        this.setSize(0.5F, 0.5F);

        this.acceleratesVertically = acceleratesVertically;
        this.isBouncing = false;
        this.bounceUpExtra = bounceUpExtra;
        this.bounceUpForce = bounceUpForce;
        this.bounceExtraEffectCooldown = 0;
    }

    public EntityProjectileBouncing(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity,
    boolean projectileTransitionShould, int projectileTransitionLength,
    boolean acceleratesVertically, boolean bounceUpExtra, double bounceUpForce)
    {
        super(worldIn, owner,
        manualPosX, manualPosY, manualPosZ, 
        maxLifetime, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity);

        this.setSize(0.5F, 0.5F);

        this.projectileTransitionShould = projectileTransitionShould;
        this.projectileTransitionLength = projectileTransitionLength; 

        this.acceleratesVertically = acceleratesVertically;
        this.isBouncing = false;
        this.bounceUpExtra = bounceUpExtra;
        this.bounceUpForce = bounceUpForce;
        this.bounceExtraEffectCooldown = 0;
    }




    @Override
    public void onUpdate()
    {
        if(this.projectileTransitionShould && (this.projectileTransitionTimer < projectileTransitionLength))
        {
            ++this.projectileTransitionTimer;

            if((this.projectileTransitionTimer >= projectileTransitionLength))
            {
                this.setNoGravity(false);
                this.projectileTransitioned = true;
            }
        }


        super.onUpdate();


        if(this.bounceExtraEffectCooldown > 0)
        {
            --this.bounceExtraEffectCooldown;
        }
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
        if(this.acceleratesVertically) { motionY *= this.accelerationRate; }
        motionZ *= this.accelerationRate;


//Increase acceleration tracker
        this.accelerationCurrent *= this.accelerationRate;
    }




    @Override
//Block hit logic
    public void onHitBlock(RayTraceResult rayTraceResult, Block blockHit)
    {
//Can set whether projectile stops at blocks
        if(!world.isRemote && this.projectileStopsAtBlock)
        {
            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.setDead();
            return;
        }

//Set bouncing flag
        this.isBouncing = true;

//Reflect the projectile based on axis hit
        switch (rayTraceResult.sideHit.getAxis()) 
        {
            case X:
                this.motionX *= -1.0D;

                break;

            case Y:
                this.motionY *= -1.0D;

//If bouncing up
                if(this.motionY > 0.0D)
                {
//Slight safety offset
                    this.posY += 0.05D;

//Minimal bounce-up that can counteract gravity
                    if(this.bounceUpExtra && this.motionY < bounceUpForce)
                    {
                        this.motionY += bounceUpForce;
                    }
                }

                break;

            case Z:
                this.motionZ *= -1.0D;

                break;
        }

//Play indicative sound
        this.projectileBounceSound();
    }


    public void projectileBounceSound()
    {
        this.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0F, 1.0F);
    }




    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);

        compound.setBoolean("AcceleratesVertically", acceleratesVertically);
        compound.setBoolean("IsBouncing", isBouncing);
        compound.setBoolean("BounceUpExtra", bounceUpExtra);
        compound.setDouble("BounceUpForce", bounceUpForce);
        compound.setInteger("BounceExtraEffectCooldown", bounceExtraEffectCooldown);
    }


    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("AcceleratesVertically")) { acceleratesVertically = compound.getBoolean("AcceleratesVertically"); }
        if (compound.hasKey("IsBouncing")) { isBouncing = compound.getBoolean("IsBouncing"); }
        if (compound.hasKey("BounceUpExtra")) { bounceUpExtra = compound.getBoolean("BounceUpExtra"); }
        if (compound.hasKey("BounceUpForce")) { bounceUpForce = compound.getDouble("BounceUpForce"); }
        if (compound.hasKey("BounceExtraEffectCooldown")) { bounceExtraEffectCooldown = compound.getInteger("BounceExtraEffectCooldown"); }
    }

}
