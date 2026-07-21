package org.sporotofpoorety.eternitymode.core;


import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import org.sporotofpoorety.eternitymode.packets.*;



//This is my first time using packets...
public class EternityModePacketRegistry 
{

//Make mod's own network channel
    public static SimpleNetworkWrapper CHANNEL_INSTANCE 
        = NetworkRegistry.INSTANCE.newSimpleChannel("eternitymode");

//Different packet ids
    private static int packetId = 0;
    
//Register each message
    public static void registerMessages() 
    {
//Explosion particles
        CHANNEL_INSTANCE.registerMessage
        (
            ExplosionVisualPacket.Handler.class,
            ExplosionVisualPacket.class,
            0, 
            Side.CLIENT
        );
    }

}
