package org.sporotofpoorety.eternitymode.util;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import electroblob.wizardry.Wizardry;




public final class MiscUtil 
{

//Return compound at compound, 
//and if it doesn't exist, create it
    public static NBTTagCompound compoundInCompound(String compoundKey, NBTTagCompound atCompound) 
    {

//If key found
        if(atCompound.hasKey(compoundKey)) 
        {
//Return nested compound with that key
            return atCompound.getCompoundTag(compoundKey);
        }

//If key not found 
        else 
        {
//Create a new compound
            NBTTagCompound newCompound = new NBTTagCompound();
//Then assign it to the parameter key
            atCompound.setTag(compoundKey, newCompound);
//And return new compound            
            return atCompound.getCompoundTag(compoundKey);
        }
    }


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


    public static void screenShakeForNearbyPlayers(int shakeIntensity)
    {
        EntityPlayer localPlayer = Minecraft.getMinecraft().player;
        Wizardry.proxy.shakeScreen(localPlayer, shakeIntensity);
    }


//Necessary for server side sound
    public static void sendPacketToNearSound(World worldIn, double atX, double atY, double atZ, double maxDist,
    SoundEvent soundEvent, SoundCategory category, float volume, float pitch)
    {
        for(EntityPlayer player : worldIn.playerEntities)
        {
            if(player.getDistance(atX, atY, atZ) <= maxDist)
            {
                ((EntityPlayerMP)player).connection.sendPacket
                (
                    new SPacketSoundEffect
                    (
                        soundEvent, category, atX, atY, atZ, volume, pitch
                    ) 
                );
            }
        }
    }
}


