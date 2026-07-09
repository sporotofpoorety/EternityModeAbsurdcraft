package org.sporotofpoorety.eternitymode.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.sporotofpoorety.eternitymode.util.PuppetEntity;
import org.sporotofpoorety.eternitymode.util.PuppetNBT;




public abstract class EntityWithOwner extends Entity
{

    public boolean justReloaded;
    public boolean onReloadDespawn;

    public int realTicksExisted = 0;
    public int lifetimeMax = -1;

    public EntityLivingBase owner;
    public UUID ownerUUID;
    public boolean previousValidateOwnerFailed;

    public String puppetsStoredType = "default";
    public List<PuppetEntity> puppetEntities = new ArrayList<>();

    public double gravitySpeed;
    public boolean acceleratesVertically;
    public double accelerationVal;

    public boolean dontMove;

    public float damageVal;




    public EntityWithOwner(World worldIn)
    {
        super(worldIn);
    }


    public EntityWithOwner(World worldIn, double x, double y, double z, EntityLivingBase owner)
    {
        super(worldIn);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;

//Just safeguarding defaults
        this.owner = owner;
        if(this.owner != null) { this.ownerUUID = owner.getUniqueID(); }

//Defaults for entities that won't explicitly set this
        this.gravitySpeed = 0.08D;
        this.acceleratesVertically = true;
        this.accelerationVal = 1.0D;
    }

    public void setMovement(double speedX, double speedY, double speedZ, 
    double gravitySpeed, boolean acceleratesVertically, double accelerationVal)
    {
        this.motionX = speedX;
        this.motionY = speedY;
        this.motionZ = speedZ;


        this.gravitySpeed = gravitySpeed;
        this.acceleratesVertically = acceleratesVertically;
        this.accelerationVal = accelerationVal;
    }

    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setBoolean("OnReloadDespawn", this.onReloadDespawn);

        compound.setInteger("RealTicksExisted", this.realTicksExisted);
        compound.setInteger("LifetimeMax", this.lifetimeMax);

        if(this.ownerUUID != null) { compound.setUniqueId("OwnerUUID", this.ownerUUID); }

        compound.setString("PuppetsStoredType", this.puppetsStoredType);
        PuppetNBT.nbtWritePuppetList(this, compound);

        compound.setDouble("GravitySpeed", this.gravitySpeed);
        compound.setBoolean("AcceleratesVertically", this.acceleratesVertically);
        compound.setDouble("AccelerationVal", this.accelerationVal);

        compound.setBoolean("DontMove", this.dontMove);

        compound.setFloat("DamageVal", this.damageVal);
    }

    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        this.justReloaded = true;
        if (compound.hasKey("OnReloadDespawn")) { this.onReloadDespawn = compound.getBoolean("OnReloadDespawn"); }

        if (compound.hasKey("RealTicksExisted")) { this.realTicksExisted = compound.getInteger("RealTicksExisted"); }
        if (compound.hasKey("LifetimeMax")) { this.lifetimeMax = compound.getInteger("LifetimeMax"); }

        if (compound.hasKey("OwnerUUID")) 
        { 
            this.ownerUUID = compound.getUniqueId("OwnerUUID"); 
            this.validateOwner();
        }


        if (compound.hasKey("PuppetsStoredType")) { this.puppetsStoredType = compound.getString("PuppetsStoredType"); }
        PuppetNBT.nbtReadPuppetList(this, compound);

        if (compound.hasKey("GravitySpeed")) { this.gravitySpeed = compound.getDouble("GravitySpeed"); }
        if (compound.hasKey("AcceleratesVertically")) { this.acceleratesVertically = compound.getBoolean("AcceleratesVertically"); }
        if (compound.hasKey("AccelerationVal")) { this.accelerationVal = compound.getDouble("AccelerationVal"); }

        if (compound.hasKey("DontMove")) { this.dontMove = compound.getBoolean("DontMove"); }

        if (compound.hasKey("DamageVal")) { this.damageVal = compound.getFloat("DamageVal"); }
    }




    public void onUpdate()
    {
//If just reloaded
        if (!this.world.isRemote && this.justReloaded) 
        {
            this.justReloaded = false;

//Check for forced despawn
            if(this.onReloadDespawn)
            {
                this.setDead();
                return;
            }

//Recreate puppet entities from stored data
            this.recreatePuppetEntities();
        }

//Adjust puppet entities
        if(!this.world.isRemote) { this.adjustPuppetEntities(); }


        super.onUpdate();


//Testing if this is the right place to put it
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;



//Reliable ticks existed
        ++this.realTicksExisted;


//Server side
        if(!this.world.isRemote)
        {
//Universalized lifetime
            if(this.realTicksExisted > this.lifetimeMax) { this.onLifetimeExpire(); return; }


//Periodically validate owner
            if(this.realTicksExisted % 20 == 0)
            {
//Checks for two failed validations in a row
                if(this.performOwnerValidation()) { this.previousValidateOwnerFailed = false; }
                else
                {
                    this.previousValidateOwnerFailed = true;
                }
            }
        }    
    }

    public void onLifetimeExpire()
    {
        this.setDead();
    }

    public void performBasicMovement()
    {
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);


        this.motionX *= this.accelerationVal;
        if(this.acceleratesVertically) { this.motionY *= this.accelerationVal; }
        this.motionZ *= this.accelerationVal;


        if (!this.hasNoGravity())
        {
            this.motionY -= this.gravitySpeed;
        }
    }



    public boolean performOwnerValidation()
    {
        return this.validateOwner();
    }


    public boolean ownerValidConditions(Entity toValidate)
    {
        return (toValidate instanceof EntityLivingBase);
    }


