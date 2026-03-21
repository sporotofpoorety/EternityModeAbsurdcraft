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

import org.sporotofpoorety.eternitymode.entity.EntityThrownBlock;
import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;
import org.sporotofpoorety.eternitymode.util.BlockUtil;
import org.sporotofpoorety.eternitymode.util.PuppetEntity;




public class EntityEarthPiece extends EntityWithOwner 
{

    public BlockPos searchBasis = new BlockPos(0, 0, 0);

    public String phaseAt;

    public String pieceType = "spin";
    public String pieceShape = "cube";
    public int pieceSize;


    public int gatherTimer;
    public boolean gatherStarted;

    public int liftTimer;
    public double liftSpeed;

    public int positionTimer;
    public double positionAbove;
    public boolean startedPositioning;
    public Vec3d ownerInitialVec = new Vec3d(0.0D, 0.0D, 0.0D);
    public double ownerGlueDistance;

    public int homeTimer;
    public double homeSpeed;
    
    public int flingTimer;
    public double flingSpeed;
    public double blockOutSpeed;
    public double blockOutGravity;
   

//Spin piece specific
    public int spinTimer;
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

    public EntityEarthPiece(World world, double x, double y, double z, 
    EntityLivingBase owner,
    String pieceType, String pieceShape, int pieceSize,
    int gatherTimer, 
    int liftTimer, double liftSpeed,
    int positionTimer, double positionAbove,
    int homeTimer, double homeSpeed,
    int flingTimer, double flingSpeed, double blockOutSpeed, double blockOutGravity)
    {
        super(world, x, y, z, owner);
        setSize(0.5F, 0.5F);

        this.gravitySpeed = 0.1D;
        this.setNoGravity(true);
        this.noClip = true;


        this.setPosition(x, y, z);

//Starting pos parameter should be
//a bit above owner's feet, then take it and use solid block nearby
        BlockPos attemptedStartPos = BlockUtil.findFirstSolidBlock(this, 8.0F, 32, 2);

//Pos parameters serve as a fallback position
        if(attemptedStartPos == null) { setPosition(x, y, z); } 
        else { setPosition(((double) attemptedStartPos.getX()) + 0.5D, ((double) attemptedStartPos.getY()) + 0.5D, ((double) attemptedStartPos.getZ()) + 0.5D); }


//This position rounded to BlockPos for search basis
        this.searchBasis = new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ);
//Start at search phase
        this.phaseAt = "search";

        this.pieceType = pieceType;
        this.pieceShape = pieceShape;
        this.pieceSize = pieceSize;


        this.gatherTimer = gatherTimer;

        this.liftTimer = liftTimer;
        this.liftSpeed = liftSpeed;

        this.positionTimer = positionTimer;
        this.positionAbove = positionAbove;
        this.startedPositioning = false;

        this.homeTimer = homeTimer;
        this.homeSpeed = homeSpeed;

        this.flingTimer = flingTimer;
        this.flingSpeed = flingSpeed;
        this.blockOutSpeed = blockOutSpeed;
        this.blockOutGravity = blockOutGravity;
    }

