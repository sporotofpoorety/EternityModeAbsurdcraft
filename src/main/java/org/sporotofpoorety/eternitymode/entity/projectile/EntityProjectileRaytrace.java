package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;




public class EntityProjectileRaytrace extends EntityWithOwner
{

    public int maxLifetime;
    public boolean firstBeenShot;

    public double speedX;
    public double speedY;
    public double speedZ;
    public double accelerationRate;
    public double accelerationCurrent;
    public double gravitySpeed;

    public double hitCheckSize;
    public boolean projectileStopsAtEntity;
    public boolean projectileStopsAtBlock;
    public float projectileHitDamage;

    int particleLifetime;
    int particleDensity;
    double particleVelocity;


//Niche
    boolean projectileTransitionShould = false;
    int projectileTransitionTimer = 0;
    int projectileTransitionLength = 20;
    boolean projectileTransitioned = false;

    int splitProjectileCount = 50; 
    double splitConeRadians = Math.PI;
    int splitAimMode = 0;
    float splitDamage = 1.0F; 
    double splitSpeed = 1.0D;
    double splitAcceleration = 1.01D;




    public EntityProjectileRaytrace(World worldIn)
    {
        super(worldIn);

        this.setNoGravity(true);

        this.maxLifetime = 200;
        this.firstBeenShot = false;

        this.speedX = 0.0D;
        this.speedY = 0.0D;
        this.speedZ = 0.0D;
        this.accelerationRate = 1.0D;
        this.accelerationCurrent = 1.0D;
        this.gravitySpeed = 0.08D;

        this.hitCheckSize = 0.3D;
        this.projectileStopsAtEntity = true;
        this.projectileStopsAtBlock = true;
        this.projectileHitDamage = 5.0F;

        this.particleLifetime = 10;
        this.particleDensity = 5;
        this.particleVelocity = 0.06D;
    }

    public EntityProjectileRaytrace(World worldIn, EntityLivingBase owner)
    {
        super(worldIn, owner);

        this.setNoGravity(true);

        this.owner = owner;
        if(this.owner != null) { this.setPosition(owner.posX, owner.posY + (double) owner.getEyeHeight() - 0.10000000149011612D, owner.posZ); }
        else { this.setPosition(0.0D, 0.0D, 0.0D); }

        this.maxLifetime = 200;
        this.firstBeenShot = false;

        this.speedX = 0.0D;
        this.speedY = 0.0D;
        this.speedZ = 0.0D;
        this.accelerationRate = 1.0D;
        this.accelerationCurrent = 1.0D;
        this.gravitySpeed = 0.08D;

        this.hitCheckSize = 0.3D;
        this.projectileStopsAtEntity = true;
        this.projectileStopsAtBlock = true;
        this.projectileHitDamage = 5.0F;

        this.particleLifetime = 10;
        this.particleDensity = 5;
        this.particleVelocity = 0.06D;
    }

    public EntityProjectileRaytrace(World worldIn, EntityLivingBase owner,
    double manualPosX, double manualPosY, double manualPosZ, 
    int maxLifetime, double speedX, double speedY, double speedZ,
    double accelerationRate, double gravitySpeed,
    double hitCheckSize, boolean projectileStopsAtEntity, boolean projectileStopsAtBlock, float projectileHitDamage,
    int particleLifetime, int particleDensity, double particleVelocity)
    {
        super(worldIn, owner);

        this.setNoGravity(true);

        if(this.owner != null) { this.setPosition(owner.posX, owner.posY + (double) owner.getEyeHeight() - 0.10000000149011612D, owner.posZ); }
        else { this.setPosition(manualPosX, manualPosY, manualPosZ); }

        this.maxLifetime = maxLifetime;
        this.firstBeenShot = false;

        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.accelerationRate = accelerationRate;
        this.accelerationCurrent = accelerationRate;
        this.gravitySpeed = gravitySpeed;

        this.hitCheckSize = hitCheckSize;
        this.projectileStopsAtEntity = projectileStopsAtEntity;
        this.projectileStopsAtBlock = projectileStopsAtBlock;
        this.projectileHitDamage = projectileHitDamage;

        this.particleLifetime = particleLifetime;
        this.particleDensity = particleDensity;
        this.particleVelocity = particleVelocity;
    }


