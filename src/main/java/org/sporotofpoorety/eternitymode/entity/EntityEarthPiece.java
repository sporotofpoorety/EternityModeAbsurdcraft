package org.sporotofpoorety.eternitymode.entity;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.client.particles.ParticleSpiral;
import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;
import org.sporotofpoorety.eternitymode.util.BlockUtil;




public class EntityEarthPiece extends EntityWithOwner 
{

    public BlockPos searchBasis = new BlockPos(0, 0, 0);
    public String phaseAt;

    public ArrayList<EntityThrownBlock> controlledBlocks = new ArrayList<>();

    public String pieceType = "spin";
    public String pieceShape = "cube";
    public int pieceSize;

    public int gatherCountdown;
    public int gatherCountdownMax;

    public int liftCountdown;
    public int liftCountdownMax;
    public double liftSpeed;

    public boolean startedPositioning;
    public int positionTime;
    public double positionAbove;
    public Vec3d ownerInitialVec = new Vec3d(0.0D, 0.0D, 0.0D);
    public double ownerGlueDistance;

    public int homeTime;
    public int homeTimemax;
    public double homeSpeed;
    
    public double flingSpeed;
    public double blockOutSpeed;
    public double blockOutGravity;

    public int lingerTime;
   

//Spin piece specific
    public int spinTime;
    public int spinTimemax;
    public double spinDistance;
    public double spinRadian;
    public double spinRadianStep;
    

    

    public EntityEarthPiece(World world) 
    {
        super(world);
        setSize(0.5F, 0.5F);

        this.gravitySpeed = 0.1D;
        this.setNoGravity(true);
        this.noClip = true;
    }

    public EntityEarthPiece(World world, EntityLivingBase owner, double x, double y, double z, 
    String pieceType, String pieceShape, int pieceSize,
    int gatherCountdownMax, int liftCountdownMax, double liftSpeed,
    int positionTime, double positionAbove,
    int homeTimemax, double homeSpeed,
    double flingSpeed, double blockOutSpeed, double blockOutGravity,
    int lingerTime)
    {
        super(world, owner);
        setSize(0.5F, 0.5F);

        this.gravitySpeed = 0.1D;
        this.setNoGravity(true);
        this.noClip = true;


        this.setPosition(x, y, z);

//Starting pos parameter should be
//a bit above owner's feet, then take it and use solid block nearby
        BlockPos attemptedStartPos = BlockUtil.findFirstSolidBlock(this, 8.0F, 32, 2);

//Parameters serve as a fallback position
        if(attemptedStartPos == null) { setPosition(x, y, z); } 
        else { setPosition(((double) attemptedStartPos.getX()) + 0.5D, ((double) attemptedStartPos.getY()) + 0.5D, ((double) attemptedStartPos.getZ()) + 0.5D); }


//This position rounded to BlockPos for search basis
        this.searchBasis = new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ);
//Start at search phase
        this.phaseAt = "search";

        this.pieceType = pieceType;
        this.pieceShape = pieceShape;
        this.pieceSize = pieceSize;

        this.gatherCountdown = gatherCountdownMax;
        this.gatherCountdownMax = gatherCountdownMax;

        this.liftCountdown = liftCountdownMax;
        this.liftCountdownMax = liftCountdownMax;
        this.liftSpeed = liftSpeed;

        this.startedPositioning = false;
        this.positionTime = positionTime;
        this.positionAbove = positionAbove;

        this.homeTime = homeTimemax;
        this.homeTimemax = homeTimemax;
        this.homeSpeed = homeSpeed;

        this.flingSpeed = flingSpeed;
        this.blockOutSpeed = blockOutSpeed;
        this.blockOutGravity = blockOutGravity;

        this.lingerTime = lingerTime;

/*
        System.out.println("Constructed earth piece");
        System.out.println("After constructor, owner is: " + (this.owner == null ? "NULL" : this.owner.getName()));
        if(this.owner == null)
        {
            System.out.println("posX is " + this.posX + ", posY is " + this.posY + ", posZ is " + this.posZ);
        }
        else
        {
            System.out.println("(OWNER RELATIVE) posX is " + (this.owner.posX - this.posX) + ", posY is " + this.posY + ", posZ is " + this.posZ);
        }
*/
    }