//Validate owner and return if successful
    public boolean validateOwner()
    {

//If there is a owner UUID
        if(this.ownerUUID != null)
        {
//But no valid owner 
            if(this.owner == null)
            {
//Try to get owner from UUID
                Entity foundEntity  
                = ((WorldServer)world).getEntityFromUuid(this.ownerUUID);


//If owner found
//and owner conditions met
                if(foundEntity != null && this.ownerValidConditions(foundEntity))
                {
//Restore owner
                    this.owner = (EntityLivingBase) foundEntity;
//Check successful
                    return true;
                }
            }

//If there's both a owner and its UUID
            else
            {
//Check successful
                return true;
            }
        }


//If no UUID, check failed
        return false;

    }




    public void recreatePuppetEntities()
    {
        for(PuppetEntity puppetEntity : this.puppetEntities)
        {
            this.recreatePuppetEntity(puppetEntity);
        }
    }

    public void recreatePuppetEntity(PuppetEntity puppetEntity)
    {

    }

    public void adjustPuppetEntities()
    {
        for(PuppetEntity puppetEntity : this.puppetEntities)
        {
            this.adjustPuppetEntity(puppetEntity);
        }
    }

    public void adjustPuppetEntity(PuppetEntity puppetEntity)
    {

    }
    
    public void moveWithPuppets(double x, double y, double z)
    {
        this.move(MoverType.SELF, x, y, z);

        for(PuppetEntity puppet : this.puppetEntities)
        {
            puppet.entity.move(MoverType.SELF, x, y, z);
        }
    }

    public void performBasicMovementWithPuppets()
    {
        this.moveWithPuppets(this.motionX, this.motionY, this.motionZ);


        this.motionX *= this.accelerationVal;
        if(this.acceleratesVertically) { this.motionY *= this.accelerationVal; }
        this.motionZ *= this.accelerationVal;


        if (!this.hasNoGravity())
        {
            this.motionY -= this.gravitySpeed;
        }
    }

    public void moveAndPositionPuppets(double x, double y, double z)
    {
        this.move(MoverType.SELF, x, y, z);

        for(PuppetEntity puppet : this.puppetEntities)
        {
            puppet.entity.setPositionAndUpdate(puppet.entity.posX + x, puppet.entity.posY + y, puppet.entity.posZ + z);
        }
    }

    public void performBasicMovementAndPositionPuppets()
    {
        this.moveAndPositionPuppets(this.motionX, this.motionY, this.motionZ);


        this.motionX *= this.accelerationVal;
        if(this.acceleratesVertically) { this.motionY *= this.accelerationVal; }
        this.motionZ *= this.accelerationVal;


        if (!this.hasNoGravity())
        {
            this.motionY -= this.gravitySpeed;
        }
    }

}
