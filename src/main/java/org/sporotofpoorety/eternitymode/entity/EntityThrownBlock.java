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

import org.sporotofpoorety.eternitymode.entity.EntityOrbVoidCustom;




public class EntityThrownBlock extends Entity
{

    public EntityLivingBase owner;


//For if owner is orb
//Note to self: refactor later to be more modular
    public EntityOrbVoidCustom controllerOrb;
    public UUID controllerOrbUUID;
    public double expelRadians;


    public Vec3d controllerInitialVec;
    public double controllerGlueDistance;
    public boolean controllerReached;
    public boolean blockExpelled;


    public int controlMode;
    public int controllerReleaseMode;


    private IBlockState fallTile;
    public NBTTagCompound tileEntityData;
    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityFallingBlock.class, DataSerializers.BLOCK_POS);


    public boolean dealsDamage;
    public float thrownBlockDamage;


    public double gravitySpeed;
    public boolean acceleratesVertically;
    public double accelerationVal;
    
    


    public EntityThrownBlock(World worldIn)
    {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    public EntityThrownBlock(World worldIn, EntityLivingBase owner, BlockPos blockPos, double x, double y, double z, float thrownBlockDamage)
    {
        this(worldIn);

        this.owner = owner;


//Just safeguarding defaults
        this.controllerOrb = null;
        this.controllerOrbUUID = null;
        this.expelRadians = 6.9420D;

        this.controllerInitialVec = new Vec3d(0.0D, -0.1D, 0.0D);
        this.controllerGlueDistance = 6.9420D;
        this.controllerReached = false;
        this.blockExpelled = false;

        this.controlMode = 1;
        this.controllerReleaseMode = 1;


        this.setOrigin(blockPos);


        if(this.owner != null) { this.setLocationAndAngles(owner.posX, owner.posY, owner.posZ, owner.rotationYaw, 0.0F); }
            else { this.setPosition(x, y, z); } 
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;

        
        this.dealsDamage = true;
        this.thrownBlockDamage = thrownBlockDamage;


        this.motionX = 0.69420D;
        this.motionY = 0.69420D;
        this.motionZ = 0.69420D;


        this.gravitySpeed = 0.08D;
        this.acceleratesVertically = true;
        this.accelerationVal = 0.98D;
    }

    protected void entityInit()
    {
        this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
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


    
  


    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
//NOTE TO SELF: LATER REFACTOR THIS INTO SUPERCLASS "validateOwner()" and "validateController()"
//Try to validate controller (for stability)
        if((this.ticksExisted % 20) == 0 && !world.isRemote)
        {
//If no controller found resume normal behavior     
            if(!this.validateController()) { this.setBlockNormal(true); }
        }


//If being controlled by a void orb and not already expelled
        if(this.controllerOrb != null && !this.blockExpelled)
        {
            this.controlByOrb();
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


        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;


        if (!this.hasNoGravity())
        {
            this.motionY -= this.gravitySpeed;
        }


        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);


        this.motionX *= this.accelerationVal;
        if(this.acceleratesVertically) { this.motionY *= this.accelerationVal; }
        this.motionZ *= this.accelerationVal;


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
    }




//Validate controller and return if successful
    public boolean validateController()
    {
        boolean checkSuccessful = false;

   
//If no valid controller
        if(this.controllerOrb == null
//But there is stored controller UUID
        && this.controllerOrbUUID != null) 
        {
//Try to get entity from UUID
            Entity foundEntity  
            = ((WorldServer)world).getEntityFromUuid(this.controllerOrbUUID);

//If it's orb void
            if (foundEntity instanceof EntityOrbVoidCustom) 
            {
//Set controller orb to it
                this.controllerOrb = (EntityOrbVoidCustom) foundEntity;
//Check successful
                checkSuccessful = true;
            }

//If no controller found
            else
            {
//Check failed
                checkSuccessful = false;
            }
        }

    
        return checkSuccessful;
    }




    public void controlByOrb()
    {
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
                this.expelByOrb();
            }
        }


