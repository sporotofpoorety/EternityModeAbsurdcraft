package org.sporotofpoorety.eternitymode.packets;


import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.sporotofpoorety.eternitymode.util.ExplosionUtil;




public class ExplosionVisualPacket implements IMessage 
{
    private int particleType;
    private double atX, atY, atZ;
    private float radius;
    private boolean breakBlocks;
    

    public ExplosionVisualPacket() {}
    
    public ExplosionVisualPacket(int particleType, double atX, double atY, double atZ, float radius, boolean breakBlocks) 
    {
        this.particleType = particleType;
        this.atX = atX; this.atY = atY; this.atZ = atZ;
        this.radius = radius; 
        this.breakBlocks = breakBlocks;
    }
    

    @Override
    public void toBytes(ByteBuf buf) 
    {
        buf.writeInt(this.particleType);
        buf.writeDouble(this.atX); buf.writeDouble(this.atY); buf.writeDouble(this.atZ);
        buf.writeFloat(this.radius);
        buf.writeBoolean(this.breakBlocks);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) 
    {
        this.particleType = buf.readInt();
        this.atX = buf.readDouble(); this.atY = buf.readDouble(); this.atZ = buf.readDouble();
        this.radius = buf.readFloat();
        this.breakBlocks = buf.readBoolean();
    }
    

//Packet handler
    public static class Handler implements IMessageHandler<ExplosionVisualPacket, IMessage> 
    {
//On receiving message
        @Override
        public IMessage onMessage(ExplosionVisualPacket msg, MessageContext ctx) 
        {
//Schedule client task
            Minecraft.getMinecraft().addScheduledTask
            (
//Lambda
                () -> {
//Get client world
                    World worldIn = Minecraft.getMinecraft().world;
//Spawn particles there
                    ExplosionUtil.explosionParticleSelection(worldIn, 
                        msg.particleType, msg.atX, msg.atY, msg.atZ, msg.radius, msg.breakBlocks);
                }
            );

            return null;
        }
    }
}
