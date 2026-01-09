package org.sporotofpoorety.eternitymode.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShot;



public class EntityFlameShotLinear extends EntityFlameShot {


    public double speedX;
    public double speedY;
    public double speedZ;



    public EntityFlameShotLinear(World worldIn) 
    {
        super(worldIn);

        this.setSize(0.5F, 0.5F);

        this.speedX = 0.0D;
        this.speedY = 0.0D;
        this.speedZ = 0.0D;
    }

    public EntityFlameShotLinear(World worldIn, EntityLivingBase owner, double speedX, double speedY, double speedZ) 
    {
        super(worldIn, owner);

        this.setSize(0.5F, 0.5F);

        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
    }




    @Override
    public void onUpdate()
    {
        motionX = this.speedX;
        motionY = this.speedY;
        motionZ = this.speedZ;
        super.onUpdate();
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
