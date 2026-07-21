package org.sporotofpoorety.eternitymode.util;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import org.sporotofpoorety.eternitymode.core.EternityModePacketRegistry;





public final class PacketUtil 
{

//Send custom message to nearby players
    public static void sendPacketToNearbyPlayers(World worldIn, double atX, double atY, double atZ, double maxDist, IMessage messageIn)
    {
//Send packet
        sendPacketToNearbyPlayers(worldIn, atX, atY, atZ, maxDist,
//Translate message into packet
            EternityModePacketRegistry.CHANNEL_INSTANCE.getPacketFrom(messageIn));
    }


    public static void sendPacketToNearbyPlayers(World worldIn, double atX, double atY, double atZ, double maxDist, Packet<?> packetIn)
    {
        if(worldIn instanceof WorldServer)
        {
            for(EntityPlayer player : worldIn.playerEntities)
            {
                if(player instanceof EntityPlayerMP)
                {
                    if(player.getDistance(atX, atY, atZ) <= maxDist)
                    {
                        ((EntityPlayerMP)player).connection.sendPacket(packetIn);
                    }
                }
            }
        }
    }

}
