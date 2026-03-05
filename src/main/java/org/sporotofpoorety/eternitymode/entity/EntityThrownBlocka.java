package org.sporotofpoorety.eternitymode.entity;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.sporotofpoorety.srpabsurdcraft.entity.EntityOrbVoidCustom;

import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;




public class EntityThrownBlocka extends EntityFallingBlock
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



    public boolean dealsDamage;
    public float thrownBlockDamage;


//Controller logic specific
    public String controlMode = "none";
    public String controllerReleaseMode = "none";

    public double expelRadians;
    public double expelForceHorizontal;
    public double expelForceVertical;
    public double expelGravity;
    public double expelAcceleration;

    public Vec3d controllerInitialVec = new Vec3d(0.0D, 0.0D, 0.0D);
    public double controllerGlueDistance;
    public boolean controllerReached;
    public boolean blockExpelled;

    public double stickX;
    public double stickY;
    public double stickZ;
    
    


    public EntityThrownBlocka(World worldIn)
    {
        super(worldIn);
    }

    public EntityThrownBlocka(World worldIn, double x, double y, double z, IBlockState fallingBlockState, EntityLivingBase owner, float thrownBlockDamage)
    {
        super(worldIn, x, y, z, fallingBlockState);

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

        this.gravitySpeed = 0.08D;
        this.acceleratesVertically = true;
        this.accelerationVal = 1.0D;
        
        this.dealsDamage = true;
        this.thrownBlockDamage = thrownBlockDamage;
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

    public void setBlockNormal(boolean normal)
    {
//Set gravity
        this.setNoGravity(!normal);
//Set clip
        this.noClip = !normal;
//Set deals damage
        this.dealsDamage = normal;
    }

    public void setBlockStick(double stickX, double stickY, double stickZ)
    {
        this.stickX = stickX;
        this.stickY = stickY;
        this.stickZ = stickZ;
    }
   

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);


        if(this.ownerUUID != null) { compound.setUniqueId("OwnerUUID", this.ownerUUID); }
        compound.setInteger("OwnerCheckCooldown", this.ownerCheckCooldown);
        compound.setInteger("OwnerCheckCooldownMax", this.ownerCheckCooldownMax);

        if(this.controllerUUID != null) { compound.setUniqueId("ControllerUUID", this.controllerUUID); }
        compound.setInteger("ControllerCheckCooldown", this.controllerCheckCooldown);
        compound.setInteger("ControllerCheckCooldownMax", this.controllerCheckCooldownMax);

        compound.setDouble("GravitySpeed", this.gravitySpeed);
        compound.setBoolean("AcceleratesVertically", this.acceleratesVertically);
        compound.setDouble("AccelerationVal", this.accelerationVal);


        compound.setBoolean("DealsDamage", this.dealsDamage);
        compound.setFloat("ThrownBlockDamage", this.thrownBlockDamage);


		compound.setString("ControlMode", this.controlMode);
		compound.setString("ControllerReleaseMode", this.controllerReleaseMode);


        compound.setDouble("ExpelRadians", this.expelRadians);
        compound.setDouble("ExpelForceHorizontal", this.expelForceHorizontal);
        compound.setDouble("ExpelForceVertical", this.expelForceVertical);
        compound.setDouble("ExpelGravity", this.expelGravity);
        compound.setDouble("ExpelAcceleration", this.expelAcceleration);

        if(this.controllerInitialVec != null)
        {
            compound.setDouble("ControllerInitialVecX", this.controllerInitialVec.x);
            compound.setDouble("ControllerInitialVecY", this.controllerInitialVec.y);
            compound.setDouble("ControllerInitialVecZ", this.controllerInitialVec.z);
        }
        compound.setDouble("ControllerGlueDistance", this.controllerGlueDistance);
        compound.setBoolean("ControllerReached", this.controllerReached);
        compound.setBoolean("BlockExpelled", this.blockExpelled);

        compound.setDouble("StickX", this.stickX);
        compound.setDouble("StickY", this.stickY);
        compound.setDouble("StickZ", this.stickZ);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);


        if (compound.hasKey("OwnerUUID")) { this.ownerUUID = compound.getUniqueId("OwnerUUID"); }
        if (compound.hasKey("OwnerCheckCooldown")) { this.ownerCheckCooldown = compound.getInteger("OwnerCheckCooldown"); }
        if (compound.hasKey("OwnerCheckCooldownMax")) { this.ownerCheckCooldownMax = compound.getInteger("OwnerCheckCooldownMax"); }

        if (compound.hasKey("ControllerUUID")) { this.controllerUUID = compound.getUniqueId("ControllerUUID"); }
        if (compound.hasKey("ControllerCheckCooldown")) { this.controllerCheckCooldown = compound.getInteger("ControllerCheckCooldown"); }
        if (compound.hasKey("ControllerCheckCooldownMax")) { this.controllerCheckCooldownMax = compound.getInteger("ControllerCheckCooldownMax"); }

        if (compound.hasKey("GravitySpeed")) { this.gravitySpeed = compound.getDouble("GravitySpeed"); }
        if (compound.hasKey("AcceleratesVertically")) { this.acceleratesVertically = compound.getBoolean("AcceleratesVertically"); }
        if (compound.hasKey("AccelerationVal")) { this.accelerationVal = compound.getDouble("AccelerationVal"); }


        if (compound.hasKey("DealsDamage")) { this.dealsDamage = compound.getBoolean("DealsDamage"); }
        if (compound.hasKey("ThrownBlockDamage")) { this.thrownBlockDamage = compound.getFloat("ThrownBlockDamage"); }


        if (compound.hasKey("ControlMode")) { this.controlMode = compound.getString("ControlMode"); }
        if (compound.hasKey("ControllerReleaseMode")) { this.controllerReleaseMode = compound.getString("ControllerReleaseMode"); }


        if (compound.hasKey("ExpelRadians")) { this.expelRadians = compound.getDouble("ExpelRadians"); }
        if (compound.hasKey("ExpelForceHorizontal")) { this.expelForceHorizontal = compound.getDouble("ExpelForceHorizontal"); }
        if (compound.hasKey("ExpelForceVertical")) { this.expelForceVertical = compound.getDouble("ExpelForceVertical"); }
        if (compound.hasKey("ExpelGravity")) { this.expelGravity = compound.getDouble("ExpelGravity"); }
        if (compound.hasKey("ExpelAcceleration")) { this.expelAcceleration = compound.getDouble("ExpelAcceleration"); }

        if (compound.hasKey("ControllerInitialVecX") 
        && compound.hasKey("ControllerInitialVecY") && compound.hasKey("ControllerInitialVecZ")) 
            { this.controllerInitialVec = new Vec3d(compound.getDouble("ControllerInitialVecX"), 
            compound.getDouble("ControllerInitialVecY"), compound.getDouble("ControllerInitialVecZ")); }
        if (compound.hasKey("ControllerGlueDistance")) { this.controllerGlueDistance = compound.getDouble("ControllerGlueDistance"); }
        if (compound.hasKey("ControllerReached")) { this.controllerReached = compound.getBoolean("ControllerReached"); }
        if (compound.hasKey("BlockExpelled")) { this.blockExpelled = compound.getBoolean("BlockExpelled"); }

        if (compound.hasKey("StickX")) { this.stickX = compound.getDouble("StickX"); }
        if (compound.hasKey("StickY")) { this.stickY = compound.getDouble("StickY"); }
        if (compound.hasKey("StickZ")) { this.stickZ = compound.getDouble("StickZ"); }
    }


    

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {

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

        }  


        this.performBasicMovement();