//Spin piece specific
    public void setPieceSpin(int spinTimer, double spinDistance, double spinRadian, double spinRadianStep)
    {
        this.spinTimer = spinTimer;
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


        compound.setInteger("GatherTimer", this.gatherTimer);
        compound.setBoolean("GatherStarted", this.gatherStarted);

        compound.setInteger("LiftTimer", this.liftTimer);
        compound.setDouble("LiftSpeed", this.liftSpeed);

        compound.setInteger("PositionTimer", this.positionTimer);
        compound.setDouble("PositionAbove", this.positionAbove);
        compound.setBoolean("StartedPositioning", this.startedPositioning);
        if(this.ownerInitialVec != null)
        {
            compound.setDouble("OwnerInitialVecX", this.ownerInitialVec.x);
            compound.setDouble("OwnerInitialVecY", this.ownerInitialVec.y);
            compound.setDouble("OwnerInitialVecZ", this.ownerInitialVec.z);
        }
        compound.setDouble("OwnerGlueDistance", this.ownerGlueDistance);

        compound.setInteger("HomeTimer", this.homeTimer);
        compound.setDouble("HomeSpeed", this.homeSpeed);

        compound.setInteger("FlingTimer", this.flingTimer);
        compound.setDouble("FlingSpeed", this.flingSpeed);
        compound.setDouble("BlockOutSpeed", this.blockOutSpeed);
        compound.setDouble("BlockOutGravity", this.blockOutGravity);


        compound.setInteger("SpinTimer", this.spinTimer);
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


        if (compound.hasKey("GatherTimer")) { this.gatherTimer = compound.getInteger("GatherTimer"); }
        if (compound.hasKey("GatherStarted")) { this.gatherStarted = compound.getBoolean("GatherStarted"); }

        if (compound.hasKey("LiftTimer")) { this.liftTimer = compound.getInteger("LiftTimer"); }
        if (compound.hasKey("LiftSpeed")) { this.liftSpeed = compound.getDouble("LiftSpeed"); }

        if (compound.hasKey("PositionTimer")) { this.positionTimer = compound.getInteger("PositionTimer"); }
        if (compound.hasKey("PositionAbove")) { this.positionAbove = compound.getDouble("PositionAbove"); }
        if (compound.hasKey("StartedPositioning")) { this.startedPositioning = compound.getBoolean("StartedPositioning"); }
        if (compound.hasKey("OwnerInitialVecX") 
        && compound.hasKey("OwnerInitialVecY") && compound.hasKey("OwnerInitialVecZ")) 
            { this.ownerInitialVec = new Vec3d(compound.getDouble("OwnerInitialVecX"), 
            compound.getDouble("OwnerInitialVecY"), compound.getDouble("OwnerInitialVecZ")); }
        if (compound.hasKey("OwnerGlueDistance")) { this.ownerGlueDistance = compound.getDouble("OwnerGlueDistance"); }

        if (compound.hasKey("HomeTimer")) { this.homeTimer = compound.getInteger("HomeTimer"); }
        if (compound.hasKey("HomeSpeed")) { this.homeSpeed = compound.getDouble("HomeSpeed"); }

        if (compound.hasKey("FlingTimer")) { this.flingTimer = compound.getInteger("FlingTimer"); }
        if (compound.hasKey("FlingSpeed")) { this.flingSpeed = compound.getDouble("FlingSpeed"); }
        if (compound.hasKey("BlockOutSpeed")) { this.blockOutSpeed = compound.getDouble("BlockOutSpeed"); }
        if (compound.hasKey("BlockOutGravity")) { this.blockOutGravity = compound.getDouble("BlockOutGravity"); }


        if (compound.hasKey("SpinTimer")) { this.spinTimer = compound.getInteger("SpinTimer"); }
        if (compound.hasKey("SpinDistance")) { this.spinDistance = compound.getDouble("SpinDistance"); }
        if (compound.hasKey("SpinRadian")) { this.spinRadian = compound.getDouble("SpinRadian"); }
        if (compound.hasKey("SpinRadianStep")) { this.spinRadianStep = compound.getDouble("SpinRadianStep"); }
    }


    @Override
    protected void entityInit() {}




    @Override
    public void onUpdate() 
    {
        super.onUpdate();

        if(this.world.isRemote) { return; }




//If no owner
        if (this.owner == null) 
        {
//          System.out.println("Piece at premature expel");
//Premature expel
            this.phaseAt = "expel";
        }


//If not searched for blocks yet
        else if(this.phaseAt.equals("search"))
        {
//          System.out.println("Piece at search");
//Perform search
            this.blockSearch();
//And move to gather
            this.phaseAt = "gather";
        }


//Wait for blocks to gather
        else if(this.phaseAt.equals("gather"))
        {
//          System.out.println("Piece at gather");
            this.updateGatherPhase();
        }


//Lift up
        else if(this.phaseAt.equals("lift"))
        {
//          System.out.println("Piece at lift");
            this.updateLiftPhase();
        }


//Set into position
        else if(this.phaseAt.equals("position"))
        {
//          System.out.println("Piece at position");
            if(this.pieceType.equals("spin"))
            {
                this.positionIntoSpin();
            }

//If has been positioning too long
            if(--this.positionTimer <= -10)
            {
//Failsafe expel
                this.phaseAt = "expel";                
            }
        }


        else if(this.phaseAt.equals("active"))
        {
//          System.out.println("Piece at active");
//If this is a spinning piece
            if(this.pieceType.equals("spin"))
            {
//Active spin
                this.activeSpin();
            }
        }

        else if(this.phaseAt.equals("homing"))
        {
//          System.out.println("Piece at homing");
            this.updateHomingPhase();
        }

        else if(this.phaseAt.equals("flung"))
        {
//          System.out.println("Piece at flung");
            if(this.collided)
            {
                this.phaseAt = "expel";
            }

            this.performBasicMovementWithPuppets();
        }

        else if(this.phaseAt.equals("expel"))
        {
//          System.out.println("Piece at expel");
            this.expelBlocks();
            this.setDead(); 
            return;  
        }
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
        for(int atX = (-1 * this.pieceSize); atX <= this.pieceSize; atX++)
        {
            for(int atY = (-1 * this.pieceSize); atY <= this.pieceSize; atY++)
            {
                for(int atZ = (-1 * this.pieceSize); atZ <= this.pieceSize; atZ++)
                {
//Iterative cube origin
                    BlockPos blockOrigin = BlockUtil.findFirstSolidBlock(this.world, 
                        this.searchBasis.getX() + atX, this.searchBasis.getY() + atY, this.searchBasis.getZ() + atZ, 16.0F, 16, 2);

//If origin not null
                    if(blockOrigin != null)
                    {
//Make entity block at origin
                        EntityThrownBlock thrownBlock = new EntityThrownBlock
                        (
                            this.world, blockOrigin.getX() + 0.5D, blockOrigin.getY() + 0.5D, blockOrigin.getZ() + 0.5D, 
                            this.owner, this.world.getBlockState(blockOrigin), 
                            false, true, true, 1.0F
                        );

//Give block controller and UUID
                        thrownBlock.controller = this;
                        thrownBlock.controllerUUID = this.getUniqueID();

//Set block not solid for now
                        thrownBlock.setBlockSolid(false);


//New puppet entity
                        PuppetEntity puppetBlock = new PuppetEntity(thrownBlock, 
                        (double) atX, (double) atY, (double) atZ, 0, 0);
//Grant UUID separately
                        puppetBlock.puppetUUID = thrownBlock.getUniqueID();

//Add block to puppet list
                        this.puppetEntities.add(puppetBlock);

//Spawn block, not solid yet
                        if (!this.world.isRemote) 
                        {
                            thrownBlock.setBlockSolid(false); 
                            this.getEntityWorld().spawnEntity(thrownBlock); 
                        }
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


//If gather time over
        if(--this.gatherTimer <= 0)
        {
/*
//Disown blocks that didn't reach correct position
            this.disownMisalignedBlocks())
*/
//Move onto lift
            this.phaseAt = "lift";                
        }
    }

/*
//Check if gather done
    public void disownMisalignedBlocks()
    {
        for(PuppetEntity puppet : this.puppetEntities)
        {

        }

    }
*/

//Block gather logic
    public void gatherBlocksTowardsPosition()
    {
//If first tick of gather
        if(!this.gatherStarted)
        {
//Get each puppet
            for(PuppetEntity puppet : this.puppetEntities)
            { 
//Set puppet vec to point to this + offset
                puppet.storedVec
                    = new Vec3d(
                    (this.posX + puppet.offsetX) - puppet.entity.posX, 
                    (this.posY + puppet.offsetY) - puppet.entity.posY, 
                    (this.posZ + puppet.offsetZ) - puppet.entity.posZ)
                    .scale(1.25D / ((double) this.gatherTimer));
//Set puppet glue distance
                puppet.storedDistance 
                    = 1.5D * puppet.storedVec.length();
            }

//Set gather started
            this.gatherStarted = true;
        }


//Get each puppet
        for(PuppetEntity puppet : this.puppetEntities)
        {
//Pull each in a fraction of gather timer
            puppet.entity.move(MoverType.SELF, puppet.storedVec.x, puppet.storedVec.y, puppet.storedVec.z);


//Check if block close enough to self to glue
            if(puppet.entity.getDistance
            (this.posX + puppet.offsetX, this.posY + puppet.offsetY, this.posZ + puppet.offsetZ) <= puppet.storedDistance)
            {
//If so set glued
                puppet.controlState = 1;
//Follow this (at offset)
                puppet.entity.setPosition(this.posX + puppet.offsetX, this.posY + puppet.offsetY, this.posZ + puppet.offsetZ);
            }
        }  
    }




    public void updateLiftPhase()
    {
//If still has lift left
        if(this.liftTimer > 0)
        {
//Lift
            this.moveWithPuppets(0.0D, this.liftSpeed, 0.0D);
//Lift timer
            --this.liftTimer;
        }

//If no lift time left
        else
        {
//Stop lifting and move to positioning
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
//Set initial vec to owner and offset
            this.ownerInitialVec
                = new Vec3d(
                (this.owner.posX + (Math.cos(this.spinRadian) * this.spinDistance)) - this.posX, 
                (this.owner.posY + this.positionAbove) - this.posY, 
                (this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance)) - this.posZ) 
                .scale(1.25D / ((double) positionTimer));

//Set glue distance
            this.ownerGlueDistance 
            = 1.5D * ownerInitialVec.length();

//Set started positioning
            this.startedPositioning = true;           
        }


//In any case, move to owner,
//in a fraction of positioning time + using owner motion
        this.moveWithPuppets(ownerInitialVec.x + this.owner.motionX, ownerInitialVec.y + this.owner.motionY, ownerInitialVec.z + this.owner.motionZ);


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
                this.owner.posY + this.positionAbove, 
                this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance));
//Attach blocks too
            for(PuppetEntity puppet : this.puppetEntities)
            {
                puppet.entity.setPosition
                (
                    puppet.offsetX + this.owner.posX + (Math.cos(this.spinRadian) * this.spinDistance), 
                    puppet.offsetY + this.owner.posY + this.positionAbove, 
                    puppet.offsetZ + this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance)
                );
            }
        }                  
    }




