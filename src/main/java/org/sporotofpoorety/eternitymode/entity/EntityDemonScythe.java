package org.sporotofpoorety.eternitymode.entity;


import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;


import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;
import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityMob;
import org.sporotofpoorety.eternitymode.util.ExplosionUtil;




public class EntityDemonScythe extends EntityWithOwner 
{

    public EntityDemonScythe(World world) 
    {
        super(world);
        setSize(0.5F, 0.5F);
//Should noclip by default ig
        this.noClip = true;
        this.setNoGravity(true);
    }

    public EntityDemonScythe(World worldIn, double x, double y, double z, 
    EntityLivingBase owner,
    int lifetimeMax) 
    {
        super(worldIn, x, y, z, owner);
        setSize(0.5F, 0.5F);
//Should noclip by default ig
        this.noClip = true;
        this.setNoGravity(true);

        this.lifetimeMax = lifetimeMax;
	}


    @Override
    protected void entityInit() {}


//Particles on expire
    public void onLifetimeExpire()
    {
        for (int particleAt = 0; particleAt < 1000; ++particleAt)
        {
            this.world.spawnParticle(EnumParticleTypes.PORTAL, 
            this.posX + (this.rand.nextDouble() - 0.5D) * 6.0D, 
            this.posY + (this.rand.nextDouble() - 0.5D) * 6.0D, 
            this.posZ + (this.rand.nextDouble() - 0.5D) * 6.0D, 
            (this.rand.nextDouble() - 0.5D) * 2.0D, 
            -this.rand.nextDouble(), 
            (this.rand.nextDouble() - 0.5D) * 2.0D);
        }   

        this.setDead();
    }


    @Override
    public void onUpdate() 
    {
//Die and particles on collide
        if(!this.world.isRemote && this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(1.5D, 0.1D, 1.5D))) 
        {
            for (int particleAt = 0; particleAt < 1000; ++particleAt)
            {
                this.world.spawnParticle(EnumParticleTypes.PORTAL, 
                this.posX + (this.rand.nextDouble() - 0.5D) * 6.0D, 
                this.posY + (this.rand.nextDouble() - 0.5D) * 6.0D, 
                this.posZ + (this.rand.nextDouble() - 0.5D) * 6.0D, 
                (this.rand.nextDouble() - 0.5D) * 2.0D, 
                -this.rand.nextDouble(), 
                (this.rand.nextDouble() - 0.5D) * 2.0D);
            }   
         
            this.setDead(); 
            return; 
        }


        super.onUpdate();


        if(!this.world.isRemote)
        {
            for (int particleAt = 0; particleAt < 20; ++particleAt)
            {
                this.world.spawnParticle(EnumParticleTypes.PORTAL, 
                this.posX + (this.rand.nextDouble() - 0.5D) * 3.5D, 
                this.posY + this.rand.nextDouble() * 0.5D, 
                this.posZ + (this.rand.nextDouble() - 0.5D) * 3.5D, 
                (this.rand.nextDouble() - 0.5D) * 2.0D, 
                -this.rand.nextDouble(), 
                (this.rand.nextDouble() - 0.5D) * 2.0D);
            }
        }


        if(this.owner != null)
        {
            List<Entity> nearEntities = this.world.getEntitiesWithinAABBExcludingEntity(this.owner, this.getEntityBoundingBox().grow(2.75D, 2.75D, 2.75D));

            if(this.owner instanceof EntityPlayer)
            {
                for(Entity nearEntity : nearEntities)
                {
                    if((nearEntity instanceof EntityMob && !((IMixinEntityMob) nearEntity).isTamed()) 
                    || (nearEntity instanceof IMob && !(nearEntity instanceof EntityMob)))
                    {
			            nearEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) this.owner), this.damageVal);

//Fling away
                        double entityDist = Math.sqrt
                        (Math.pow(nearEntity.posX - this.posX, 2) + Math.pow(nearEntity.posY - this.posY, 2) + Math.pow(nearEntity.posZ - this.posZ, 2));

                        nearEntity.motionX += (nearEntity.posX - this.posX) / entityDist;
                        nearEntity.motionY += (nearEntity.posY - this.posY) / entityDist;
                        nearEntity.motionZ += (nearEntity.posZ - this.posZ) / entityDist;
                    }
                }
            }
        }

        this.performBasicMovement();
    }




    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

    }


    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
    }




    @Override
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
    }

    @Override
    public boolean canRenderOnFire() 
    {
        return false;
    }

}
