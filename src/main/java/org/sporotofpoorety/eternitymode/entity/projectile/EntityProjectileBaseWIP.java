/*
package org.sporotofpoorety.eternitymode.entity.projectile;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;



public class EntityProjectileBase extends Entity {

//Parameters
    public EntityLivingBase owner;
    protected NBTTagCompound ownerNbt;
    public int maxLifetime = 200;


    public EntityProjectileBase(World worldIn) 
    {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
    }

    
    public void initRotationFromMotion()
    {
        float horizontalSpeed = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) horizontalSpeed) * (180D * Math.PI));

        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }



    @Override
    protected void entityInit() 
    {

    }




    @Override
    public void onUpdate()
    {
        super.onUpdate();

//Implemented lifetime
        if (!world.isRemote && this.ticksExisted > this.maxLifetime) 
        {
            this.setDead();
        }

//Yes, this has to be set manually
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        shotMotion();   

        shotRaytraceAndImpact();

        shotSyncMovement();

        shotPostUpdate();
    }




//Normally would need code duplication
//in subclasses due to awkward ordering of
//Entity.onUpdate(), the individual motion and the base logic
    public void shotMotion()
    {

    }




//Collision detection
//set right after the motion
    public void shotRaytraceAndImpact()
    {
//Start and end vectors for this tick's movement
        Vec3d vecStart = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vecEnd = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

//Check for block collision
        RayTraceResult collisionResult = this.world.rayTraceBlocks(vecStart, vecEnd,
//stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock
        false, true, false);

//
//Kept just in case something breaks for some reason
//      vecStart = new Vec3d(this.posX, this.posY, this.posZ);
//      vecEnd = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
//

//If block collision did happen
        if (collisionResult != null)
        {
//Truncate vecEnd
//to not reach behind collided block
            vecEnd = new Vec3d(collisionResult.hitVec.x, collisionResult.hitVec.y, collisionResult.hitVec.z);
        }

//Get hit entity (if any)
        Entity hitEntity = this.getHitEntity(vecStart, vecEnd);

//If entity not null (was hit)
        if (hitEntity != null)
        {
//Override block collision with entity collision
            collisionResult = new RayTraceResult(hitEntity);
        }

//Finally if
//anything got hit at all
        if (collisionResult != null)
        {
//Then performs on hit logic
            this.onImpact(collisionResult);
        }
    }




    public void shotSyncMovement()
    {
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
    }




//After logic
    public void shotPostUpdate()
    {
        if (!this.hasNoGravity())
        {
            this.motionY -= (double) this.getGravityVelocity();
        }

//Position should only be adjusted after logic
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    protected float getGravityVelocity()
    {
        return 0.03F;
    }




    @Nullable
    private Entity getHitEntity(Vec3d vecStart, Vec3d vecEnd)
    {
//Closest hit entity null by default
        Entity closestHitEntity = null;

//Entities within BB, motion and 1 block of lenience
        List<Entity> candidateEntities
            = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));

//Closest hit entity distance squared
        double closestHitDistanceSq = 0.0D;


//For each candidate entity
        for (Entity candidateEntity : candidateEntities)
        {

//If entity is not shooter
            if (candidateEntity != this.owner)
            {

//Get and grow 
//entity bounding box (hitcheck)
                AxisAlignedBB entityBB = candidateEntity.getEntityBoundingBox().grow(0.30000001192092896D);
//Calculate if entity bounding box 
//is in projectile's current motion path
                RayTraceResult entityHitResult = entityBB.calculateIntercept(vecStart, vecEnd);



                if (entityHitResult != null)
                {
//Get distance sq
                    double candidateHitDistanceSq = vecStart.squareDistanceTo(entityHitResult.hitVec);

//If first entity in hitting range
                    if (closestHitDistanceSq == 0.0D 
//Or candidate distance below current closest distance hit
                    || candidateHitDistanceSq < closestHitDistanceSq)
                    {
//This is now the closest hit entity
                        closestHitEntity = candidateEntity;
//And set closest hit distance to candidate hit distance
                        closestHitDistanceSq = candidateHitDistanceSq;
                    }
                }
            }
        }

//Return closest hit entity
        return closestHitEntity;
    }



//Set if can collide
    @Override
    public boolean canBeCollidedWith() 
    {
        return true;
    }
//Have to implement impact myself
    protected void onImpact(RayTraceResult result) 
    {
        if (!this.world.isRemote) 
        {
            if (result.entityHit != null)
            {

            }

            this.setDead();
        }
    }



    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) 
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) 
    {

    }


}
*/
