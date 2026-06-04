package org.sporotofpoorety.eternitymode.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;

import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLiving;


public class EntityAIStun extends EntityAIBase
{
    EntityLiving stunnedEntity;
    IMixinEntityLiving stunnedEntityMixin;

    public EntityAIStun(EntityLiving entityLivingIn)
    {
//Should stop mostly everything 
//except target tasks and tasks that run just by having
//a target and independent of mutexbits, for that i mixined into setAttackTarget()
        this.setMutexBits(7);

        this.stunnedEntity = entityLivingIn;
        this.stunnedEntityMixin = (IMixinEntityLiving) entityLivingIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return (this.stunnedEntityMixin.getAbsurdcraftStunned());
    }

	/**
    * Returns whether an in-progress EntityAIBase should continue executing
	*/
	public boolean continueExecuting()
    {
        return (this.stunnedEntityMixin.getAbsurdcraftStunned());
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.stunnedEntityMixin.onAbsurdcraftStunned();
        this.stunnedEntityMixin.onAbsurdcraftStunnedExtra();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {

    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {

    }
}

