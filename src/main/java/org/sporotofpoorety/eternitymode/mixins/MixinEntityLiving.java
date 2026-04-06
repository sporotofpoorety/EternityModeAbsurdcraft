package org.sporotofpoorety.eternitymode.mixins;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


import javax.annotation.Nullable;


import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLiving;
import org.sporotofpoorety.eternitymode.util.QueuedActionAtPos;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;


import org.sporotofpoorety.eternitymode.client.particles.ParticleSpiral; 



//Mixin this class
@Mixin(value = EntityLiving.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityLiving implements IMixinEntityLiving
{

//Named this way for compatibility
    @Unique
    private static final DataParameter<Boolean> IS_ABSURDCRAFT_STUNNED = EntityDataManager.<Boolean>createKey(EntityLiving.class, DataSerializers.BOOLEAN);
//Named this way for compatibility
    @Unique private int absurdcraftStunnedTimer;

//Common architecture for scheduled actions
    @Unique
    private final ConcurrentLinkedQueue<QueuedActionAtPos> queuedActionsAtPos = new ConcurrentLinkedQueue<>();




    @Inject
    (
//Inject in this method
        method = "onUpdate",
//At tail (low priority after all)
        at = @At("TAIL")
    )
    private void onUpdateStunTimer(CallbackInfo callInfo)
    {
        if(this.absurdcraftStunnedTimer > 0)
        {
            Entity selfEntity = (Entity) (Object) this;

            if((selfEntity.ticksExisted % 9) == 0)
            {
                for (int angleStepAt = 0; angleStepAt < 9; angleStepAt++) 
                {
                    Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSpiral(selfEntity.world, 9,
                    selfEntity.posX, selfEntity.posY + (double) (selfEntity.height * 1.15F), selfEntity.posZ, selfEntity.posX, selfEntity.posZ, 
                    65, 40, angleStepAt, (double) (selfEntity.width * 1.15F), 0.0D));
                }
            }

            
            if(--absurdcraftStunnedTimer <= 0)
            {
                this.setAbsurdcraftStunned(false);
            }
        }
    }


    @Inject
    (
        method = "onUpdate",
        at = @At("TAIL")
    )
//Queued action processing logic
    private void updateQueuedActions(CallbackInfo callInfo)
    {
//If action queue has actions
        if (!this.queuedActionsAtPos.isEmpty())
        {
//Iterate over them
            this.queuedActionsIterate();
        }
//If action queue is empty
        else
        {
//Empty queue logic
            this.queuedActionsEmptyLogic();
        }
    }


    public void queuedActionsIterate()
    {
        Entity selfEntity = (Entity) (Object) this; 

//Iterator for them
        Iterator<QueuedActionAtPos> iter = this.queuedActionsAtPos.iterator();


//While still having queued actions
        while (iter.hasNext()) 
        {
//Get next queued action and increment iterator there
            QueuedActionAtPos queuedAction = iter.next();

//If action time reached
            if(selfEntity.world.getTotalWorldTime() >= queuedAction.actionTick) 
            {
//Execute action
                this.queuedActionExecute(queuedAction);
//Then remove it from queue
                iter.remove();
            }

//If time not reached
            else
            {
//Before action time logic
                this.queuedActionBefore(queuedAction);
            }
        }
    }

    
    public void queuedActionBefore(QueuedActionAtPos queuedAction)
    {

    }


    public void queuedActionExecute(QueuedActionAtPos queuedAction)
    {

    }


    public void queuedActionsEmptyLogic()
    {

    }




    @WrapWithCondition
    (
        method = "despawnEntity",
        at = 
        @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLiving;setDead()V"
        ),
//Slice
        slice = @Slice(
//From this reference point
            from = 
            @At
            (
                value = "CONSTANT",
                args = "doubleValue=16384.0D"
            ),
//To another, later reference point
            to = 
            @At
            (
                value = "CONSTANT",
                args = "doubleValue=1024.0D"
            )
        )
    )
    private boolean flyingHelperLiving(EntityLiving self) 
    {
        return false;
    }



    @Inject
    (
        method = "entityInit",
        at = @At("TAIL")
    )