    protected void entityInit()
    {

    }




    public void onUpdate()
    {
        super.onUpdate();


//Implemented lifetime
        if (!world.isRemote && this.ticksExisted > this.maxLifetime) 
        {
            this.onTrueSetDead();
            this.setDead();
        }




//Yes, this has to be set manually
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;




//Start and end vectors for this tick's movement
        Vec3d vecStart = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vecEnd = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

//Check for block collision
        RayTraceResult collisionResult = this.world.rayTraceBlocks(vecStart, vecEnd,
//stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock
        false, true, false);

//If this collides with blocks
        if (this.projectileStopsAtBlock
//and it actually did collide with a block
        && collisionResult != null)
        {
//Truncate vecEnd to not reach behind collided block
            vecEnd = new Vec3d(collisionResult.hitVec.x, collisionResult.hitVec.y, collisionResult.hitVec.z);
        }




//Closest hit entity null by default
        Entity closestHitEntity = null;

//Entities within BB + motion + 1 block of lenience
        List<Entity> entitiesNearby = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(this.hitCheckSize + 0.7D));

//Closest hit entity distance squared
        double closestHitDistanceSq = 0.0D;




//For each nearby entity
        for (Entity candidateEntity : entitiesNearby)
        {
//Check this projectile's hit entity conditions
            if(!this.projectileHitEntityConditions(candidateEntity))
            {
                continue;    
            }


//Get and grow entity bounding box (hitcheck)
                AxisAlignedBB entityBB = candidateEntity.getEntityBoundingBox().grow(this.hitCheckSize);
//Calc if entity BB is in projectile's current motion path
                RayTraceResult entityHitResult = entityBB.calculateIntercept(vecStart, vecEnd);


//If entity is in motion path
            if (entityHitResult != null)
            {
//Get distance sq
                double candidateHitDistanceSq = vecStart.squareDistanceTo(entityHitResult.hitVec);

//If this is the first near enough candidate
                if (closestHitDistanceSq == 0.0D 
//Or candidate closer than current closest candidate
                || candidateHitDistanceSq < closestHitDistanceSq)
                {
//This is now the closest hit entity
                    closestHitEntity = candidateEntity;
//And set closest candidate distance to this candidate's
                    closestHitDistanceSq = candidateHitDistanceSq;
                }
            }                  
        }




//If an entity got hit at all
        if (closestHitEntity != null)
        {
//Override block collision with entity collision
            collisionResult = new RayTraceResult(closestHitEntity);
        }


//If anything got hit at all
        if (collisionResult != null)
        {
//Goes through portals
            if (collisionResult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(collisionResult.getBlockPos()).getBlock() == Blocks.PORTAL)
            {
                this.setPortal(collisionResult.getBlockPos());
            }
//Otherwise performs collision
            else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, collisionResult))
            {
                this.onHit(collisionResult);
            }
        }




//Have to specify motion myself yes
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;




//Get horizontal speed
        float horizontalSpeed = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);


//Get horizontal rotation
        this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * 360D / (2 * Math.PI));
//Set vertical rotation
        this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) horizontalSpeed) * 360D / (2 * Math.PI));


//If previous vertical rotation above positive bound
        while (this.rotationPitch - this.prevRotationPitch < -180.0F)
        {
//Adjust it negatively
            this.prevRotationPitch -= 360.0F;
        }

//If previous vertical rotation below negative bound
        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
//Adjust it positively
            this.prevRotationPitch += 360.0F;
        }

//If previous horizontal rotation above positive bound
        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
//Adjust it negatively
            this.prevRotationYaw -= 360.0F;
        }

//If previous horizontal rotation below negative bound
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
//Adjust it positively
            this.prevRotationYaw += 360.0F;
        }


//Rotation smoothing
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;




