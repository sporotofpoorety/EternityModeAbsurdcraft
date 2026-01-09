package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityProjectileBase;



public class EntityFlameShot extends EntityProjectileBase {


    public EntityFlameShot(World worldIn)
    {
        super(worldIn);

        this.setSize(1.0F, 1.0F);
        this.setNoGravity(true);
    }

    public EntityFlameShot(World worldIn, EntityLivingBase owner)
    {
        super(worldIn);
        this.owner = owner;
        this.setPosition(owner.posX - (double)(owner.width + 1.0F) * 0.5D, owner.posY + (double)owner.getEyeHeight() - 0.10000000149011612D, owner.posZ + (double)(owner.width + 1.0F) * 0.5D);

        this.setSize(1.0F, 1.0F);
        this.setNoGravity(true);
    }


    public void onUpdate()
    {
        super.onUpdate();
    }


    public void onHit(RayTraceResult rayTraceResult)
    {
        if (rayTraceResult.entityHit != null && !(rayTraceResult.entityHit == this.owner) && rayTraceResult.entityHit instanceof EntityLivingBase)
        {
//          rayTraceResult.entityHit.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), 6.0F);
        }

        if (!this.world.isRemote)
        {
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
