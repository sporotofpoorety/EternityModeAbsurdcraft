package org.sporotofpoorety.eternitymode.entity;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;





public abstract class EntityWithOwner extends Entity
{

    public EntityLivingBase owner;
    public UUID ownerUUID;
    public int ownerCheckCooldown;
    public int ownerCheckCooldownMax;

    public Entity controller;
    public UUID controllerUUID;
    public int controllerCheckCooldown;
    public int controllerCheckCooldownMax;

    public double gravitySpeed;
    public boolean acceleratesVertically;
    public double accelerationVal;




    public EntityWithOwner(World worldIn)
    {
        super(worldIn);
    }


    public EntityWithOwner(World worldIn, EntityLivingBase owner)
    {
        super(worldIn);

//Just safeguarding defaults
        this.owner = owner;
        if(this.owner != null) { this.ownerUUID = owner.getUniqueID(); }
        this.ownerCheckCooldown = 0;
        this.ownerCheckCooldownMax = 20;

//Just safeguarding defaults
        this.controller = null;
        this.controllerUUID = null;
        this.controllerCheckCooldown = 0;
        this.controllerCheckCooldownMax = 20;

        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;

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

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        if(this.ownerUUID != null) { compound.setUniqueId("OwnerUUID", this.ownerUUID); }
        compound.setInteger("OwnerCheckCooldown", this.ownerCheckCooldown);
        compound.setInteger("OwnerCheckCooldownMax", this.ownerCheckCooldownMax);

        if(this.controllerUUID != null) { compound.setUniqueId("ControllerUUID", this.controllerUUID); }
        compound.setInteger("ControllerCheckCooldown", this.controllerCheckCooldown);
        compound.setInteger("ControllerCheckCooldownMax", this.controllerCheckCooldownMax);

        compound.setDouble("GravitySpeed", this.gravitySpeed);
        compound.setBoolean("AcceleratesVertically", this.acceleratesVertically);
        compound.setDouble("AccelerationVal", this.accelerationVal);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("OwnerUUID")) { this.ownerUUID = compound.getUniqueId("OwnerUUID"); }
        if (compound.hasKey("OwnerCheckCooldown")) { this.ownerCheckCooldown = compound.getInteger("OwnerCheckCooldown"); }
        if (compound.hasKey("OwnerCheckCooldownMax")) { this.ownerCheckCooldownMax = compound.getInteger("OwnerCheckCooldownMax"); }

        if (compound.hasKey("ControllerUUID")) { this.controllerUUID = compound.getUniqueId("ControllerUUID"); }
        if (compound.hasKey("ControllerCheckCooldown")) { this.controllerCheckCooldown = compound.getInteger("ControllerCheckCooldown"); }
        if (compound.hasKey("ControllerCheckCooldownMax")) { this.controllerCheckCooldownMax = compound.getInteger("ControllerCheckCooldownMax"); }

        if (compound.hasKey("GravitySpeed")) { this.gravitySpeed = compound.getDouble("GravitySpeed"); }
        if (compound.hasKey("AcceleratesVertically")) { this.acceleratesVertically = compound.getBoolean("AcceleratesVertically"); }
        if (compound.hasKey("AccelerationVal")) { this.accelerationVal = compound.getDouble("AccelerationVal"); }
    }




    public void onUpdate()
    {
        super.onUpdate();


//Server side
        if(!this.world.isRemote)
        {

//If no owner check cooldown
            if(this.ownerCheckCooldown <= 0)
            {
//Try to validate owner
                this.performOwnerValidation();
            }
            else
            {
//Else decrement owner check cooldown
                --this.ownerCheckCooldown;
            }


//If no controller check cooldown
            if(this.controllerCheckCooldown <= 0)
            {
//Try to validate controller
                this.performControllerValidation();
            }
            else
            {
//Else decrement controller check cooldown
                --this.controllerCheckCooldown;
            }

        }    
    }

    public void performBasicMovement()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;


        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);


        if (!this.hasNoGravity())
        {
            this.motionY -= this.gravitySpeed;
        }


        this.motionX *= this.accelerationVal;
        if(this.acceleratesVertically) { this.motionY *= this.accelerationVal; }
        this.motionZ *= this.accelerationVal;
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

//Rate-limit failed owner checks
                else
                {
                    this.ownerCheckCooldown = this.ownerCheckCooldownMax;
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

    public boolean ownerValidConditions(Entity toValidate)
    {
        return (toValidate instanceof EntityLivingBase);
    }

    public void performOwnerValidation()
    {
        this.validateOwner();
    }




//Validate controller and return if successful
    public boolean validateController()
    {

//If there is a controller UUID
        if(this.controllerUUID != null)
        {
//But no valid controller 
            if(this.controller == null)
            {
//Try to get controller from UUID
                Entity foundEntity  
                = ((WorldServer)world).getEntityFromUuid(this.controllerUUID);


//If controller found
//and controller conditions met
                if(foundEntity != null && this.controllerValidConditions(foundEntity))
                {
//Restore controller
                    this.controller = foundEntity;
//Check successful
                    return true;
                }

//Rate-limit failed controller checks
                else
                {
                    this.controllerCheckCooldown = this.controllerCheckCooldownMax;
                }
            }

//If there's both a controller and its UUID
            else
            {
//Check successful
                return true;
            }
        }


//If no UUID, check failed
        return false;

    }

    public boolean controllerValidConditions(Entity toValidate)
    {
        return true;
    }

    public void performControllerValidation()
    {
        this.validateController();
    }
}
