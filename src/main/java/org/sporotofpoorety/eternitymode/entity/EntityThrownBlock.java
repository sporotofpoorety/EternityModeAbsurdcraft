package org.sporotofpoorety.eternitymode.entity;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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

import org.sporotofpoorety.eternitymode.entity.EntityEarthPiece;
import org.sporotofpoorety.eternitymode.entity.EntityOrbVoidCustom;
import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;




public class EntityThrownBlock extends EntityWithOwner
{
    private IBlockState fallTile;
    public NBTTagCompound tileEntityData;
    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityFallingBlock.class, DataSerializers.BLOCK_POS);


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
    
    


    public EntityThrownBlock(World worldIn)
    {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    public EntityThrownBlock(World worldIn, EntityLivingBase owner, BlockPos blockPos, double x, double y, double z, float thrownBlockDamage)
    {
        super(worldIn, owner);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);


        this.setOrigin(blockPos);


        if(this.owner != null) { this.setLocationAndAngles(owner.posX, owner.posY, owner.posZ, owner.rotationYaw, 0.0F); }
            else { this.setPosition(x, y, z); } 
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;

        
        this.dealsDamage = true;
        this.thrownBlockDamage = thrownBlockDamage;
    }

    protected void entityInit()
    {
        this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
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

    public void setBlockShower(String controlMode, String controllerReleaseMode,
    double expelForceHorizontal, double expelForceVertical,
    double expelGravity, double expelAcceleration)
    {
        this.controlMode = controlMode;
        this.controllerReleaseMode = controllerReleaseMode;

        this.expelForceHorizontal = expelForceHorizontal;
        this.expelForceVertical = expelForceVertical;
        this.expelGravity = expelGravity;
        this.expelAcceleration = expelAcceleration;
    }

    public void setBlockPiece(String pieceType, double stickX, double stickY, double stickZ,
    double expelForceHorizontal, double expelForceVertical,
    double expelGravity, double expelAcceleration)
    {
        this.controlMode = pieceType;

        this.stickX = stickX;
        this.stickY = stickY;
        this.stickZ = stickZ;

        this.expelForceHorizontal = expelForceHorizontal;
        this.expelForceVertical = expelForceVertical;
        this.expelGravity = expelGravity;
        this.expelAcceleration = expelAcceleration;    
    }
   

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);


		compound.setInteger("BlockPosX", this.getOrigin().getX());
		compound.setInteger("BlockPosY", this.getOrigin().getY());
		compound.setInteger("BlockPosZ", this.getOrigin().getZ());

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
//Block restored to normal if no owner
        super.onUpdate();


//If being controlled 
        if(this.controller != null) 
        {
//If not already expelled
            if(!this.blockExpelled)
            {
//Control by orb
                if(this.controller instanceof EntityOrbVoidCustom) { this.controlByOrb(); }
//Control by piece
                if(this.controller instanceof EntityEarthPiece) { this.controlByPiece(); }
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


        this.performBasicMovement();


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




//Restore block to normal if no owner
    public void performControllerValidation()
    {
        if(!this.validateController()) { this.setBlockNormal(true); }
    }




    public void controlByOrb()
    {
        if(this.controlMode.equals("shower"))
        {
            this.controlByOrbShower();
        }
    }


    public void controlByPiece()
    {
        if(this.controlMode.equals("spin"))
        {
            this.controlByPieceSpin();        
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


//If controlled by an earth piece (spin)
    public void controlByPieceSpin()
    {
        EntityEarthPiece controllerPiece = (EntityEarthPiece) this.controller;


//If controller piece is at "flung"
        if(controllerPiece.phaseAt.equals("flung"))
        {
//If this collided
            if(this.collidedHorizontally || this.collidedVertically)
            {
//Expel blocks
                controllerPiece.phaseAt = "expel";
            }
        }

//If piece at expel (also triggers for losing owner)
        if(controllerPiece.phaseAt.equals("expel"))
        {
//Expel
            this.expelByPieceSpin();
//Cancel everything else
            return;
        }


//If at position relative to piece
        if(this.controllerReached)
        {
//But piece not at expel
            if(!controllerPiece.phaseAt.equals("expel"))
            {
//Follow piece (at offset)
                this.setPosition(controllerPiece.posX + this.stickX, controllerPiece.posY + this.stickY, controllerPiece.posZ + this.stickZ);
//No motion
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }
        }


//If not reached piece yet
        else
        {
//If piece at gathering phase
            if(controllerPiece.phaseAt.equals("gather"))
            {
//And first tick of piece gathering
                if(controllerPiece.gatherCountdown == controllerPiece.gatherCountdownMax)
                { 
//Set initial vec to piece and offset
                    this.controllerInitialVec
                        = new Vec3d(
                        (controllerPiece.posX + this.stickX) - this.posX, 
                        (controllerPiece.posY + this.stickY) - this.posY, 
                        (controllerPiece.posZ + this.stickZ) - this.posZ)
                        .scale(1.25D / ((double) controllerPiece.gatherCountdownMax));
//Set glue distance
                    this.controllerGlueDistance 
                    = 1.5D * controllerInitialVec.length();
//Set block "not normal"
                    this.setBlockNormal(false);
                }


//In any case, move to piece,
//in a fraction of gather countdown + using piece motion
                this.motionX = (controllerInitialVec.x + controllerPiece.motionX);
                this.motionY = (controllerInitialVec.y + controllerPiece.motionY);
                this.motionZ = (controllerInitialVec.z + controllerPiece.motionZ);


//Check if close enough to piece to glue
                if(this.getDistance
                (controllerPiece.posX + this.stickX, controllerPiece.posY + this.stickY, controllerPiece.posZ + this.stickZ) <= this.controllerGlueDistance)
                {
//If so set glued
                    this.controllerReached = true;
//Follow piece (at offset)
                    this.setPosition(controllerPiece.posX + this.stickX, controllerPiece.posY + this.stickY, controllerPiece.posZ + this.stickZ);
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


    public void expelByPieceSpin()
    {
//Set block expelled
        this.blockExpelled = true;

//Aim direction based on offset
        Vec3d aimDirection = new Vec3d(this.stickX, this.stickY, this.stickZ).normalize();

//Shoot out block
        this.setMovement(aimDirection.x * this.expelForceHorizontal, aimDirection.y * this.expelForceVertical, aimDirection.z * this.expelForceHorizontal,
//Flat-ish gravity and quick horizontal deceleration 
        this.expelGravity, false, this.expelAcceleration);
//Restore block normal behavior
        this.setBlockNormal(true);        
    }



    public void fall(float distance, float damageMultiplier)
    {
//First if controller not null
        if(this.controller != null) 
        {
//And controller is piece, expel
            if(this.controller instanceof EntityEarthPiece) { ((EntityEarthPiece) this.controller).phaseAt = "expel"; }
        }


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
    
}