//Spin piece specific
    public void setPieceSpin(int spinTimemax, double spinDistance, double spinRadian, double spinRadianStep)
    {
        this.spinTime = spinTimemax;
        this.spinTimemax = spinTimemax;
        this.spinDistance = spinDistance;
        this.spinRadian = spinRadian;
        this.spinRadianStep = spinRadianStep;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);


		compound.setInteger("SearchPosX", this.searchBasis.getX());
		compound.setInteger("SearchPosY", this.searchBasis.getY());
		compound.setInteger("SearchPosZ", this.searchBasis.getZ());
        compound.setString("PhaseAt", this.phaseAt);

        compound.setString("PieceType", this.pieceType);
        compound.setString("PieceShape", this.pieceShape);
        compound.setInteger("PieceSize", this.pieceSize);

        compound.setInteger("GatherCountdown", this.gatherCountdown);
        compound.setInteger("GatherCountdownMax", this.gatherCountdownMax);

        compound.setInteger("LiftCountdown", this.liftCountdown);
        compound.setInteger("LiftCountdownMax", this.liftCountdownMax);
        compound.setDouble("LiftSpeed", this.liftSpeed);

        compound.setBoolean("StartedPositioning", this.startedPositioning);
        compound.setInteger("PositionTime", this.positionTime);
        compound.setDouble("PositionAbove", this.positionAbove);
        if(this.ownerInitialVec != null)
        {
            compound.setDouble("OwnerInitialVecX", this.ownerInitialVec.x);
            compound.setDouble("OwnerInitialVecY", this.ownerInitialVec.y);
            compound.setDouble("OwnerInitialVecZ", this.ownerInitialVec.z);
        }
        compound.setDouble("OwnerGlueDistance", this.ownerGlueDistance);

        compound.setInteger("HomeTime", this.homeTime);
        compound.setInteger("HomeTimemax", this.homeTimemax);
        compound.setDouble("HomeSpeed", this.homeSpeed);

        compound.setDouble("FlingSpeed", this.flingSpeed);
        compound.setDouble("BlockOutSpeed", this.blockOutSpeed);
        compound.setDouble("BlockOutGravity", this.blockOutGravity);

        compound.setInteger("LingerTime", this.lingerTime);


        compound.setInteger("SpinTime", this.spinTime);
        compound.setInteger("SpinTimemax", this.spinTimemax);
        compound.setDouble("SpinDistance", this.spinDistance);
        compound.setDouble("SpinRadian", this.spinRadian);
        compound.setDouble("SpinRadianStep", this.spinRadianStep);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);


        if (compound.hasKey("SearchPosX") && compound.hasKey("SearchPosY") && compound.hasKey("SearchPosZ"))
        {
            int X = 0;
            int Y = 0;
            int Z = 0;

	        X = compound.getInteger("SearchPosX");
	        Y = compound.getInteger("SearchPosY");
	        Z = compound.getInteger("SearchPosZ");

            this.searchBasis = new BlockPos((int) X, (int) Y, (int) Z);
        } else { this.searchBasis = new BlockPos(0, 0, 0); }
        if (compound.hasKey("PhaseAt")) { this.phaseAt = compound.getString("PhaseAt"); }

        if (compound.hasKey("PieceType")) { this.pieceType = compound.getString("PieceType"); }
        if (compound.hasKey("PieceShape")) { this.pieceShape = compound.getString("PieceShape"); }
        if (compound.hasKey("PieceSize")) { this.pieceSize = compound.getInteger("PieceSize"); }

        if (compound.hasKey("GatherCountdown")) { this.gatherCountdown = compound.getInteger("GatherCountdown"); }
        if (compound.hasKey("GatherCountdownMax")) { this.gatherCountdownMax = compound.getInteger("GatherCountdownMax"); }

        if (compound.hasKey("LiftCountdown")) { this.liftCountdown = compound.getInteger("LiftCountdown"); }
        if (compound.hasKey("LiftCountdownMax")) { this.liftCountdownMax = compound.getInteger("LiftCountdownMax"); }
        if (compound.hasKey("LiftSpeed")) { this.liftSpeed = compound.getDouble("LiftSpeed"); }

        if (compound.hasKey("StartedPositioning")) { this.startedPositioning = compound.getBoolean("StartedPositioning"); }
        if (compound.hasKey("PositionTime")) { this.positionTime = compound.getInteger("PositionTime"); }
        if (compound.hasKey("PositionAbove")) { this.positionAbove = compound.getDouble("PositionAbove"); }
        if (compound.hasKey("OwnerInitialVecX") 
        && compound.hasKey("OwnerInitialVecY") && compound.hasKey("OwnerInitialVecZ")) 
            { this.ownerInitialVec = new Vec3d(compound.getDouble("OwnerInitialVecX"), 
            compound.getDouble("OwnerInitialVecY"), compound.getDouble("OwnerInitialVecZ")); }
        if (compound.hasKey("OwnerGlueDistance")) { this.ownerGlueDistance = compound.getDouble("OwnerGlueDistance"); }

        if (compound.hasKey("HomeTime")) { this.homeTime = compound.getInteger("HomeTime"); }
        if (compound.hasKey("HomeTimemax")) { this.homeTimemax = compound.getInteger("HomeTimemax"); }
        if (compound.hasKey("HomeSpeed")) { this.homeSpeed = compound.getDouble("HomeSpeed"); }

        if (compound.hasKey("FlingSpeed")) { this.flingSpeed = compound.getDouble("FlingSpeed"); }
        if (compound.hasKey("BlockOutSpeed")) { this.blockOutSpeed = compound.getDouble("BlockOutSpeed"); }
        if (compound.hasKey("BlockOutGravity")) { this.blockOutGravity = compound.getDouble("BlockOutGravity"); }

        if (compound.hasKey("LingerTime")) { this.lingerTime = compound.getInteger("LingerTime"); }


        if (compound.hasKey("SpinTime")) { this.spinTime = compound.getInteger("SpinTime"); }
        if (compound.hasKey("SpinTimemax")) { this.spinTimemax = compound.getInteger("SpinTimemax"); }
        if (compound.hasKey("SpinDistance")) { this.spinDistance = compound.getDouble("SpinDistance"); }
        if (compound.hasKey("SpinRadian")) { this.spinRadian = compound.getDouble("SpinRadian"); }
        if (compound.hasKey("SpinRadianStep")) { this.spinRadianStep = compound.getDouble("SpinRadianStep"); }
    }


    @Override
    protected void entityInit() {}


    public void cleanDeadBlocks()
    {
//Iterator of thrown blocks
        Iterator<EntityThrownBlock> iterBlocks = this.controlledBlocks.iterator();

//While iterator has next block
        while(iterBlocks.hasNext())
        {
//Get next block
            EntityThrownBlock block = iterBlocks.next();

//If null or dead
            if(block == null || block.isDead)
            {
//Remove it (thread-safe)
                iterBlocks.remove();
            }
        }
    }


    @Override
    public void onUpdate() 
    {
        super.onUpdate();

        if(this.world.isRemote) { return; }


//Before anything else, clean dead blocks
        this.cleanDeadBlocks();


//Also, if no owner
        if (this.owner == null) 
        {
            System.out.println("Premature expel at " + this.ticksExisted);
//Premature expel
            this.phaseAt = "expel";
        }


//If not searched for blocks yet
        else if(this.phaseAt.equals("search"))
        {
            System.out.println("Checking for block search");
//Perform search
            this.blockSearch();
//And move to gather
            this.phaseAt = "gather";
        }


//Wait for blocks to gather
        else if(this.phaseAt.equals("gather"))
        {
            this.updateGatherPhase();
        }


//Lift up
        else if(this.phaseAt.equals("lift"))
        {
            this.updateLiftPhase();

//Make blocks follow
            for(EntityThrownBlock block : this.controlledBlocks)
            {
                block.setPosition(this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ);
            } 
        }


//Set into position
        else if(this.phaseAt.equals("position"))
        {
            if(this.pieceType.equals("spin"))
            {
                this.positionIntoSpin();
            }

//Make blocks follow
            for(EntityThrownBlock block : this.controlledBlocks)
            {
                block.setPosition(this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ);
            } 
        }


        else if(this.phaseAt.equals("active"))
        {
//If this is a spinning piece
            if(this.pieceType.equals("spin"))
            {
//Active spin
                this.activeSpin();
            }

//Make blocks follow
            for(EntityThrownBlock block : this.controlledBlocks)
            {
                block.setPosition(this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ);
            } 
        }

        else if(this.phaseAt.equals("homing"))
        {
            this.updateHomingPhase();

//Make blocks follow
            for(EntityThrownBlock block : this.controlledBlocks)
            {
                block.setPosition(this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ);
            } 
        }

        else if(this.phaseAt.equals("flung"))
        {
//Make blocks follow
            for(EntityThrownBlock block : this.controlledBlocks)
            {
                block.setPosition(this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ);

//If any block collides this enters expel
                if(block.collided)
                {
                    this.phaseAt = "expel";
                }
            } 
        }

        else if(this.phaseAt.equals("expel"))
        {
                this.expelBlocks();
                this.setDead(); 
                return;  
        }

    
        this.performBasicMovement();    
    }