//Gravity
        if (!this.hasNoGravity())
        {
            this.motionY -= this.gravitySpeed;
        }




//Setting position is the last thing
        this.setPosition(this.posX, this.posY, this.posZ);
    }




    public void speedApply()
    {

    }


    public void accelerationApply()
    {

    }


    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float)(MathHelper.atan2(y, f) * (180D / Math.PI));
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }




    public boolean projectileHitEntityConditions(Entity entityToCheck)
    {
//If entity can't be collided
        if (!entityToCheck.canBeCollidedWith()
//Or is not EntityLivingBase
        || !(entityToCheck instanceof EntityLivingBase)
//Or is owner of projectile
        || entityToCheck == this.owner
//Or is on projectile's team
        || entityToCheck.isOnSameTeam(this)
//Or is on (non-null) owner's team
        || (this.owner != null && (entityToCheck.isOnSameTeam(this.owner))))
        {
//Then it does not meet conditions
            return false;
        }

//Else it does
        return true;
    }


//Already preemptively null-checked to enable
//projectile-specific logic to safely run before this setDead()
    public void onHitEntity(RayTraceResult rayTraceResult, Entity entityHit)
    {
//If owner is not null
        if (this.owner != null
//And projectile has actual damage
        && this.projectileHitDamage > 0.1F)
        {
//Attack entity
            entityHit.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), this.projectileHitDamage);
        }

//Can set whether projectile stops at entities
        if(!world.isRemote && this.projectileStopsAtEntity)
        {
            this.onTrueSetDead();
            this.setDead();
            return;
        }
    }


//Block hit logic
    public void onHitBlock(RayTraceResult rayTraceResult, Block blockHit)
    {
//Can set whether projectile stops at blocks
        if(!world.isRemote && this.projectileStopsAtBlock)
        {
            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.onTrueSetDead();
            this.setDead();
            return;
        }
    }


//When projectile collides
    public void onHit(RayTraceResult rayTraceResult)
    {
//If raytrace hit an entity
        if (rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY) 
        {
//Ensure an entity was actually hit
            if(rayTraceResult.entityHit != null)
            {
//Perform on hitting entity logic
                onHitEntity(rayTraceResult, rayTraceResult.entityHit);
            }
        } 
//If raytrace hit a block
        else if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) 
        {
//Get block pos
            BlockPos posHit = rayTraceResult.getBlockPos();
//Get block hit
            Block blockHit = this.world.getBlockState(posHit).getBlock();

//Perform on hitting block logic
            onHitBlock(rayTraceResult, blockHit);        
        }
    }


    public void onTrueSetDead()
    {

    }




    @Nullable
    public EntityLivingBase getProjectileOwnerTarget() 
    {
        if (owner instanceof EntityLiving) 
        {
            return ((EntityLiving) owner).getAttackTarget();
        }

        return null;
    }




    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("MaxLifetime", this.maxLifetime);
        compound.setBoolean("FirstBeenShot", this.firstBeenShot);

        compound.setDouble("SpeedX", this.speedX);
        compound.setDouble("SpeedY", this.speedY);
        compound.setDouble("SpeedZ", this.speedZ);
        compound.setDouble("AccelerationRate", this.accelerationRate);
        compound.setDouble("AccelerationCurrent", this.accelerationCurrent);
        compound.setDouble("GravitySpeed", this.gravitySpeed);

        compound.setDouble("HitCheckSize", this.hitCheckSize);
        compound.setBoolean("ProjectileStopsAtEntity", this.projectileStopsAtEntity);
        compound.setBoolean("ProjectileStopsAtBlock", this.projectileStopsAtBlock);
        compound.setFloat("ProjectileHitDamage", this.projectileHitDamage);

        compound.setInteger("ParticleLifetime", this.particleLifetime);
        compound.setInteger("ParticleDensity", this.particleDensity);
        compound.setDouble("ParticleVelocity", this.particleVelocity);


