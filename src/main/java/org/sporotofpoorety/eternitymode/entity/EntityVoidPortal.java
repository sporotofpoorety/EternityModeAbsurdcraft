package org.sporotofpoorety.eternitymode.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;




public class EntityVoidPortal extends Entity {


//Needed for render, completely irrelevant to logic
       public float prevRenderYawOffset;
       public float renderYawOffset;
       public float prevRotationYawHead;
       public float rotationYawHead;
       public float prevLimbSwingAmount;
       public float limbSwingAmount;
       public float limbSwing;
       public int deathTime;
       protected int lastActiveTime;
       protected int timeSinceIgnited;




//Lifecycle is
//Delaying -> Growing -> Bursting
    private static final DataParameter<Integer> PHASE =
        EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);

    private static final DataParameter<Integer> INITIAL_GROW_DELAY =
        EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);

    private static final DataParameter<Integer> GROW_DURATION =
        EntityDataManager.createKey(EntityVoidPortal.class, DataSerializers.VARINT);


    private EntityLivingBase owner;
    private boolean shouldFollowOwner;
    private double frozenX, frozenY, frozenZ;


    private int growTicks;
    private int burstTicks;




    public EntityVoidPortal(World world) 
    {
        super(world);

        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;

        this.setSize(0.5F, 0.5F);
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;

        this.setInitialGrowDelay(20);
        this.setGrowDuration(40);
    }


    public EntityVoidPortal(World world, 
    EntityLivingBase owner, boolean shouldFollow, int growDelay, int growTime) 
    {
        this(world);

//Owner
        this.owner = owner;
//Just for rendering
        this.initRendering();


//Whether should follow owner
        this.shouldFollowOwner = shouldFollow;


//Phase durations
        this.setInitialGrowDelay(growDelay);
        this.setGrowDuration(growTime);
    }


    @Override
    protected void entityInit() {
        dataManager.register(PHASE, 0);
        dataManager.register(INITIAL_GROW_DELAY, 20);
        dataManager.register(GROW_DURATION, 40);
    }


    private void initRendering()
    {
        this.prevRenderYawOffset = owner.prevRenderYawOffset;
        this.renderYawOffset = owner.renderYawOffset;
        this.prevRotationYawHead = owner.prevRotationYawHead;
        this.rotationYawHead = owner.rotationYawHead;
        this.prevLimbSwingAmount = owner.prevLimbSwingAmount;
        this.limbSwingAmount = owner.limbSwingAmount;
        this.limbSwing = owner.limbSwing;
    }




    @Override
    public void onUpdate() 
    {
        super.onUpdate();


//Just for render
        this.updateRenderLogic();


//Update position
        this.updatePosition();


//Client side particles
        if (world.isRemote) 
        { this.spawnClientParticles();    return; }


//Lifecycle logic
        this.updateLifecycle();
    }




    @SideOnly(Side.CLIENT)
    public float getSelfeFlashIntensity(float partialTicks) 
    {
        return ((float)this.lastActiveTime +
            (float)(this.timeSinceIgnited - this.lastActiveTime) * partialTicks * 5.0F)
            / (float)(this.getGrowDuration() - 2);
    }




    private void updateRenderLogic()
    {
        if(this.owner != null) 
        { 
            this.prevRenderYawOffset = this.owner.prevRenderYawOffset;
            this.renderYawOffset = this.owner.renderYawOffset;
            this.prevRotationYawHead = this.owner.prevRotationYawHead;
            this.rotationYawHead = this.owner.rotationYawHead;
        }
    }




    private void updatePosition() 
    {
//If owner is valid and should be followed
        if (this.owner != null && this.shouldFollowOwner) 
        {
//And should be followed
            if (this.owner.isEntityAlive()) 
            {
//Follow owner
                this.posX = owner.posX;
                this.posY = owner.posY + owner.height + 0.5D;
                this.posZ = owner.posZ;

//And track last valid pos
                this.frozenX = this.posX;
                this.frozenY = this.posY;
                this.frozenZ = this.posZ;
            }


//If owner not alive set dead
            else 
            {
                this.setDead();
            }
        }
//If shouldn't follow owner, stay still 
        else 
        {
            if(this.ticksExisted > 1)
            {
                this.posX = frozenX;
                this.posY = frozenY;
                this.posZ = frozenZ;
            }
        }
    }




//Meat and potatoes right here
    private void updateLifecycle() 
    {
//Initial delay before growing
        if (this.ticksExisted <= this.getInitialGrowDelay()) { return; }


//Based on phase
        switch (getPhase()) 
        {
//Begin growing
            case 0:
                this.beginGrowing();
                break;
//Execute growing
            case 1:
                this.tickGrowing();
                break;
//Or execute bursting
            case 2:
                this.tickBursting();
                break;
        }
    }




//Begin growing
    private void beginGrowing() 
    {
//Set to growing phase
        this.setPhase(1);
    }


    private void tickGrowing() 
    {
//Increment grow ticks
        this.growTicks++;

//Visual growth
        this.setSize(width + 0.6F, height + 0.24F);


//If growth duration over
        if (this.growTicks >= this.getGrowDuration()) 
        {
//Then BURST
            this.beginBurst();
        }
    }




    private void beginBurst() 
    {
//Set to bursting phase
        this.setPhase(2);


//Initial burst logic
        this.onPortalOpened();
    }


    private void tickBursting() 
    {
//Increment bursting ticks
        this.burstTicks++;


//If burst at over 20 ticks
        if (this.burstTicks > 20) 
        {
//Shrink a lot
            this.setSize
            (
                Math.max(0.1F, this.width - 0.6F),
                Math.max(0.1F, this.height - 0.24F)
            );
        }


//At 30 ticks set dead
        if (burstTicks > 30) {
            this.setDead();
        }
    }




    protected void onPortalOpened() 
    {

    }




    @SideOnly(Side.CLIENT)
    private void spawnClientParticles() 
    {

    }




//Data getters
    public int getPhase() { return dataManager.get(PHASE); }
    public int getInitialGrowDelay() { return dataManager.get(INITIAL_GROW_DELAY); }
    public int getGrowDuration() { return dataManager.get(GROW_DURATION); }

//Data setters
    public void setPhase(int phase) { dataManager.set(PHASE, phase); }
    public void setInitialGrowDelay(int t) { dataManager.set(INITIAL_GROW_DELAY, t); }
    public void setGrowDuration(int ticks) { dataManager.set(GROW_DURATION, ticks); }




//No AABB
    @Override
    public AxisAlignedBB getCollisionBoundingBox() 
    {
        return Block.NULL_AABB;
    }


//No collision
    @Override
    public void applyEntityCollision(Entity e) {}




//NBT WIP
    @Override protected void writeEntityToNBT(NBTTagCompound compound) 
    {
    
    }


    @Override protected void readEntityFromNBT(NBTTagCompound compound) 
    {
    
    }
}