//Search branch
    public void blockSearch()
    {
//And build appropriate shape

//Cube shape
        if(this.pieceShape == "cube")
        {
            this.blockSearchCube();
        }
    }

//Search cube
    public void blockSearchCube()
    {
        System.out.println("Reached block search");

        for(int atX = (-1 * this.pieceSize); atX <= this.pieceSize; atX++)
        {
            for(int atY = (-1 * this.pieceSize); atY <= this.pieceSize; atY++)
            {
                for(int atZ = (-1 * this.pieceSize); atZ <= this.pieceSize; atZ++)
                {
//Iterative cube origins
                    BlockPos blockOrigin = BlockUtil.findFirstSolidBlock(this.world, 
                        this.searchBasis.getX() + atX, this.searchBasis.getY() + atY, this.searchBasis.getZ() + atZ, 16.0F, 16, 2);

//If origin not null
                    if(blockOrigin != null)
                    {
//Make entity block at origin
                        EntityThrownBlock thrownBlock = new EntityThrownBlock
                        (
                            this.world, this.owner, blockOrigin, blockOrigin.getX() + 0.5D, blockOrigin.getY() + 0.5D, blockOrigin.getZ() + 0.5D, 1.0F
                        );

//Give block owner and UUID
                        thrownBlock.owner = this.owner;
                        thrownBlock.ownerUUID = this.owner.getUniqueID();

                        thrownBlock.controller = this;
                        thrownBlock.controllerUUID = this.getUniqueID();

//Set block no clip and no gravity for now
                        thrownBlock.setBlockNormal(false);


//And set block earth piece params
                        thrownBlock.setBlockPiece((double) atX, (double) atY, (double) atZ);

//Add block to this list
                        this.controlledBlocks.add(thrownBlock);
//Spawn block
                        if (!this.world.isRemote) { this.getEntityWorld().spawnEntity(thrownBlock); }
                    }
                }
            }
        }
    }




