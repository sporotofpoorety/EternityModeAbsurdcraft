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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.sporotofpoorety.eternitymode.util.PuppetEntity;




public abstract class EntityWithOwner extends Entity
{

    public int realTicksExisted = 0;
    public int lifetimeMax = -1;

    public EntityLivingBase owner;
    public UUID ownerUUID;
    public int ownerCheckCooldown;
    public int ownerCheckCooldownMax;

    public Entity controller;
    public UUID controllerUUID;
    public int controllerCheckCooldown;
    public int controllerCheckCooldownMax;

    public List<PuppetEntity> puppetEntities = new ArrayList<>();

    public double gravitySpeed;
    public boolean acceleratesVertically;
    public double accelerationVal;




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
        this.ownerCheckCooldown = 0;
        this.ownerCheckCooldownMax = 20;

//Just safeguarding defaults
        this.controller = null;
        this.controllerUUID = null;
        this.controllerCheckCooldown = 0;
        this.controllerCheckCooldownMax = 20;

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

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("RealTicksExisted", this.realTicksExisted);
        compound.setInteger("LifetimeMax", this.lifetimeMax);

        if(this.ownerUUID != null) { compound.setUniqueId("OwnerUUID", this.ownerUUID); }
        compound.setInteger("OwnerCheckCooldown", this.ownerCheckCooldown);
        compound.setInteger("OwnerCheckCooldownMax", this.ownerCheckCooldownMax);

        if(this.controllerUUID != null) { compound.setUniqueId("ControllerUUID", this.controllerUUID); }
        compound.setInteger("ControllerCheckCooldown", this.controllerCheckCooldown);
        compound.setInteger("ControllerCheckCooldownMax", this.controllerCheckCooldownMax);

        this.nbtWritePuppetList(compound);

        compound.setDouble("GravitySpeed", this.gravitySpeed);
        compound.setBoolean("AcceleratesVertically", this.acceleratesVertically);
        compound.setDouble("AccelerationVal", this.accelerationVal);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("RealTicksExisted")) { this.realTicksExisted = compound.getInteger("RealTicksExisted"); }
        if (compound.hasKey("LifetimeMax")) { this.lifetimeMax = compound.getInteger("LifetimeMax"); }

        if (compound.hasKey("OwnerUUID")) { this.ownerUUID = compound.getUniqueId("OwnerUUID"); }
        if (compound.hasKey("OwnerCheckCooldown")) { this.ownerCheckCooldown = compound.getInteger("OwnerCheckCooldown"); }
        if (compound.hasKey("OwnerCheckCooldownMax")) { this.ownerCheckCooldownMax = compound.getInteger("OwnerCheckCooldownMax"); }

        if (compound.hasKey("ControllerUUID")) { this.controllerUUID = compound.getUniqueId("ControllerUUID"); }
        if (compound.hasKey("ControllerCheckCooldown")) { this.controllerCheckCooldown = compound.getInteger("ControllerCheckCooldown"); }
        if (compound.hasKey("ControllerCheckCooldownMax")) { this.controllerCheckCooldownMax = compound.getInteger("ControllerCheckCooldownMax"); }

        this.nbtReadPuppetList(compound);

        if (compound.hasKey("GravitySpeed")) { this.gravitySpeed = compound.getDouble("GravitySpeed"); }
        if (compound.hasKey("AcceleratesVertically")) { this.acceleratesVertically = compound.getBoolean("AcceleratesVertically"); }
        if (compound.hasKey("AccelerationVal")) { this.accelerationVal = compound.getDouble("AccelerationVal"); }
    }


//Write puppet entities to NBT
    public void nbtWritePuppetList(NBTTagCompound compound)
    {
//Puppet array to store
        NBTTagList puppetListToStore = new NBTTagList();

//For each puppet entity  
        for (PuppetEntity puppetEntity : this.puppetEntities) 
        {
//Make puppet map
            NBTTagCompound puppetToStore = new NBTTagCompound();
                puppetToStore.setDouble("PuppetOffsetX", puppetEntity.offsetX);
                puppetToStore.setDouble("PuppetOffsetY", puppetEntity.offsetY);
                puppetToStore.setDouble("PuppetOffsetZ", puppetEntity.offsetZ);
                puppetToStore.setInteger("PuppetTime", puppetEntity.controlTime);
                puppetToStore.setInteger("PuppetState", puppetEntity.controlState);
                puppetToStore.setUniqueId("PuppetUUID", puppetEntity.puppetUUID);
//Append it to puppet array
            puppetListToStore.appendTag(puppetToStore);
        }
        
        compound.setTag("PuppetEntityArray", puppetListToStore);   
    }


