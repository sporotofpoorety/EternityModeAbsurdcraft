package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
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

public class EntityProjectileBase extends Entity implements IProjectile {

    public EntityLivingBase owner;
    protected NBTTagCompound ownerNbt;
    public int maxLifetime = 200;



    public EntityProjectileBase(World worldIn)
    {
        super(worldIn);
    }

    public EntityProjectileBase(World worldIn, EntityLivingBase owner)
    {
        super(worldIn);
        this.owner = owner;
        this.setPosition(owner.posX - (double)(owner.width + 1.0F) * 0.5D, owner.posY + (double)owner.getEyeHeight() - 0.10000000149011612D, owner.posZ + (double)(owner.width + 1.0F) * 0.5D);
    }


    protected void entityInit()
    {

    }




    public void onUpdate()
    {
        super.onUpdate();

        if (!world.isRemote && this.ticksExisted > this.maxLifetime) {
            this.setDead();
        }

        if (this.ownerNbt != null)
        {
            this.restoreOwnerFromSave();
        }

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;


        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
        vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (raytraceresult != null)
        {
            vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }

        Entity entity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
        double d0 = 0.0D;


        for (Entity entity1 : list)
        {
            if (!entity1.canBeCollidedWith()) { continue; }
            if (entity1 == owner) { continue; }


            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
            RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

            if (raytraceresult1 != null)
            {
                double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                if (d1 < d0 || d0 == 0.0D)
                {
                    entity = entity1;
                    d0 = d1;
                }
            }                  
        }


        if (entity != null)
        {
            raytraceresult = new RayTraceResult(entity);
        }

        if (raytraceresult != null)
        {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL)
            {
                this.setPortal(raytraceresult.getBlockPos());
            }
            else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
            {
                this.onHit(raytraceresult);
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;


        float gravitySpeed = this.getGravityVelocity();


        if (!this.hasNoGravity())
        {
            this.motionY -= (double) gravitySpeed;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
    }

    protected float getGravityVelocity()
    {
        return 0.03F;
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




    @Nullable
    private Entity getHitEntity(Vec3d vecStart, Vec3d vecEnd)
    {
        Entity entity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
        double d0 = 0.0D;

        for (Entity entity1 : list)
        {

            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vecStart, vecEnd);

            if (raytraceresult != null)
            {
                double d1 = vecStart.squareDistanceTo(raytraceresult.hitVec);

                if (d1 < d0 || d0 == 0.0D)
                {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }
        return entity;
    }


    public void onHit(RayTraceResult rayTraceResult)
    {

    }




    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float horizontalSpeed = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, horizontalSpeed) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }




    private void restoreOwnerFromSave()
    {
        if (this.ownerNbt != null && this.ownerNbt.hasUniqueId("OwnerUUID"))
        {
            UUID uuid = this.ownerNbt.getUniqueId("OwnerUUID");

            for (EntityLivingBase livingBase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(15.0D)))
            {
                if (livingBase.getUniqueID().equals(uuid))
                {
                    this.owner = livingBase;
                    break;
                }
            }
        }

        this.ownerNbt = null;
    }


    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("Owner", 10))
        {
            this.ownerNbt = compound.getCompoundTag("Owner");
        }
    }


    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        if (this.owner != null)
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            UUID uuid = this.owner.getUniqueID();
            nbttagcompound.setUniqueId("OwnerUUID", uuid);
            compound.setTag("Owner", nbttagcompound);
        }
    }
}

