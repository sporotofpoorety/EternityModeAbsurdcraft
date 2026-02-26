package org.sporotofpoorety.eternitymode.entity;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbVoid;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import org.sporotofpoorety.eternitymode.entity.EntityThrownBlock;
import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityOrbVoid;
import org.sporotofpoorety.eternitymode.util.EntityUtil;




//Lemme write down how the original 
//logic flow works to try to make sense of it


//Timers:

//ticksExisted
//timeSinceIgnited
//timerDDD


//Lengths:

//StartState/WAITSTART 
//FuseState/FUSE       (Grows during this)
//Hardcoded


//Functions:

//onUpdate else
//onUpdate main + orbDoing() + setSelfeState(1) + dyingBurst(true, 1) (Else branch)
//selfExplode (called from dyingBurst() main branch)



public class EntityOrbVoidCustom extends EntityOrbVoid {


    public IMixinEntityOrbVoid orbVoidMixin;
    public boolean orbFreeMoving = true;


//Owner that doesn't have to be a parasite
    public EntityLiving ownerCustom;


//Orb type
    public String orbCustomType = "none";


//This is necessary to keep or change size with differing timers
    public float growthRate;
    public float deflateRate;

//Orb death timer not hardcoded
    public int orbDeflatesWhen;
    public int orbDiesWhen;


//Shower orb specific
    public double riseSpeed;
    public double riseLimit;

    public int scatterBlockCount;
    public int aimedBlockCount;

    public double blockForceHorizontal;
    public double blockForceVertical;
    public double blockAcceleration;




    public EntityOrbVoidCustom(World worldIn) 
    {
        super(worldIn);

        this.setCustomOrbVoid();

        this.ownerCustom = null;

        this.growthRate = 1.0F;
        this.deflateRate = 1.0F;

        this.orbDeflatesWhen = 80;
        this.orbDiesWhen = 90;
    }

    public EntityOrbVoidCustom(World worldIn, EntityPMalleable in, EntityLiving ownerCustom, int fuse, int waitStart,
    float growthRate, float deflateRate, int orbDeflatesWhen, int orbDiesWhen) 
    {
        super(worldIn);

        this.setFuseState(fuse);
        this.setStartState(waitStart);

        this.setCustomOrbVoid();

        this.ownerCustom = ownerCustom;

        this.growthRate = growthRate;
        this.deflateRate = deflateRate;

        this.orbDeflatesWhen = orbDeflatesWhen;
        this.orbDiesWhen = orbDiesWhen;
    }

    public EntityOrbVoidCustom(World worldIn, EntityPMalleable in, EntityLiving ownerCustom, int fuse, int waitStart, boolean stayPY,
    float growthRate, float deflateRate, int orbDeflatesWhen, int orbDiesWhen) 
    {
        super(worldIn);

        this.setFuseState(fuse);
        this.setStartState(waitStart);

        this.setCustomOrbVoid();

        this.ownerCustom = ownerCustom;

        this.growthRate = growthRate;
        this.deflateRate = deflateRate;

        this.orbDeflatesWhen = orbDeflatesWhen;
        this.orbDiesWhen = orbDiesWhen;
    }

    public void setCustomOrbVoid()
    {
        orbVoidMixin = (IMixinEntityOrbVoid) this;

        orbVoidMixin.setOrbVoidIsAbsurdcraft(true);
    }

    public void setOrbShower(double riseSpeed, double riseLimit,
    int scatterBlockCount, int aimedBlockCount,
    double blockForceHorizontal, double blockForceVertical, double blockAcceleration)
    {
        this.riseSpeed = riseSpeed;
        this.riseLimit = riseLimit;

        this.scatterBlockCount = scatterBlockCount;
        this.aimedBlockCount = aimedBlockCount;

        this.blockForceHorizontal = blockForceHorizontal;
        this.blockForceVertical = blockForceVertical;
        this.blockAcceleration = blockAcceleration;
    }




    public void onUpdate()
    {
        super.onUpdate();


/*
        if(!this.world.isRemote)
        {
*/
//If at max pre-growth ticks
            if(this.ticksExisted == this.getStartState())
            {
//Perform a function for start of growing
                this.whenOrbStartsGrowing();
            }


//If orb is active
            if (this.getSelfeState() == 2 && this.timerDDD <= this.orbDeflatesWhen)
            {
//Perform active function
                this.whenOrbIsActive();
            }


            this.posX += this.motionX; this.posY += this.motionY; this.posZ += this.motionZ;
//          if(this.orbFreeMoving) { this.poosX = this.posX; this.poosY = this.posY; this.poosZ = this.posZ; }
            if(this.orbFreeMoving) { this.orbVoidMixin.setOrbPoos(this.posX, this.posY, this.posZ); }
//        } 
    }





    public void whenOrbStartsGrowing()
    {
        if(this.orbCustomType.equals("blockshower"))
        {
            this.orbGrowingShower();
        }
    }


