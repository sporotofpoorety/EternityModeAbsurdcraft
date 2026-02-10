package org.sporotofpoorety.eternitymode.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;

import org.sporotofpoorety.eternitymode.entity.ai.EntityAIRelentlessTargetPlayers;




//Mixin this class
@Mixin(value = EntitySlime.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntitySlime
{

    @Inject
    (
//Inject in this method
        method = "initEntityAI",
//At tail
        at = @At("TAIL")
    )
    private void slimeTargetTaskReplace(CallbackInfo callInfo)
    {
        EntitySlime selfEntitySlime = (EntitySlime) (Object) this;


//Get set of task entries
        selfEntitySlime.targetTasks.taskEntries.removeIf
//Remove based on instanceof predicate
            ( taskEntry -> (taskEntry.action instanceof EntityAIFindEntityNearestPlayer) );
/*
//Repeat
        selfEntitySlime.targetTasks.taskEntries.removeIf
            ( taskEntry -> (taskEntry.action instanceof EntityAIFindEntityNearest) );
*/


//Add new player target task
        selfEntitySlime.targetTasks.addTask(1, new EntityAIRelentlessTargetPlayers(selfEntitySlime, 160.0D));
    }

}