//Normal falling block logic
        super.onUpdate();


//If being controlled 
        if(this.controller != null) 
        {
//If not already expelled
            if(!this.blockExpelled)
            {
//Control by orb
                if(this.controller instanceof EntityOrbVoidCustom) { this.controlByOrb(); }
            }
        }




//Simple AoE damage, testing if it has any issues
        if(this.dealsDamage)
        {
            List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(1.25, 1.25, 1.25)));

            for (Entity entity : list)
            {
                if(entity == this.owner)
                {
                    continue;
                }
                else
                {
                	if(this.owner == null)
                	{
                		entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this), this.thrownBlockDamage);
                	}
                	else
                	{
                		entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.owner), this.thrownBlockDamage);
                	}
                }   
            }
        }


/*
        if (this.onGround && this.dealsDamage)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;

//Spawns particles within a randomized range
//specified number of times, will be useful for future reference
            for(int particleAt = 0; particleAt < 36; particleAt++)
            {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (rand.nextFloat() - rand.nextFloat()), this.posY + 0.5D, this.posZ + (rand.nextFloat() - rand.nextFloat()), (rand.nextFloat() - rand.nextFloat()), 1.0D, (rand.nextFloat() - rand.nextFloat()), Block.getIdFromBlock(this.world.getBlockState(this.getOrigin()).getBlock()));
            }
            
            this.setDead();
        }
*/
    }









    public void controlByOrb()
    {
        if(this.controlMode.equals("shower"))
        {
            this.controlByOrbShower();
        }
    }

    
    public void controlByOrbShower()
    {
        EntityOrbVoidCustom controllerOrb = (EntityOrbVoidCustom) this.controller;


//If at orb center
        if(this.controllerReached)
        {
//And orb hasn't started to deflate
            if(controllerOrb.getTimerDDD() <= controllerOrb.orbDeflatesWhen)
            {
//Follow orb
                this.setPosition(controllerOrb.posX, controllerOrb.posY, controllerOrb.posZ);
//No motion
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }


//If orb started deflating
            else if(controllerOrb.getTimerDDD() > controllerOrb.orbDeflatesWhen)
            {
//Expel
                this.expelByOrbShower();
            }
        }


//If not reached orb center yet
        else
        {
//If orb growing and not active yet
            if(controllerOrb.ticksExisted >= controllerOrb.getStartState() && controllerOrb.getTimerDDD() <= 0)
            {
//And first tick of orb growth
                if(controllerOrb.getTimeSinceIgnited() == 1)
                { 
//Set initial vec to orb
                    this.controllerInitialVec
                        = new Vec3d(controllerOrb.posX - this.posX, controllerOrb.posY - this.posY, controllerOrb.posZ - this.posZ)
                        .scale(1.25D / controllerOrb.getFuseState());
//Set glue distance
                    this.controllerGlueDistance 
                    = 1.5D * controllerInitialVec.length();
//Set block "not normal"
                    this.setBlockNormal(false);
                }


//In any case, move to orb,
//in a fraction of growth time + using orb motion
                this.motionX = (controllerInitialVec.x + controllerOrb.motionX);
                this.motionY = (controllerInitialVec.y + controllerOrb.motionY);
                this.motionZ = (controllerInitialVec.z + controllerOrb.motionZ);


//Check if close enough to orb to glue
                if(this.getDistance(controllerOrb) <= this.controllerGlueDistance)
                {
//If so set glued
                    this.controllerReached = true;
//Follow orb
                    this.setPosition(controllerOrb.posX, controllerOrb.posY, controllerOrb.posZ);
//No motion
                    this.motionX = 0.0D;
                    this.motionY = 0.0D;
                    this.motionZ = 0.0D;
                }                 
            }    
        }
    }




    public void expelByOrbShower()
    {
//Set block expelled
        this.blockExpelled = true;


//Get block expel mode

//If random release (radians provided by controller)
        if(controllerReleaseMode.equals("scatter"))
        {
//Random force
            double actualForce = this.expelForceHorizontal * rand.nextDouble();              
//Shoot out block
            this.setMovement(Math.cos(this.expelRadians) * actualForce, this.expelForceVertical, Math.sin(this.expelRadians) * actualForce,
//Flat-ish gravity and quick horizontal deceleration 
            this.expelGravity, false, this.expelAcceleration);
//Restore block normal behavior
            this.setBlockNormal(true);          
        }


//If aimed release (radians calculated at expel time)
        if(controllerReleaseMode.equals("aimed"))
        {
//If owner valid
            if(this.owner != null && (this.owner instanceof EntityLiving) && ((EntityLiving) this.owner).getAttackTarget() != null) 
            {
//Get owner target
                EntityLivingBase ownerTarget = ((EntityLiving) this.owner).getAttackTarget();
//Get radians to owner target 
                this.expelRadians = Math.atan2(ownerTarget.posZ - this.posZ, ownerTarget.posX - this.posX);
//Randomize radians
                this.expelRadians += (0.5D * Math.PI) * (rand.nextDouble() - rand.nextDouble());
            }
//If no valid owner target pick random direction
            else
            {
                this.expelRadians = (2.0D * Math.PI) * rand.nextDouble();
            }

//Random force
            double actualForce = this.expelForceHorizontal * rand.nextDouble();              
//Shoot out block
            this.setMovement(Math.cos(this.expelRadians) * actualForce, this.expelForceVertical, Math.sin(this.expelRadians) * actualForce,
//Flat-ish gravity and quick horizontal deceleration 
            this.expelGravity, false, this.expelAcceleration);
//Restore block normal behavior
            this.setBlockNormal(true);          
        }
    }




    public void fall(float distance, float damageMultiplier)
    {
        if(this.dealsDamage)
        {
            List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(1.25, 1.25, 1.25)));

            for (Entity entity : list)
            {
                if(entity == this.owner)
                {
                    continue;
                }
                else
                {
                	if(this.owner == null)
                	{
                		entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this), this.thrownBlockDamage);
                	}
                	else
                	{
                		entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.owner), this.thrownBlockDamage);
                	}   
                }
            }
        }


        super.fall(distance, damageMultiplier);
    }




    public void performBasicMovement()
    {
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

//Restore block to normal if no controller
    public void performControllerValidation()
    {
        if(!this.validateController()) { this.setBlockNormal(true); }
    }
}