    public void orbGrowingShower()
    {
//Generate and return 150/100 blocks
//in a random 64 cube, no owner, breaks them conditionally
        ArrayList<EntityThrownBlock> scatterBlocks = EntityUtil.generateAndReturnRandomBlocks(this,
        null, this.scatterBlockCount, 64, 32, 2, 2);
        ArrayList<EntityThrownBlock> aimedBlocks = EntityUtil.generateAndReturnRandomBlocks(this, 
        null, this.aimedBlockCount, 64, 32, 2, 2);


        for(EntityThrownBlock scatterBlock : scatterBlocks)
        {
            scatterBlock.owner = this.ownerCustom;

            scatterBlock.controller = this;
            scatterBlock.controllerUUID = this.getUniqueID();

            scatterBlock.setBlockNormal(false);


            scatterBlock.setBlockShower("shower", "scatter",
            this.blockForceHorizontal, this.blockForceVertical,
            0.04D, this.blockAcceleration);

            scatterBlock.expelRadians = (2.0D * Math.PI) * rand.nextDouble();


            if (!this.world.isRemote) { this.getEntityWorld().spawnEntity(scatterBlock); }

//          System.out.println("Spawned test block at " + scatterBlock.posY);
        }

        for(EntityThrownBlock aimedBlock : aimedBlocks)
        {
            aimedBlock.owner = this.ownerCustom;

            aimedBlock.controller = this;
            aimedBlock.controllerUUID = this.getUniqueID();

            aimedBlock.setBlockNormal(false);


            aimedBlock.setBlockShower("shower", "aimed",
            this.blockForceHorizontal, this.blockForceVertical,
            0.04D, this.blockAcceleration);


            if(!this.world.isRemote) { this.getEntityWorld().spawnEntity(aimedBlock); }

//          System.out.println("Spawned test block at " + aimedBlock.posY);
        }
    }



    public void whenOrbIsActive()
    {
        if(this.orbCustomType.equals("blockshower"))
        {
            this.orbActiveShower();
        }
    }


    public void orbActiveShower()
    {
        if(this.ownerCustom != null)
        {
            EntityLivingBase ownerTarget = ownerCustom.getAttackTarget();
//If owner has target
            if(ownerTarget != null)
            {
//If far above target then explode immediately
                if((this.posY - ownerTarget.posY) > this.riseLimit)
                {
                    this.setSelfeState(2);
                    this.timerDDD = this.orbDeflatesWhen;
                }
//Otherwise
                else
                {
//Ascend fast
                    this.motionY = this.riseSpeed;
                }
            }
        }

//If no valid owner explode immediately
        else
        {
            this.timerDDD = this.orbDeflatesWhen;
        }
    }


    @Override
    protected void dyingBurst(boolean fromDeath, int value) 
    {
//Get "self-exploding" step
        int i = this.getSelfeState();
//Increment fused timer by the step
        this.timeSinceIgnited += i * value;

//And make sure it's not negative
        if (this.timeSinceIgnited < 0) 
        {
            this.timeSinceIgnited = 0;
        }


//If already fully fused
        if (this.timeSinceIgnited >= this.getFuseState()) 
        {
//Don't go over fuse limit
            this.timeSinceIgnited = this.getFuseState();
//Run orb activity + death check
            this.selfExplode();
        } 
//If not fully fused scale it up
        else 
        {
            this.setSize(this.width + (0.8F * this.growthRate), this.height + (0.32F * this.growthRate));
        }
    }




    @Override
    protected void selfExplode() 
    {
        this.setSelfeState(2);


        if (this.getSelfeState() == 2) 
        {
            ++this.timerDDD;
            if (this.timerDDD > this.orbDeflatesWhen) 
            {
                this.setSize(Math.max(0.1F, this.width - (0.8F * this.deflateRate)), 
                             Math.max(0.1F, this.height - (0.32F * this.deflateRate)));
                if (this.world.isRemote) 
                {
                    int par = this.getFuseState();
                    par += par / 2;

                    for(int i = 0; i <= par; ++i) 
                    {
                        this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, this.posY + this.rand.nextDouble() * 2.0D * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), new int[]{0, 0, 0});
                    }
                }

                this.playSound(SRPSounds.ORB_E, 1.0F, 1.0F);
                if (this.timerDDD > orbDiesWhen) 
                {
                    this.setDead();
                }
            }
        }
    }


//Clean out regular behavior
    protected void orbDoingCustom() 
    {

    }
   

    @Override
    public void pullEntity(EntityLivingBase target) 
    {

    }




//Scale flash intensity appropriately to max fuse
    @SideOnly(Side.CLIENT)
    public float getSelfeFlashIntensity(float p_70831_1_) 
    {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_ * 5.0F) 
        / (float) ((float) this.getFuseState() - (float) (2.0F * ((float) this.getFuseState() / 8.0F)));
    }




//New getters
    public int getTimeSinceIgnited()
    {
        return this.timeSinceIgnited;
    }

    public int getTimerDDD()
    {
        return this.timerDDD;
    }

}