//Active branch
//Active spin
    public void activeSpin()
    {
        if(this.spinTimer > 0)
        {
//Spin around owner (at offset)
            this.moveWithPuppets
            (
                (this.owner.posX + (Math.cos(this.spinRadian) * this.spinDistance)) - (this.posX), 
                (this.owner.posY + this.positionAbove) - (this.posY), 
                (this.owner.posZ + (Math.sin(this.spinRadian) * this.spinDistance)) - (this.posZ)
            );


//Increment spin radian
            this.spinRadian += this.spinRadianStep;
//Decrement spin time
            --this.spinTimer;
        }
//If spin over
        else
        {
//Start homing
            this.phaseAt = "homing";
        }
    }




//Homing branch
    public void updateHomingPhase()
    {
//If homing time left
        if(this.homeTimer > 0)
        {
//Homing
            this.performHoming();
//Homing timer
            --this.homeTimer;
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
                Vec3d targetDir = new Vec3d(ownerTarget.posX - this.posX, (ownerTarget.posY + ownerTarget.height) - this.posY, ownerTarget.posZ - this.posZ).normalize();

                this.moveWithPuppets(targetDir.x * this.homeSpeed, targetDir.y * this.homeSpeed, targetDir.z * this.homeSpeed); 
            }
        }
    }

