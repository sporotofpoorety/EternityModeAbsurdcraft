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
import net.minecraft.util.SoundCategory;


import org.sporotofpoorety.eternitymode.client.particles.ParticleSpiral; 
import org.sporotofpoorety.eternitymode.core.EternityModeSoundEvents;



//Mixin this class
@Mixin(value = EntityLiving.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityLiving implements IMixinEntityLiving
{

//Named this way for compatibility
    @Unique
    private static final DataParameter<Boolean> IS_ABSURDCRAFT_STUNNED = EntityDataManager.<Boolean>createKey(EntityLiving.class, DataSerializers.BOOLEAN);
    @Unique private boolean absurdcraftStunnedInitial;
    @Unique private int absurdcraftStunnedTimer;
    @Unique private int absurdcraftStunnedDuration;
    @Unique private float absurdcraftStunnedDamage;
    @Unique private int absurdcraftStunnedCooldown;
    @Unique private boolean absurdcraftStunnedIsPostStop;

//Real ticks existed
    @Unique
    private static final DataParameter<Integer> REAL_TICKS_EXISTED = EntityDataManager.<Integer>createKey(EntityLiving.class, DataSerializers.VARINT);

//Common architecture for scheduled actions
    @Unique
    private final ConcurrentLinkedQueue<QueuedActionAtPos> queuedActionsAtPos = new ConcurrentLinkedQueue<>();




    @Inject
    (
        method = "onUpdate",
        at = @At("HEAD")
    )
    private void onUpdateStunAndRealTicks(CallbackInfo callInfo)
    {
        this.setRealTicksExisted(this.getRealTicksExisted() + 1);
    }


    @Inject
    (
        method = "onUpdate",
        at = @At("TAIL")
    )
    private void onUpdateStun(CallbackInfo callInfo)
    {
        if(this.absurdcraftStunnedInitial)
        {
            this.onAbsurdcraftStunned();
            this.onAbsurdcraftStunnedExtra();
            this.absurdcraftStunnedInitial = false;
        }        


        if(this.absurdcraftStunnedTimer > 0)
        {   
            if(--absurdcraftStunnedTimer <= 0)
            {
                this.setAbsurdcraftStunned(false);
                this.onLoseAbsurdcraftStunned();
                this.onLoseAbsurdcraftStunnedExtra();
            }
            else
            {
                this.duringAbsurdcraftStunned();
                this.duringAbsurdcraftStunnedExtra();
            }
        }
        else if(this.absurdcraftStunnedCooldown > 0)
        {
            --this.absurdcraftStunnedCooldown;
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
//Register the new data parameters
        selfEntity.getDataManager().register(REAL_TICKS_EXISTED, Integer.valueOf(0));
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
        compound.setInteger("RealTicksExisted", this.getRealTicksExisted());
        compound.setBoolean("AbsurdcraftStunned", this.getAbsurdcraftStunned());
        compound.setBoolean("AbsurdcraftStunnedInitial", this.getAbsurdcraftStunnedInitial());
        compound.setInteger("AbsurdcraftStunnedTimer", this.getAbsurdcraftStunnedTimer());
        compound.setInteger("AbsurdcraftStunnedDuration", this.getAbsurdcraftStunnedDuration());
        compound.setFloat("AbsurdcraftStunnedDamage", this.getAbsurdcraftStunnedDamage());
        compound.setInteger("AbsurdcraftStunnedCooldown", this.getAbsurdcraftStunnedCooldown());
        compound.setBoolean("AbsurdcraftStunnedIsPostStop", this.getAbsurdcraftStunnedIsPostStop());

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
        if (compound.hasKey("RealTicksExisted")) { this.setRealTicksExisted(compound.getInteger("RealTicksExisted")); }
        if (compound.hasKey("AbsurdcraftStunned")) { this.setAbsurdcraftStunned(compound.getBoolean("AbsurdcraftStunned"));}
        if (compound.hasKey("AbsurdcraftStunnedInitial")) { this.setAbsurdcraftStunnedInitial(compound.getBoolean("AbsurdcraftStunnedInitial"));}
        if (compound.hasKey("AbsurdcraftStunnedTimer")) { this.setAbsurdcraftStunnedTimer(compound.getInteger("AbsurdcraftStunnedTimer")); }
        if (compound.hasKey("AbsurdcraftStunnedDuration")) { this.setAbsurdcraftStunnedDuration(compound.getInteger("AbsurdcraftStunnedDuration")); }
        if (compound.hasKey("AbsurdcraftStunnedDamage")) { this.setAbsurdcraftStunnedDamage(compound.getFloat("AbsurdcraftStunnedDamage")); }
        if (compound.hasKey("AbsurdcraftStunnedCooldown")) { this.setAbsurdcraftStunnedCooldown(compound.getInteger("AbsurdcraftStunnedCooldown")); }
        if (compound.hasKey("AbsurdcraftStunnedIsPostStop")) { this.setAbsurdcraftStunnedIsPostStop(compound.getBoolean("AbsurdcraftStunnedIsPostStop")); }

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

    public int getRealTicksExisted()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Integer)selfEntity.getDataManager().get(REAL_TICKS_EXISTED)).intValue();
    }

//Named this way for compatibility
    public boolean getAbsurdcraftStunned()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Boolean)selfEntity.getDataManager().get(IS_ABSURDCRAFT_STUNNED)).booleanValue();
    }

    public boolean getAbsurdcraftStunnedInitial()
    {
        return this.absurdcraftStunnedInitial;
    }

    public int getAbsurdcraftStunnedTimer()
    {
        return this.absurdcraftStunnedTimer;
    }

    public int getAbsurdcraftStunnedDuration()
    {
        return this.absurdcraftStunnedDuration;
    }

    public float getAbsurdcraftStunnedDamage()
    {
        return this.absurdcraftStunnedDamage;
    }

    public int getAbsurdcraftStunnedCooldown()
    {
        return this.absurdcraftStunnedCooldown;
    }

    public boolean getAbsurdcraftStunnedIsPostStop()
    {
        return this.absurdcraftStunnedIsPostStop;
    }

