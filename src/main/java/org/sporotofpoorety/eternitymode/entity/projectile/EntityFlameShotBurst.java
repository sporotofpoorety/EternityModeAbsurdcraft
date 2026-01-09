package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShot;
import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShotLinear;



public class EntityFlameShotBurst extends EntityFlameShot {


    public EntityFlameShotBurst(World worldIn) 
    {
        super(worldIn);
        this.setSize(2.0F, 2.0F);
    }

    public EntityFlameShotBurst(World worldIn, EntityLivingBase owner, double speedX, double speedY, double speedZ) 
    {
        super(worldIn, owner);
    }


    @Override
    public void onUpdate()
    {
        motionY = 0.1D;
        super.onUpdate();
    }



    public void setDead()
    {
        this.randomKaboom();

        super.setDead();
    }


    public void randomKaboom()
    {
/*
        if(this.theShooter != null)
        {
*/
            if(this.owner == null)
            {
                for(int times = 0; times < 200; times++)
                {
                    EntityFlameShotLinear entitySplit = new EntityFlameShotLinear(this.world);

                    entitySplit.posX = this.posX;
                    entitySplit.posY = this.posY;
                    entitySplit.posZ = this.posZ;
                    entitySplit.speedX = (rand.nextFloat() * 0.25) - 0.125;
                    entitySplit.speedY = (rand.nextFloat() * 0.25) - 0.125;
                    entitySplit.speedZ = (rand.nextFloat() * 0.25) - 0.125;
                    this.world.spawnEntity(entitySplit);
                }
            }
            else
            {
                for(int times = 0; times < 200; times++)
                {
                    EntityFlameShotLinear entitySplit = new EntityFlameShotLinear(this.world, this.owner, 
                    (rand.nextFloat() * 0.25) - 0.125, (rand.nextFloat() * 0.25) - 0.125, (rand.nextFloat() * 0.25) - 0.125);

                    entitySplit.posX = this.posX;
                    entitySplit.posY = this.posY;
                    entitySplit.posZ = this.posZ;
                    this.world.spawnEntity(entitySplit);
                }
            }
/*
        }
*/
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