//If not reached orb center yet
        else
        {
//If orb growing and not active yet
            if(controllerOrb.ticksExisted >= controllerOrb.getStartState() && controllerOrb.getTimerDDD() <= 0)
            {
//And first tick of orb growth
                if(this.controllerOrb.getTimeSinceIgnited() == 1)
                { 
//Set initial vec to orb
                    this.controllerInitialVec
                        = new Vec3d(this.controllerOrb.posX - this.posX, this.controllerOrb.posY - this.posY, this.controllerOrb.posZ - this.posZ)
                        .scale(1.0D / (controllerOrb.getFuseState() / 2.0D));
//Set glue distance
                    this.controllerGlueDistance 
                    = 2.0D * controllerInitialVec.length();
//Set block "not normal"
                    this.setBlockNormal(false);
                }


//In any case, move to orb,
//in a fraction of growth time + using orb motion
                this.motionX = (controllerInitialVec.x + controllerOrb.motionX);
                this.motionY = (controllerInitialVec.y + controllerOrb.motionY);
                this.motionZ = (controllerInitialVec.z + controllerOrb.motionZ);


//Check if close enough to orb to glue
                if(this.getDistance(this.controllerOrb) <= this.controllerGlueDistance)
                {
//If so set glued
                    this.controllerReached = true;
//Follow orb
                    this.setPosition(this.controllerOrb.posX, this.controllerOrb.posY, this.controllerOrb.posZ);
//No motion
                    this.motionX = 0.0D;
                    this.motionY = 0.0D;
                    this.motionZ = 0.0D;
                }                 
            }    
        }
    }


    public void expelByOrb()
    {
//Set block expelled
        this.blockExpelled = true;


//Get block expel mode

//If random release (radians provided by controller)
        if(controllerReleaseMode == 1)
        {
//Random force
            double expelForce = (80.0D * rand.nextDouble()) / 20.0D;              
//Shoot out block
            this.setMovement(Math.cos(this.expelRadians) * expelForce, -2.0D, Math.sin(this.expelRadians) * expelForce,
//Flat-ish gravity and quick horizontal deceleration 
            0.01D, false, 0.96D);
//Restore block normal behavior
            this.setBlockNormal(true);          
        }


//If aimed release (radians calculated at expel time)
        if(controllerReleaseMode == 2)
        {
/*
//If orb owner valid and orb owner target valid
            if(controllerOrb.ownerCustom != null && controllerOrb.ownerCustom.getAttackTarget() != null) 
            {
//Get owner target
                EntityLivingBase ownerTarget = controllerOrb.ownerCustom.getAttackTarget();
//Get radians to owner target 
                this.expelRadians = Math.atan2(ownerTargetZ - this.posZ, ownerTarget.posX - this.posX);
//Randomize radians
                this.expelRadians += (0.5D * Math.PI) * (rand.nextDouble() - rand.nextDouble());
            }
*/
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
            double expelForce = (80.0D * rand.nextDouble()) / 20.0D;              
//Shoot out block
            this.setMovement(Math.cos(this.expelRadians) * expelForce, -2.0D, Math.sin(this.expelRadians) * expelForce,
//Flat-ish gravity and quick horizontal deceleration 
            0.01D, false, 0.96D);
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
    }



    public void setOrigin(BlockPos p_184530_1_)
    {
        this.dataManager.set(ORIGIN, p_184530_1_);
    }


    public BlockPos getOrigin()
    {
        return (BlockPos)this.dataManager.get(ORIGIN);
    }


    /**
     * returns null or the entityliving it was placed or ignited by
     */
    public EntityLivingBase getOwner()
    {
        return this.owner;
    }


    private void explode()
    {
    	boolean flag = true;
    	

       // this.getEntityWorld().createExplosion(this, this.posX, this.posY + (double)(this.height / 16.0F), this.posZ, 1, flag);
    }






    public float getEyeHeight()
    {
        return 0.0F;
    }
    
    /**
     * Return whether this entity should be rendered as on fire.
     */
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire()
    {
        return false;
    }
    

    @SideOnly(Side.CLIENT)
    public World getWorldObj()
    {
        return this.world;
    }


    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }


    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }


    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean canBeAttackedWithItem()
    {
        return false;
    }
    
    


    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        if(this.controllerOrbUUID != null) { compound.setUniqueId("ControllerOrbUUID", this.controllerOrbUUID); }
        compound.setDouble("ExpelRadians", this.expelRadians);

        if(this.controllerInitialVec != null)
        {
            compound.setDouble("ControllerInitialVecX", this.controllerInitialVec.x);
            compound.setDouble("ControllerInitialVecY", this.controllerInitialVec.y);
            compound.setDouble("ControllerInitialVecZ", this.controllerInitialVec.z);
        }
        compound.setDouble("ControllerGlueDistance", this.controllerGlueDistance);
        compound.setBoolean("ControllerReached", this.controllerReached);
        compound.setBoolean("BlockExpelled", this.blockExpelled);

		compound.setInteger("ControlMode", this.controlMode);
		compound.setInteger("ControllerReleaseMode", this.controllerReleaseMode);


		compound.setInteger("BlockPosX", this.getOrigin().getX());
		compound.setInteger("BlockPosY", this.getOrigin().getY());
		compound.setInteger("BlockPosZ", this.getOrigin().getZ());

        compound.setBoolean("DealsDamage", this.dealsDamage);
        compound.setFloat("ThrownBlockDamage", this.thrownBlockDamage);

        compound.setDouble("GravitySpeed", this.gravitySpeed);
        compound.setBoolean("AcceleratesVertically", this.acceleratesVertically);
        compound.setDouble("AccelerationVal", this.accelerationVal);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("ControllerOrbUUID")) { this.controllerOrbUUID = compound.getUniqueId("ControllerOrbUUID"); }
        if (compound.hasKey("ExpelRadians")) { this.expelRadians = compound.getDouble("ExpelRadians"); }

        if (compound.hasKey("ControllerInitialVecX") 
        && compound.hasKey("ControllerInitialVecY") && compound.hasKey("ControllerInitialVecZ")) 
            { this.controllerInitialVec = new Vec3d(compound.getDouble("ControllerInitialVecX"), 
            compound.getDouble("ControllerInitialVecY"), compound.getDouble("ControllerInitialVecZ")); }
        if (compound.hasKey("ControllerGlueDistance")) { this.controllerGlueDistance = compound.getDouble("ControllerGlueDistance"); }
        if (compound.hasKey("ControllerReached")) { this.controllerReached = compound.getBoolean("ControllerReached"); }
        if (compound.hasKey("BlockExpelled")) { this.blockExpelled = compound.getBoolean("BlockExpelled"); }

        if (compound.hasKey("ControlMode")) { this.controlMode = compound.getInteger("ControlMode"); }
        if (compound.hasKey("ControllerReleaseMode")) { this.controllerReleaseMode = compound.getInteger("ControllerReleaseMode"); }


        if (compound.hasKey("BlockPosX") && compound.hasKey("BlockPosY") && compound.hasKey("BlockPosZ"))
        {
            int X = 0;
            int Y = 0;
            int Z = 0;

	        X = compound.getInteger("BlockPosX");
	        Y = compound.getInteger("BlockPosY");
	        Z = compound.getInteger("BlockPosZ");

    	    this.setOrigin(new BlockPos(X,Y,Z));
        } else { this.setOrigin(new BlockPos(0, 0, 0)); }

        if (compound.hasKey("DealsDamage")) { this.dealsDamage = compound.getBoolean("DealsDamage"); }
        if (compound.hasKey("ThrownBlockDamage")) { this.thrownBlockDamage = compound.getFloat("ThrownBlockDamage"); }

        if (compound.hasKey("GravitySpeed")) { this.gravitySpeed = compound.getDouble("GravitySpeed"); }
        if (compound.hasKey("AcceleratesVertically")) { this.acceleratesVertically = compound.getBoolean("AcceleratesVertically"); }
        if (compound.hasKey("AccelerationVal")) { this.accelerationVal = compound.getDouble("AccelerationVal"); }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInvisible() { return true; }
}