//Get queued actions
    public ConcurrentLinkedQueue<QueuedActionAtPos> getQueuedActions()
    {
        return this.queuedActionsAtPos;
    }


//New setters

    public void setRealTicksExisted(int realTicks)
    {
        Entity selfEntity = (Entity) (Object) this;
        selfEntity.getDataManager().set(REAL_TICKS_EXISTED, Integer.valueOf(realTicks));    
    }

    public void duringAbsurdcraftStunned()
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
    }

    public void duringAbsurdcraftStunnedExtra()
    {

    }

    public void onLoseAbsurdcraftStunned()
    {
        this.absurdcraftStunnedIsPostStop = true;
    }

    public void onLoseAbsurdcraftStunnedExtra()
    {

    }

    public void onAbsurdcraftStunned()
    {
        EntityLiving self = (EntityLiving) (Object) this;
        Entity selfEntity = (Entity) (Object) this;
        EntityLivingBase selfEntityLivingBase = (EntityLivingBase) (Object) this;

//Play sound
        this.onAbsurdcraftStunnedSound();

//Stop moving
        self.getNavigator().clearPath();
//Clear target
        self.setAttackTarget(null);
        selfEntityLivingBase.setRevengeTarget(null);
 
//Take self-damage
        selfEntityLivingBase.setHealth(selfEntityLivingBase.getHealth() - this.getAbsurdcraftStunnedDamage());
    }

    public void onAbsurdcraftStunnedSound()
    {
        EntityLiving self = (EntityLiving) (Object) this;
        Entity selfEntity = (Entity) (Object) this;
        EntityLivingBase selfEntityLivingBase = (EntityLivingBase) (Object) this;

//Get target
        EntityLivingBase attackTarget = self.getAttackTarget();
//Play sound
        if(selfEntity.isNonBoss())
        {
            if(attackTarget != null)
            {
                selfEntity.world.playSound(null, attackTarget.posX, attackTarget.posY, attackTarget.posZ,
                EternityModeSoundEvents.ENTITY_DIZZY, SoundCategory.HOSTILE, 5.0F, 1.0F);            
            }
            else
            {
                selfEntity.playSound(EternityModeSoundEvents.ENTITY_DIZZY, 5.0F, 1.0F);
            }
        }
        else
        {
            if(attackTarget != null)
            {
                selfEntity.world.playSound(null, attackTarget.posX, attackTarget.posY, attackTarget.posZ,
                EternityModeSoundEvents.ENTITY_DIZZY_BOSS, SoundCategory.HOSTILE, 10.0F, 1.0F);            
            }
            else
            {
                selfEntity.playSound(EternityModeSoundEvents.ENTITY_DIZZY_BOSS, 10.0F, 1.0F);
            }
        }
    }

    public void onAbsurdcraftStunnedExtra()
    {
    
    }

    public void setAbsurdcraftStunned(boolean isStunned)
    {
        Entity selfEntity = (Entity) (Object) this;
        selfEntity.getDataManager().set(IS_ABSURDCRAFT_STUNNED, Boolean.valueOf(isStunned)); 
    }

    public void setAbsurdcraftStunnedInitial(boolean isStunnedInitial)
    {
        this.absurdcraftStunnedInitial = isStunnedInitial;
    }

    public void setAbsurdcraftStunnedTimer(int time)
    {
        this.absurdcraftStunnedTimer = time;
    }

    public void setAbsurdcraftStunnedDuration(int duration)
    {
        this.absurdcraftStunnedDuration = duration;
    }

    public void setAbsurdcraftStunnedDamage(float damage)
    {
        this.absurdcraftStunnedDamage = damage;
    }

    public void setAbsurdcraftStunnedCooldown(int cooldown)
    {
        this.absurdcraftStunnedCooldown = cooldown;
    }

    public void setAbsurdcraftStunnedIsPostStop(boolean isPostStop)
    {
        this.absurdcraftStunnedIsPostStop = isPostStop;
    }

    public void setAbsurdcraftStunned(boolean isStunned, int time)
    {
        if(this.absurdcraftStunnedCooldown <= 0)
        {
            Entity selfEntity = (Entity) (Object) this;
            selfEntity.getDataManager().set(IS_ABSURDCRAFT_STUNNED, Boolean.valueOf(isStunned));
            this.absurdcraftStunnedTimer = time;
            if(isStunned) { this.absurdcraftStunnedInitial = true; } 
        }
    }

//Add queued action
    public void addQueuedAction(QueuedActionAtPos queuedAction)
    {
        this.queuedActionsAtPos.add(queuedAction);
    }

}