//Niche
        compound.setBoolean("ProjectileTransitionShould", this.projectileTransitionShould);
        compound.setInteger("ProjectileTransitionTimer", this.projectileTransitionTimer);
        compound.setInteger("ProjectileTransitionLength", this.projectileTransitionLength);
        compound.setBoolean("ProjectileTransitioned", this.projectileTransitioned);

        compound.setInteger("SplitProjectileCount", this.splitProjectileCount);
        compound.setDouble("SplitConeRadians", this.splitConeRadians);
        compound.setInteger("SplitAimMode", this.splitAimMode);
        compound.setFloat("SplitDamage", this.splitDamage);
        compound.setDouble("SplitSpeed", this.splitSpeed);
        compound.setDouble("SplitAcceleration", this.splitAcceleration);
    }


    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("MaxLifetime")) { this.maxLifetime = compound.getInteger("MaxLifetime"); }
        if (compound.hasKey("FirstBeenShot")) { this.firstBeenShot = compound.getBoolean("FirstBeenShot"); }

        if (compound.hasKey("SpeedX")) { this.speedX = compound.getDouble("SpeedX"); }
        if (compound.hasKey("SpeedY")) { this.speedY = compound.getDouble("SpeedY"); }
        if (compound.hasKey("SpeedZ")) { this.speedZ = compound.getDouble("SpeedZ"); }
        if (compound.hasKey("AccelerationRate")) { this.accelerationRate = compound.getDouble("AccelerationRate"); }
        if (compound.hasKey("AccelerationCurrent")) { this.accelerationCurrent = compound.getDouble("AccelerationCurrent"); }
        if (compound.hasKey("GravitySpeed")) { this.gravitySpeed = compound.getDouble("GravitySpeed"); }

        if (compound.hasKey("HitCheckSize")) { this.hitCheckSize = compound.getDouble("HitCheckSize"); }
        if (compound.hasKey("ProjectileStopsAtEntity")) { this.projectileStopsAtEntity = compound.getBoolean("ProjectileStopsAtEntity"); }
        if (compound.hasKey("ProjectileStopsAtBlock")) { this.projectileStopsAtBlock = compound.getBoolean("ProjectileStopsAtBlock"); }
        if (compound.hasKey("ProjectileHitDamage")) { this.projectileHitDamage = compound.getFloat("ProjectileHitDamage"); }

        if (compound.hasKey("ParticleLifetime")) { this.particleLifetime = compound.getInteger("ParticleLifetime"); }
        if (compound.hasKey("ParticleDensity")) { this.particleDensity = compound.getInteger("ParticleDensity"); }
        if (compound.hasKey("ParticleVelocity")) { this.particleVelocity = compound.getDouble("ParticleVelocity"); }


//Niche
        if (compound.hasKey("ProjectileTransitionShould")) { this.projectileTransitionShould = compound.getBoolean("ProjectileTransitionShould"); }
        if (compound.hasKey("ProjectileTransitionTimer")) { this.projectileTransitionTimer = compound.getInteger("ProjectileTransitionTimer"); }
        if (compound.hasKey("ProjectileTransitionLength")) { this.projectileTransitionLength = compound.getInteger("ProjectileTransitionLength"); }
        if (compound.hasKey("ProjectileTransitioned")) { this.projectileTransitioned = compound.getBoolean("ProjectileTransitioned"); }

        if (compound.hasKey("SplitProjectileCount")) { this.splitProjectileCount = compound.getInteger("SplitProjectileCount"); }
        if (compound.hasKey("SplitConeRadians")) { this.splitConeRadians = compound.getDouble("SplitConeRadians"); } 
        if (compound.hasKey("SplitAimMode")) { this.splitAimMode = compound.getInteger("SplitAimMode"); }
        if (compound.hasKey("SplitDamage")) { this.splitDamage = compound.getFloat("SplitDamage"); }
        if (compound.hasKey("SplitSpeed")) { this.splitSpeed = compound.getDouble("SplitSpeed"); } 
        if (compound.hasKey("SplitAcceleration")) { this.splitAcceleration = compound.getDouble("SplitAcceleration"); } 
    }

}

