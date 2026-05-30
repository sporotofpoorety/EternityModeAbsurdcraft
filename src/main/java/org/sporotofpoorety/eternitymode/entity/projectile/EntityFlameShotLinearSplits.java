package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShotLinear;

import org.sporotofpoorety.eternitymode.util.ProjectileUtil;




public class EntityFlameShotLinearSplits extends EntityFlameShotLinear {


    boolean splitExplodes; 
    float splitExplosionPower;
    boolean splitFire;
    boolean splitDestruction;


    public EntityFlameShotLinearSplits(World worldIn) 
    {
        super(worldIn);

        this.setSize(2.0F, 2.0F);

        this.splitExplodes = true;
        this.splitExplosionPower = 0.5F;
        this.splitFire = false;
        this.splitDestruction = false;
    }

    public EntityFlameShotLinearSplits(World worldIn, double x, double y, double z, 
    EntityLivingBase owner,
    int lifetimeMax, double speedX, double speedY, double speedZ, 
    double accelerationRate, double gravitySpeed, 
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage, 
    int particleLifetime, int particleDensity, double particleVelocity,
    int splitProjectileCount, double splitConeRadians, int splitAimMode,
    float splitDamage, double splitSpeed, double splitAcceleration,
    int fireDuration, boolean shouldExplode, float explosionPower, boolean explosionFire, boolean explosionDestruction,
    boolean splitExplodes, float splitExplosionPower, boolean splitFire, boolean splitDestruction)
    {
        super(worldIn, x, y, z,
        owner,
        lifetimeMax, speedX, speedY, speedZ,
        accelerationRate, gravitySpeed, 
        hitCheckSize, projectileStopsAtEntity, projectileStopsAtBlock, projectileHitDamage,  
        particleLifetime, particleDensity, particleVelocity,
        fireDuration, shouldExplode, explosionPower, explosionFire, explosionDestruction);

        this.setSize(2.0F, 2.0F);

        this.splitProjectileCount = splitProjectileCount; 
        this.splitConeRadians = splitConeRadians; 
        this.splitAimMode = splitAimMode;
        this.splitDamage = splitDamage; 
        this.splitSpeed = splitSpeed; 
        this.splitAcceleration = splitAcceleration;

        this.splitExplodes = splitExplodes;
        this.splitExplosionPower = splitExplosionPower;
        this.splitFire = splitFire;
        this.splitDestruction = splitDestruction;
    }




    public void onTrueSetDead()
    {
        if(!this.world.isRemote)
        {
            if(this.owner == null || !(this.owner instanceof EntityLiving)) 
            {
                ProjectileUtil.shootAimedFireballSpreadCoord(this.posX, this.posY, this.posZ, 
                null, this, null,
                this.splitProjectileCount, this.splitConeRadians, this.splitAimMode,
                this.splitDamage, this.splitSpeed, this.splitAcceleration,
                this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);  
            }
            else 
            {
                EntityLivingBase attackTarget = ((EntityLiving)this.owner).getAttackTarget();
                if(attackTarget != null) 
                { 
                    Vec3d targetVec = new Vec3d(attackTarget.posX - this.posX, attackTarget.posY - this.posY, attackTarget.posZ - this.posZ).normalize();  

                    EntityFlameShotLinear directShot = new EntityFlameShotLinear(this.world, this.posX, this.posY, this.posZ, 
                        this.owner,
                        100, targetVec.x * this.splitSpeed, targetVec.y * this.splitSpeed, targetVec.z * this.splitSpeed, 
                        this.splitAcceleration, 0.0D, 
                        0.3D, true, true, this.splitDamage, 
                        2, 2, 0.06D,
                        20, this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);
                    this.world.spawnEntity(directShot);
                }

                ProjectileUtil.shootAimedFireballSpreadEntity(this.owner, this, ((EntityLiving) this.owner).getAttackTarget(), 
                this.splitProjectileCount, this.splitConeRadians, this.splitAimMode,
                this.splitDamage, this.splitSpeed, this.splitAcceleration,
                this.splitExplodes, this.splitExplosionPower, this.splitFire, this.splitDestruction);    
            }
        }
    }




    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);

        compound.setBoolean("SplitExplodes", this.splitExplodes);
        compound.setFloat("SplitExplosionPower", this.splitExplosionPower);
        compound.setBoolean("SplitFire", this.splitFire);
        compound.setBoolean("SplitDestruction", this.splitDestruction);
    }


    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("SplitExplodes")) { this.splitExplodes = compound.getBoolean("SplitExplodes"); }
        if (compound.hasKey("SplitExplosionPower")) { this.splitExplosionPower = compound.getFloat("SplitExplosionPower"); }
        if (compound.hasKey("SplitFire")) { this.splitFire = compound.getBoolean("SplitFire"); }
        if (compound.hasKey("SplitDestruction")) { this.splitDestruction = compound.getBoolean("SplitDestruction"); }
    }

}
