package org.sporotofpoorety.eternitymode.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;


import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;
import org.sporotofpoorety.eternitymode.util.ExplosionUtil;




public class EntityDemonScythe extends EntityWithOwner 
{

    public EntityDemonScythe(World world) 
    {
        super(world);
        setSize(2.0F, 2.0F);
//Should noclip by default ig
        this.noClip = true;
    }

    public EntityDemonScythe(World worldIn, double x, double y, double z, 
    EntityLivingBase owner,
    int lifetimeMax) 
    {
        super(worldIn, x, y, z, owner);
        setSize(0.5F, 0.5F);
//Should noclip by default ig
        this.noClip = true;

        this.lifetimeMax = lifetimeMax;

        this.setNoGravity(true);
        this.accelerationVal = accelerationVal;
	}


    @Override
    protected void entityInit() {}




    @Override
    public void onUpdate() 
    {
        if(this.ticksExisted % 20 == 0 || (this.ticksExisted < 2)) { System.out.println("I exist"); }

        if(!this.world.isRemote && this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.1D, 0.1D, 0.1D))) { this.setDead(); return; }

        super.onUpdate();

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
