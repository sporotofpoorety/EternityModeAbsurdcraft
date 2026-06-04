package org.sporotofpoorety.eternitymode.events;


import net.minecraft.entity.EntityLiving;

import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLiving;




public class EternityModeEntityEvents 
{

//NOTE TO SELF: 
//SETATTACKTARGET() CAN CAUSE RECURSION WITH THIS EVENT
	@SubscribeEvent
	public void onSetAttackTarget(LivingSetAttackTargetEvent event)
	{

	    EntityLiving living = (EntityLiving) event.getEntityLiving();

        if(!living.world.isRemote)
        {
//If attacker not null
		    if(living != null)
		    {

//ANTI-RECURSION GUARD HERE
//If target not ALREADY null
                if(event.getTarget() != null)
                {
//Let's see if this actually makes the stun work
                    IMixinEntityLiving selfEntityLivingMixin = (IMixinEntityLiving) (Object) living;
//Can't target anything if stunned
                    if(selfEntityLivingMixin.getAbsurdcraftStunned())
                    {
	                    living.setAttackTarget(null);
                    }
                }
            }
        }     
	}
}
