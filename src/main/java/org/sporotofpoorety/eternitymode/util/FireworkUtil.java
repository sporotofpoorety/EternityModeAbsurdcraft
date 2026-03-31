package org.sporotofpoorety.eternitymode.util;


import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;




public final class FireworkUtil 
{

//Make firework effects
    public static void makeFireworkEffects(World world, double atX, double atY, double atZ,
    double motionX, double motionY, double motionZ,
    int lifetime, 
    boolean hasFlicker, boolean hasTrail, int explosionType)
    {
//With specified NBT
        NBTTagCompound fireworkNBT = makeFireworkNBT(world, lifetime, 
            hasFlicker, hasTrail, explosionType);


//Via effect renderer
        Minecraft mc = Minecraft.getMinecraft();
        mc.effectRenderer.addEffect(new ParticleFirework.Starter(world, atX, atY, atZ, motionX, motionY, motionZ, mc.effectRenderer, fireworkNBT));

    }


//With random colors
    public static NBTTagCompound makeFireworkNBT(World world, int lifetime, 
    boolean hasFlicker, boolean hasTrail, int explosionType)
    {
//Try to replicate firework NBT
        NBTTagCompound fireworkNBT = new NBTTagCompound();
//10X + 0-13 lifetime
        fireworkNBT.setByte("Flight", (byte) lifetime);


//Explosions list
        NBTTagList explosionList = new NBTTagList(); 

//Individual explosion
        NBTTagCompound explosionNBT = new NBTTagCompound();

        explosionNBT.setBoolean("Flicker", hasFlicker);
        explosionNBT.setBoolean("Trail", hasTrail);

//0 Small ball, 2 Large ball, 3 Star-shaped, 3 Creeper, 4 Burst
        explosionNBT.setByte("Type", (byte) explosionType);


//Generate colors randomly from dye
        int[] colorList = new int[world.rand.nextInt(8) + 1];
        for (int i = 0; i < colorList.length; i++) 
        {
          colorList[i] = ItemDye.DYE_COLORS[world.rand.nextInt(16)];
        }
        explosionNBT.setIntArray("Colors", colorList);


//Add explosion to list
        explosionList.appendTag(explosionNBT);
//Add explosion list to firework NBT
        fireworkNBT.setTag("Explosions", explosionList);


//Return firework NBT
        return fireworkNBT;
    }


//With specific colors
    public static NBTTagCompound makeFireworkNBT(World world, int lifetime, 
    boolean hasFlicker, boolean hasTrail, int explosionType,
    int[] mainColors, int[] fadeColors)
    {
//Try to replicate firework NBT
        NBTTagCompound fireworkNBT = new NBTTagCompound();
//10X + 0-13 lifetime
        fireworkNBT.setByte("Flight", (byte) lifetime);


//Explosions list
        NBTTagList explosionList = new NBTTagList(); 

//Individual explosion
        NBTTagCompound explosionNBT = new NBTTagCompound();

        explosionNBT.setBoolean("Flicker", hasFlicker);
        explosionNBT.setBoolean("Trail", hasTrail);

//0 Small ball, 2 Large ball, 3 Star-shaped, 4 Creeper, 5 Burst
        explosionNBT.setByte("Type", (byte) explosionType);

//Specified color
        explosionNBT.setIntArray("Colors", mainColors);
        explosionNBT.setIntArray("FadeColors", fadeColors);


//Add explosion to list
        explosionList.appendTag(explosionNBT);
//Add explosion list to firework NBT
        fireworkNBT.setTag("Explosions", explosionList);


//Return firework NBT
        return fireworkNBT;
    }

}
