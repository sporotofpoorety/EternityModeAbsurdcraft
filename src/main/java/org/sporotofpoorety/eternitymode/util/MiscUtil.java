package org.sporotofpoorety.eternitymode.util;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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


    public static void screenShakeForNearbyPlayers(int shakeIntensity)
    {
        EntityPlayer localPlayer = Minecraft.getMinecraft().player;
        Wizardry.proxy.shakeScreen(localPlayer, shakeIntensity);
    }
}