//On entity init
    private void entityInitNewDataParameter(CallbackInfo callInfo)
    {
        Entity selfEntity = (Entity) (Object) this;
//Register the new data parameter
        selfEntity.getDataManager().register(IS_ABSURDCRAFT_STUNNED, Boolean.valueOf(false));         
    }


    @Inject
    (
        method = "writeEntityToNBT",
        at = @At("TAIL"),
        require = 1
    )
    private void writeNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
//New NBT below
        compound.setBoolean("AbsurdcraftStunned", this.getAbsurdcraftStunned());
        compound.setInteger("AbsurdcraftStunnedTimer", this.getAbsurdcraftStunnedTimer());

        this.nbtWriteQueuedActions(compound);
    }


    @Inject
    (
        method = "readEntityFromNBT",
        at = @At("TAIL"),
        require = 1
    )
    private void readNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
//New NBT below
        if (compound.hasKey("AbsurdcraftStunned")) { this.setAbsurdcraftStunned(compound.getBoolean("AbsurdcraftStunned"));}
        if (compound.hasKey("AbsurdcraftStunnedTimer")) { this.setAbsurdcraftStunnedTimer(compound.getInteger("AbsurdcraftStunnedTimer")); }

        this.nbtReadQueuedActions(compound);
    }




//Write queued actions to NBT
    public void nbtWriteQueuedActions(NBTTagCompound compound)
    {
//Action array to store
        NBTTagList actionListToStore = new NBTTagList();

//For each queued action   
        for (QueuedActionAtPos queuedAction : this.queuedActionsAtPos) 
        {
//Make action map
            NBTTagCompound actionToStore = new NBTTagCompound();
                actionToStore.setDouble("QueuedActionX", queuedAction.actionX);
                actionToStore.setDouble("QueuedActionY", queuedAction.actionY);
                actionToStore.setDouble("QueuedActionZ", queuedAction.actionZ);
                actionToStore.setLong("QueuedActionTick", queuedAction.actionTick);
                actionToStore.setInteger("QueuedActionType", queuedAction.actionType);
//Append it to action array
            actionListToStore.appendTag(actionToStore);
        }
        
        compound.setTag("QueuedActionArray", actionListToStore);   
    }


//Read queued actions from NBT
    public void nbtReadQueuedActions(NBTTagCompound compound)
    {
//First clear action queue
        this.queuedActionsAtPos.clear();

//Check for action array
        if (compound.hasKey("QueuedActionArray")) 
        {
//It's an array of maps specifically
            NBTTagList storedActionList = compound.getTagList("QueuedActionArray", 10);
            
//For each stored action
            for (int i = 0; i < storedActionList.tagCount(); i++) 
            {
//Fetch it as compound
                NBTTagCompound storedAction = storedActionList.getCompoundTagAt(i);

//Make corresponding queued action
                QueuedActionAtPos action = new QueuedActionAtPos
                (
                    storedAction.getDouble("QueuedActionX"),
                    storedAction.getDouble("QueuedActionY"),
                    storedAction.getDouble("QueuedActionZ"),
                    storedAction.getLong("QueuedActionTick"),
                    storedAction.getInteger("QueuedActionType")
                );

//Store in the action queue
                this.queuedActionsAtPos.add(action);
            }
        }
    }




//New getters


//Named this way for compatibility
    public boolean getAbsurdcraftStunned()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Boolean)selfEntity.getDataManager().get(IS_ABSURDCRAFT_STUNNED)).booleanValue();
    }

    public int getAbsurdcraftStunnedTimer()
    {
        return this.absurdcraftStunnedTimer;
    }

//Get queued actions
    public ConcurrentLinkedQueue<QueuedActionAtPos> getQueuedActions()
    {
        return this.queuedActionsAtPos;
    }


//New setters


    public void setAbsurdcraftStunned(boolean isStunned)
    {
        Entity selfEntity = (Entity) (Object) this;
        selfEntity.getDataManager().set(IS_ABSURDCRAFT_STUNNED, Boolean.valueOf(isStunned)); 
    }

    public void setAbsurdcraftStunnedTimer(int time)
    {
        this.absurdcraftStunnedTimer = time;
    }

//Add queued action
    public void addQueuedAction(QueuedActionAtPos queuedAction)
    {
        this.queuedActionsAtPos.add(queuedAction);
    }

}
