package org.sporotofpoorety.eternitymode.util;


import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;




public final class PlayerMeleeUtil 
{

//Latest swing charges for players
    private static final Map<EntityPlayer, Float> LATEST_SWING_CHARGES = new HashMap<>();


    public static void setLatestCharge(EntityPlayer player)
    {
        LATEST_SWING_CHARGES.put(player, player.getCooledAttackStrength(0.0F));
    }

    public static float getLatestCharge(EntityPlayer player)
    {
        return LATEST_SWING_CHARGES.getOrDefault(player, 0.0F);
    }




//Is a hit critical 
//(For when no direct access to that data)
    public static boolean isCriticalHit(EntityPlayer player, Entity target) 
    {
//Swing nearly fully ready?
        float attackCharge = getLatestCharge(player);
        if (attackCharge < 0.9f) { return false; }
        

//Player can't be on ground
        if (player.onGround) 
        {
            return false;
        } 
//Neither on ladder or water
        else 
        {
            if (player.isOnLadder()) { return false; }
            if (player.isInWater()) { return false; }
        }
        
//Or sprinting
        if (player.isSprinting()) return false;
        
//Or blind i guess?
        if (player.isPotionActive(MobEffects.BLINDNESS)) { return false; }
        
//Or riding smth
        if (player.isRiding()) { return false; }
        
//Target must be living
        if (!(target instanceof EntityLivingBase)) { return false; }
        

//Only THEN is it a crit
        return true;
    }

}


