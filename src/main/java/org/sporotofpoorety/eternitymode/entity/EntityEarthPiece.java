package org.sporotofpoorety.eternitymode.entity;

import java.util.List;
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


/*
Earth Chunk:
searchpos
string mode (spin)
spintime
spintimemax
hometime
hometimemax
earthchunkshape
earthchunksize
earthchunkorbitdistance
earthchunkorbitspeed


Step 1:
Generate with an initial pos (rounded to int)
Step 2:
Go up the intended size and search down for a solid block for each one (iterate search position)
Step 3:
Generate blocks (noclip, new fields, new NBT, etc.)
Step 4:
Assigning each one to a position around the earth chunk (provide a "glue" position based on each one's search position)
Step 5:
Initial fast motion then adjust motion until reaching intended orbit center
Step 6:
Spin around the orbit center (random? synchronized?)
Step 7:
Wait until max spin time then start slowly homing in
Step 8:
Once homing time hits max, re-solidify, blocks launch and controller disappears (make sure to null check properly)
*/


public class EntityEarthPiece extends EntityWithOwner 
{

    public BlockPos searchBasis = new BlockPos(0, 0, 0);
    public String phaseAt;

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

//Set to mid-block
        BlockPos actualStartPos = BlockUtil.findFirstSolidBlock(this, 32.0F, 32, 2);

        setPosition(((double) actualStartPos.getX()) + 0.5D, ((double) actualStartPos.getY()) + 0.5D, ((double) actualStartPos.getZ()) + 0.5D);



//BlockPos rounded down to int for search basis
        this.searchBasis = new BlockPos((int) x, (int) y, (int) z);
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

        System.out.println("Constructed earth piece");
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


    @Override
    public void onUpdate() 
    {
        super.onUpdate();


//First if no owner
        if (this.owner == null) 
        {
            System.out.println("Premature expel");
//Premature expel
            this.phaseAt = "expel";
//No other logic
            return;
        }


//If this has not searched for blocks yet
        else if(this.phaseAt.equals("search"))
        {
            System.out.println("Checking for block search");
//Search and move onto gather
            this.blockSearch();
            this.phaseAt = "gather";
        }


//Wait for blocks to gather
        else if(this.phaseAt.equals("gather"))
        {
            if(this.gatherCountdown <= 0)
            {
                this.phaseAt = "lift";
            }
            else
            {
                --this.gatherCountdown;
            }
        }

//Lift up
        else if(this.phaseAt.equals("lift"))
        {
            if(this.liftCountdown <= 0)
            {
                this.phaseAt = "position";
            }
            else
            {
                this.posY += this.liftSpeed;
                --this.liftCountdown;
            }
        }


//Set into position
        else if(this.phaseAt.equals("position"))
        {
            if(this.pieceType.equals("spin"))
            {
                this.positionIntoSpin();
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
        }

        else if(this.phaseAt.equals("homing"))
        {
//If homing time over
            if(this.homeTime <= 0)
            {
                this.phaseAt = "flung";
                this.setNoGravity(false);


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
            else
            {
                this.performHoming();
                --this.homeTime;
            }

        }

        else if(this.phaseAt.equals("flung"))
        {

        }

        else if(this.phaseAt.equals("expel"))
        {
            if(this.lingerTime <= 0)
            { this.setDead(); return; } 
            else { --this.lingerTime; this.motionX = 0.0D; this.motionY = 0.0D; this.motionZ = 0.0D; }
        }

    
        this.performBasicMovement();    
    }




//Search for blocks
    public void blockSearch()
    {
//And build appropriate shape

//Cube shape
        if(this.pieceShape == "cube")
        {
            this.blockSearchCube();
        }
    }


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
                        this.searchBasis.getX() + atX, this.searchBasis.getY() + atY, this.searchBasis.getZ() + atZ, 0.0F, 16, 2);


//If origin not null
                    if(blockOrigin != null)
                    {
//Make entity block at origin
                        EntityThrownBlock thrownBlock = new EntityThrownBlock
                        (
                            this.world, this.owner, blockOrigin, blockOrigin.getX() + 0.5D, blockOrigin.getY() + 0.5D, blockOrigin.getZ() + 0.5D, 1.0F
                        );


//Generate block with owner and UUID
                        thrownBlock.owner = this.owner;
                        thrownBlock.ownerUUID = this.owner.getUniqueID();

                        thrownBlock.controller = this;
                        thrownBlock.controllerUUID = this.getUniqueID();

//And non-solid for now
                        thrownBlock.setBlockNormal(false);


//And set block earth piece params
                        thrownBlock.setBlockPiece("cube", (double) atX, (double) atY, (double) atZ,
                        this.blockOutSpeed, this.blockOutSpeed,
                        this.blockOutGravity, 1.0D);


//Spawn block
                        if (!this.world.isRemote) { this.getEntityWorld().spawnEntity(thrownBlock); }
                    }
                }
            }
        }
    }




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
        this.move(MoverType.SELF, ownerInitialVec.x + this.owner.motionX, ownerInitialVec.y + this.owner.motionY, ownerInitialVec.z + this.owner.motionZ);


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


    public void performHoming()
    {
        if(this.owner instanceof EntityLiving)
        {
            EntityLivingBase ownerTarget = ((EntityLiving) this.owner).getAttackTarget();

            if(ownerTarget != null)
            {
                Vec3d targetDir = new Vec3d(ownerTarget.posX - this.posX, ownerTarget.posY - this.posY, ownerTarget.posZ - this.posZ).normalize();

                this.move(MoverType.SELF, targetDir.x * this.homeSpeed, targetDir.y * this.homeSpeed, targetDir.z * this.homeSpeed);
            }
        }
    }

}