//Gather branch
    public void updateGatherPhase()
    {
//Whatever the case, move blocks closer (Note to self: This originally had an ordering issue)
        this.gatherBlocksTowardsPosition();


//If still in gather time
        if(this.gatherCountdown > 0)
        {
//Decrement gather timer
            --this.gatherCountdown;
        }

//If gather time over
        else
        {
//If all blocks reached position
            if(this.allBlocksReachedGatherPosition())
            {
//Move onto lift
                this.phaseAt = "lift";                
            }
//Else wait a bit and try again
            else
            {
                this.gatherCountdown = 5; 
            }
        }
    }

//Gather done check
    public boolean allBlocksReachedGatherPosition()
    {
        for(EntityThrownBlock block : this.controlledBlocks)
        {
            if(!block.controllerReached)
            {
                return false;
            }
        }

        return true;
    }

//Block gather logic
    public void gatherBlocksTowardsPosition()
    {
//For each block
        for(EntityThrownBlock block : this.controlledBlocks)
        {
//If this first tick of gathering
            if(this.gatherCountdown == this.gatherCountdownMax)
            { 
//Set block vec to point to this + offset
                block.controllerInitialVec
                    = new Vec3d(
                    (this.posX + block.stickX) - block.posX, 
                    (this.posY + block.stickY) - block.posY, 
                    (this.posZ + block.stickZ) - block.posZ)
                    .scale(1.25D / ((double) this.gatherCountdownMax));
//Set block glue distance
                block.controllerGlueDistance 
                = 1.5D * block.controllerInitialVec.length();
//Set block "not normal"
                block.setBlockNormal(false);
            }


//In any case, pull block
//in a fraction of gather countdown
            block.motionX = (block.controllerInitialVec.x + this.motionX);
            block.motionY = (block.controllerInitialVec.y + this.motionY);
            block.motionZ = (block.controllerInitialVec.z + this.motionZ);


//Check if block close enough to self to glue
            if(block.getDistance
            (this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ) <= block.controllerGlueDistance)
            {
//If so set glued
                block.controllerReached = true;
//Follow this (at offset)
                block.setPosition(this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ);
//No motion for block
                block.motionX = 0.0D;
                block.motionY = 0.0D;
                block.motionZ = 0.0D;
            }
        }  
    }




    public void updateLiftPhase()
    {
//If still lift left
        if(this.liftCountdown > 0)
        {
//Lift
            this.motionY = this.liftSpeed;
//Control blocks
            for(EntityThrownBlock block : this.controlledBlocks)
            {
                block.setPosition(this.posX + block.stickX, this.posY + block.stickY, this.posZ + block.stickZ);
            }
//Lift timer
            --this.liftCountdown;
        }

//If no lift time left
        else
        {
//Stop lifting and move to positioning
            this.motionY = 0.0D;
            this.phaseAt = "position";
        }
    }