//Read puppet list from NBT
    public void nbtReadPuppetList(NBTTagCompound compound)
    {
//First clear puppet list
        this.puppetEntities.clear();

//Check for puppet list
        if (compound.hasKey("PuppetEntityArray")) 
        {
//It's an array of maps specifically
            NBTTagList storedPuppetList = compound.getTagList("PuppetEntityArray", 10);
            
//For each stored puppet
            for (int i = 0; i < storedPuppetList.tagCount(); i++) 
            {
//Fetch it as compound
                NBTTagCompound storedPuppet = storedPuppetList.getCompoundTagAt(i);

//Make corresponding puppet entity
                PuppetEntity puppet = new PuppetEntity
                (
                    null,
                    storedPuppet.getDouble("PuppetOffsetX"),
                    storedPuppet.getDouble("PuppetOffsetY"),
                    storedPuppet.getDouble("PuppetOffsetZ"),
                    storedPuppet.getInteger("PuppetTime"),
                    storedPuppet.getInteger("PuppetState")
                );
                puppet.puppetUUID = storedPuppet.getUniqueId("PuppetUUID");

//Store in the puppet list
                this.puppetEntities.add(puppet);
            }
        }
    }




    public void onUpdate()
    {
        super.onUpdate();


/*
//Testing if this is the right place to put it
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
*/


//Universalized lifetime
            ++this.realTicksExisted;
            if(this.realTicksExisted >= this.lifetimeMax) { this.onLifetimeExpire(); return; }


//Server side
        if(!this.world.isRemote)
        {
//If no owner check cooldown
            if(this.ownerCheckCooldown <= 0)
            {
//Try to validate owner
                this.performOwnerValidation();
//Apply check cooldown
                this.ownerCheckCooldown = this.ownerCheckCooldownMax;
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
//Apply check cooldown
                this.controllerCheckCooldown = this.controllerCheckCooldownMax;
            }
            else
            {
//Else decrement controller check cooldown
                --this.controllerCheckCooldown;
            }


//If this has puppet entities
            if(!this.puppetEntities.isEmpty())
            {
//Validate, restore, and clean
                this.performPuppetsValidation();
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




//Validate puppet entities
    public void validatePuppets()
    {
//Iterator for them
        Iterator<PuppetEntity> iter = this.puppetEntities.iterator();


//While still having puppets
        while (iter.hasNext()) 
        {
//Get next puppet and increment iterator there
            PuppetEntity puppet = iter.next();

//If puppet invalid
            if(!validatePuppet(puppet)) 
            {
//Remove puppet
                iter.remove();
            }
        }
    }

    public boolean validatePuppet(PuppetEntity puppet)
    {
//If puppet entity not null
        if (puppet.entity != null) 
        {
//Return whether it's alive
            return !puppet.entity.isDead;
        }
        

//If puppet has actual UUID
        if (puppet.puppetUUID != null) 
        {
//Search for corresponding entity
            Entity foundEntity = ((WorldServer) this.world).getEntityFromUuid(puppet.puppetUUID);

//If found any
            if (foundEntity != null) 
            {
//Restore puppet entity
                puppet.entity = foundEntity;
//Test successful
                return true;
            }
        }
     
   
//If all tests failed, false
        return false;
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




    public boolean ownerValidConditions(Entity toValidate)
    {
        return (toValidate instanceof EntityLivingBase);
    }

    public void performOwnerValidation()
    {
        this.validateOwner();
    }


    public boolean controllerValidConditions(Entity toValidate)
    {
        return true;
    }

    public void performControllerValidation()
    {
        this.validateController();
    }


    public boolean puppetValidConditions(Entity toValidate)
    {
        return true;
    } 

    public void performPuppetsValidation()
    {
        this.validatePuppets();
    }

}
