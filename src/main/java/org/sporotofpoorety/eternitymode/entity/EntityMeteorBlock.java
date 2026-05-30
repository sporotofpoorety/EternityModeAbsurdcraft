package org.sporotofpoorety.eternitymode.entity;


import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import electroblob.wizardry.registry.WizardryBlocks;

import org.sporotofpoorety.eternitymode.client.ExplosiveHandler;
import org.sporotofpoorety.eternitymode.entity.EntityThrownBlock;
import org.sporotofpoorety.eternitymode.util.ExplosionUtil;
import org.sporotofpoorety.eternitymode.util.ProjectileUtil;




public class EntityMeteorBlock extends EntityThrownBlock
{

    float explosionPower;
    boolean explosionFire;
    boolean explosionDestruction;

    int splitProjectileCount; 
    double splitConeRadians; 
    int splitAimMode;
    float splitDamage; 
    double splitSpeed; 
    double splitAcceleration;

    boolean shouldSplit; 
    boolean splitExplodes;
    float splitExplosionPower;
    boolean splitFire; 
    boolean splitDestruction;


    public EntityMeteorBlock(World worldIn)
    {
        super(worldIn);
    }

    public EntityMeteorBlock(World worldIn, double x, double y, double z, 
    EntityLivingBase owner, boolean dealsDamage, float thrownBlockDamage,
    int lifetimeMax, double speedX, double speedY, double speedZ, 
    double accelerationVal, double gravitySpeed,
    float explosionPower, boolean explosionFire, boolean explosionDestruction,
    int splitProjectileCount, double splitConeRadians, int splitAimMode,
    float splitDamage, double splitSpeed, double splitAcceleration,
    boolean shouldSplit, boolean splitExplodes, float splitExplosionPower, boolean splitFire, boolean splitDestruction)
    {
        super(worldIn, x, y, z, owner, WizardryBlocks.meteor.getDefaultState(), true, false, dealsDamage, thrownBlockDamage);
        this.setSize(7.84F, 7.84F);
        this.hasManualOrigin = true;
        this.dontBreakInitialPos = true;

        this.lifetimeMax = lifetimeMax;

        this.setMovement(speedX, speedY, speedZ, 
            gravitySpeed, true, accelerationVal);

        this.explosionPower = explosionPower; 
        this.explosionFire = explosionFire; 
        this.explosionDestruction = explosionDestruction;


        this.splitProjectileCount = splitProjectileCount; 
        this.splitConeRadians = splitConeRadians; 
        this.splitAimMode = splitAimMode;
        this.splitDamage = splitDamage; 
        this.splitSpeed = splitSpeed; 
        this.splitAcceleration = splitAcceleration;

        this.shouldSplit = shouldSplit; 
        this.splitExplodes = splitExplodes;
        this.splitExplosionPower = splitExplosionPower;
        this.splitFire = splitFire; 
        this.splitDestruction = splitDestruction;
    }


    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        compound.setFloat("ExplosionPower", this.explosionPower);
        compound.setBoolean("ExplosionFire", this.explosionFire);
        compound.setBoolean("ExplosionDestruction", this.explosionDestruction);


        compound.setInteger("SplitProjectileCount", this.splitProjectileCount);
        compound.setDouble("SplitConeRadians", this.splitConeRadians);
        compound.setInteger("SplitAimMode", this.splitAimMode);
        compound.setFloat("SplitDamage", this.splitDamage);
        compound.setDouble("SplitSpeed", this.splitSpeed);
        compound.setDouble("SplitAcceleration", this.splitAcceleration);