//Homing to fling
    public void homingToFling()
    {
//Set flung 
        this.phaseAt = "flung";
//Set collision
        this.noClip = false;
        this.setSize((float) (1 + (2 * this.pieceSize)), (float) (1 + (2 * this.pieceSize)));


//Fling at either target or straight up (failsafe)
        if(this.owner instanceof EntityLiving)
        {
            EntityLivingBase ownerTarget = ((EntityLiving) this.owner).getAttackTarget();

            if(ownerTarget != null)
            {
                Vec3d targetDir = new Vec3d(ownerTarget.posX - this.posX, (ownerTarget.posY + ownerTarget.height) - this.posY, ownerTarget.posZ - this.posZ).normalize();

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
        for(PuppetEntity puppet : this.puppetEntities)
        {
//Aim direction based on offset
            Vec3d aimDirection = new Vec3d(puppet.offsetX, puppet.offsetY, puppet.offsetZ).normalize();

//Shoot out block
            ((EntityWithOwner) puppet.entity).setMovement(aimDirection.x * this.blockOutSpeed, aimDirection.y * this.blockOutSpeed, aimDirection.z * this.blockOutSpeed,
//Flat-ish gravity and quick horizontal deceleration 
            this.blockOutGravity, false, 1.0D);
//Restore block normal behavior
            ((EntityThrownBlock) puppet.entity).setBlockSolid(true);
        } 
    }

}
