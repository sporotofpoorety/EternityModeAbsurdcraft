package org.sporotofpoorety.eternitymode.client.particles;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



@SideOnly(Side.CLIENT)
public class ParticleSpiral extends Particle 
{

    private final double centerX;
    private final double centerZ;
//Rotates this many degrees every tick
    private final double angleStep;
//Tick rotation at
    private int angleStepAt;
    private final double radius;
    private final float baseScale;
    private final double riseSpeed;




    public ParticleSpiral(World world, int maxAge, double x, double y, double z, double centerX, double centerZ, 
    int textureIndex, int angleStepValue, int startAngleStepAt, double radius, double riseSpeed) 
    {
        super(world, x, y, z);

        this.centerX = centerX;
        this.centerZ = centerZ;
        this.angleStep = Math.toRadians(angleStepValue);
        this.angleStepAt = startAngleStepAt;
        this.radius = radius;
        this.riseSpeed = riseSpeed;

        this.motionX *= 0.01;
        this.motionY *= 0.01;
        this.motionZ *= 0.01;

        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.baseScale = this.particleScale + 0.5F;

        this.particleMaxAge = maxAge;
        this.setParticleTextureIndex(textureIndex);
    }

    @Override
    public void onUpdate() 
    {
        if (++particleAge >= particleMaxAge) 
        {
            setExpired();
            return;
        }

//Gotta manually interpolate position
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        double spunX = centerX + (Math.cos(angleStepAt * angleStep) * radius);
        double spunZ = centerZ + (Math.sin(angleStepAt * angleStep) * radius);

        angleStepAt++;

        setPosition(spunX, posY + riseSpeed, spunZ);
    }

//This is just standard stuff for every particle
    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn,
    float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) 
    {
        float ageFactor = (particleAge + partialTicks) / particleMaxAge;
        particleScale = baseScale * (1.0F - (ageFactor * ageFactor * 0.5F));
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ,
                             rotationYZ, rotationXY, rotationXZ);
    }
}
