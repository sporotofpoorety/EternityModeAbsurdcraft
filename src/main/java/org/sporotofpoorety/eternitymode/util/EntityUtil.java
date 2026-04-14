package org.sporotofpoorety.eternitymode.util;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;




public final class EntityUtil 
{

    public static boolean isPosCloseToAnyPlayer(World world, double atX, double atZ, double distanceLimit, boolean countSpectator)
    {
        EntityPlayer closestPlayer = world.getClosestPlayer(atX, 127.0D, atZ, 9999.0D, countSpectator);


        if(closestPlayer == null)
        {
            return false;
        }
        else
        {
            if(Math.sqrt(Math.pow(closestPlayer.posX - atX, 2) + Math.pow(closestPlayer.posZ - atZ, 2)) < distanceLimit)
            {
                return true;
            }
            else
            {
                return false;
            }
        } 
    }

}