        compound.setBoolean("ShouldSplit", this.shouldSplit);
        compound.setBoolean("SplitExplodes", this.splitExplodes);
        compound.setFloat("SplitExplosionPower", this.splitExplosionPower);
        compound.setBoolean("SplitFire", this.splitFire);
        compound.setBoolean("SplitDestruction", this.splitDestruction);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("ExplosionPower")) { this.explosionPower = compound.getFloat("ExplosionPower"); }
        if (compound.hasKey("ExplosionFire")) { this.explosionFire = compound.getBoolean("ExplosionFire"); }
        if (compound.hasKey("ExplosionDestruction")) { this.explosionDestruction = compound.getBoolean("ExplosionDestruction"); }


        if (compound.hasKey("SplitProjectileCount")) { this.splitProjectileCount = compound.getInteger("SplitProjectileCount"); }
        if (compound.hasKey("SplitConeRadians")) { this.splitConeRadians = compound.getDouble("SplitConeRadians"); } 
        if (compound.hasKey("SplitAimMode")) { this.splitAimMode = compound.getInteger("SplitAimMode"); }
        if (compound.hasKey("SplitDamage")) { this.splitDamage = compound.getFloat("SplitDamage"); }
        if (compound.hasKey("SplitSpeed")) { this.splitSpeed = compound.getDouble("SplitSpeed"); } 
        if (compound.hasKey("SplitAcceleration")) { this.splitAcceleration = compound.getDouble("SplitAcceleration"); } 

        if (compound.hasKey("ShouldSplit")) { this.shouldSplit = compound.getBoolean("ShouldSplit"); }
        if (compound.hasKey("SplitExplodes")) { this.splitExplodes = compound.getBoolean("SplitExplodes"); }
        if (compound.hasKey("SplitExplosionPower")) { this.splitExplosionPower = compound.getFloat("SplitExplosionPower"); }
        if (compound.hasKey("SplitFire")) { this.splitFire = compound.getBoolean("SplitFire"); }
        if (compound.hasKey("SplitDestruction")) { this.splitDestruction = compound.getBoolean("SplitDestruction"); }
    }




    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.meteorLogic();
        if(!this.shouldSplit) { this.motionY = -0.1D; }

        super.onUpdate();
    }


    public void meteorLogic()
    {
        this.setFire(20);

//      if(this.collided) { this.meteorExplode(); this.setDead(); return; }
        if(!this.world.isRemote && this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(1.66D, 1.66D, 1.66D))) { this.meteorExplode(); this.setDead(); return; }

        if(this.world.isRemote && this.ticksExisted % 2 == 0) 
        { 
            double atX = this.posX + (this.motionX * -0.2D);
            double atY = this.posY + (this.height / 2.0D) + (this.motionY * -0.2D);
            double atZ = this.posZ + (this.motionZ * -0.2D);
            ExplosiveHandler.spawnParticles(this.world, atX, atY, atZ, 1.6F, false, false); 
        }
    }


    public void meteorExplode()
    {
        ExplosionUtil.performOptimizedExplosion(this.world, this.owner, this.posX, this.posY + (this.height / 2.0D), this.posZ,
            this.explosionPower, true, this.thrownBlockDamage, true, 3.0D, this.explosionDestruction, 9999999.0F, this.explosionFire, 
            true, 1, true);


        if(this.shouldSplit) { this.randomKaboom(); }
    }


    public void randomKaboom()
    {
        if(this.owner == null || !(this.owner instanceof EntityLiving)) 
        {
//          ProjectileUtil.shootAimedFireballSpreadCoord(this.posX, this.posY, this.posZ, 
            ProjectileUtil.shootAimedFireballSpreadCoord(this.prevPosX, this.prevPosY, this.prevPosZ, 
            null, this, null,
            this.splitProjectileCount, this.splitConeRadians, this.splitAimMode,
            this.splitDamage, this.splitSpeed, this.splitAcceleration,
            this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);  
        }
        else 
        {
//          ProjectileUtil.shootAimedFireballSpreadCoord(this.posX, this.posY + 0.5D, this.posZ, 
            ProjectileUtil.shootAimedFireballSpreadCoord(this.prevPosX, this.prevPosY, this.prevPosZ, 
            this.owner, this, ((EntityLiving) this.owner).getAttackTarget(),
            this.splitProjectileCount, this.splitConeRadians, this.splitAimMode,
            this.splitDamage, this.splitSpeed, this.splitAcceleration,
            this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);      
        }
    }


    @Override
    public void dealDamage()
    {
//Get entities within AABB
        List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));

        for(Entity nearbyEntity : list)
        {
            if(nearbyEntity instanceof EntityPlayer)
            {
                this.meteorExplode(); 
                this.setDead();
                break;
            }
        }
    }


//On lifetime expire
    @Override
    public void onLifetimeExpire()
    {
        this.meteorExplode();
        this.setDead();
    }


    @Override
    public void onHitGround()
    {
       
    }


    @Override
    @Nullable
    public IBlockState getBasisState()
    {
//      return (IBlockState)((Optional)this.dataManager.get(BASIS_STATE)).orNull();
        return WizardryBlocks.meteor.getDefaultState();
    }


/*
    @Override
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
    }
*/


    /**
     * Return whether this entity should be rendered as on fire.
     */
    public boolean canRenderOnFire()
    {
        return true;
    }

}