//Position branch
//Position into spin
    public void positionIntoSpin()
    {
//If first tick of positioning
        if(!this.startedPositioning)
        {
            System.out.println("Reached first tick of position into spin");

 
//Set initial vec to owner and offset
            this.ownerInitialVec
                = new Vec3d(
                (this.owner.posX + (Math.cos(this.spinRadian) * this.spinDistance)) - this.posX, 
                (this.owner.posY + this.positionAbove) - this.posY, 
                (this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance)) - this.posZ) 
                .scale(1.25D / ((double) positionTime));

//Set glue distance
            this.ownerGlueDistance 
            = 1.5D * ownerInitialVec.length();

//Set started positioning
            this.startedPositioning = true;           
        }


//In any case, move to owner,
//in a fraction of positioning time + using owner motion
        this.motionX = ownerInitialVec.x + this.owner.motionX; 
        this.motionY = ownerInitialVec.y + this.owner.motionY;  
        this.motionZ = ownerInitialVec.z + this.owner.motionZ;


//Check if close enough to owner to glue
        if(this.getDistance
        (this.owner.posX + (Math.cos(this.spinRadian) * this.spinDistance), 
        this.owner.posY + this.positionAbove, 
        this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance)) <= this.ownerGlueDistance)
        {
//If so set active
            this.phaseAt = "active";
//And attach to owner (at offset)
            this.setPosition
            (this.owner.posX + (Math.cos(this.spinRadian) * this.spinDistance), 
            this.owner.posY + this.positionAbove, this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance));
//Increment spin radian
            this.spinRadian += this.spinRadianStep;
        }                  
    }




//Active branch
//Active spin
    public void activeSpin()
    {
        if(this.spinTime > 0)
        {
//Spin around owner (at offset)
            this.setPosition
            (this.owner.posX + (Math.cos(this.spinRadian) * this.spinDistance), 
            this.owner.posY + this.positionAbove, this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance));
//Increment spin radian
            this.spinRadian += this.spinRadianStep;
//Decrement spin time
            --this.spinTime;
        }
        else
        {
            System.out.println("Reached last tick of active spin");

            this.phaseAt = "homing";
        }
    }




//Homing branch
    public void updateHomingPhase()
    {
//If homing time left
        if(this.homeTime > 0)
        {
//Homing
            this.performHoming();
//Homing timer
            --this.homeTime;
        }

//If homing over
        else
        {
            this.homingToFling();
        }
    }

//Homing logic
    public void performHoming()
    {
        if(this.owner instanceof EntityLiving)
        {
            EntityLivingBase ownerTarget = ((EntityLiving) this.owner).getAttackTarget();

            if(ownerTarget != null)
            {
                Vec3d targetDir = new Vec3d(ownerTarget.posX - this.posX, ownerTarget.posY - this.posY, ownerTarget.posZ - this.posZ).normalize();

                this.motionX = targetDir.x * this.homeSpeed; 
                this.motionY = targetDir.y * this.homeSpeed; 
                this.motionZ = targetDir.z * this.homeSpeed;
            }
        }
    }

//Homing to fling
    public void homingToFling()
    {
//Set flung and has gravity
        this.phaseAt = "flung";

//Fling at either target or straight up
        if(this.owner instanceof EntityLiving)
        {
            EntityLivingBase ownerTarget = ((EntityLiving) this.owner).getAttackTarget();

            if(ownerTarget != null)
            {
                Vec3d targetDir = new Vec3d(ownerTarget.posX - this.posX, ownerTarget.posY - this.posY, ownerTarget.posZ - this.posZ).normalize();

                this.motionX = targetDir.x * this.flingSpeed; 
                this.motionY = targetDir.y * this.flingSpeed; 
                this.motionZ = targetDir.z * this.flingSpeed;
            }
            else
            {
                this.motionY = this.flingSpeed * 2.0D;
            }
        }
        else
        {
            this.motionY = this.flingSpeed * 2.0D;
        }
    }




//Expel branch
    public void expelBlocks()
    {
        for(EntityThrownBlock block : this.controlledBlocks)
        {
//Set block expelled
            block.blockExpelled = true;

//Aim direction based on offset
            Vec3d aimDirection = new Vec3d(block.stickX, block.stickY, block.stickZ).normalize();

//Shoot out block
            block.setMovement(aimDirection.x * this.blockOutSpeed, aimDirection.y * this.blockOutSpeed, aimDirection.z * this.blockOutSpeed,
//Flat-ish gravity and quick horizontal deceleration 
            this.blockOutGravity, false, 1.0D);
//Restore block normal behavior
            block.setBlockNormal(true);
        } 
    }
}
