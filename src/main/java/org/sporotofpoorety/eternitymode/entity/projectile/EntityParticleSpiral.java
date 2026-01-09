package org.sporotofpoorety.eternitymode.entity.projectile;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.client.particles.ParticleSpiral;



public class EntityParticleSpiral extends Entity 
{

    private int lifetimeTicks = 100;
    private boolean doesDamage;
    private double damageRadius;
    private double damageHeight;
    private double visualRadius;
    private double riseSpeed;
    private int textureIndex;

    public EntityParticleSpiral(World world) 
    {
        super(world);
        setSize(0.0F, 0.0F);
    }

    public EntityParticleSpiral(World world, double x, double y, double z, 
    boolean doesDamage, double damageRadius, double damageHeight, double visualRadius, double riseSpeed, int textureIndex) 
    {
        this(world);
        setPosition(x, y, z);
        this.doesDamage = doesDamage;
        this.damageRadius = damageRadius;
        this.damageHeight = damageHeight;
        this.visualRadius = visualRadius;
        this.riseSpeed = riseSpeed;
        this.textureIndex = textureIndex;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() 
    {
        super.onUpdate();

        if (--lifetimeTicks <= 0) 
        {
            setDead();
            return;
        }

        if (!world.isRemote) 
        {
            if(doesDamage)
            {
                List<EntityLivingBase> mobs =
                world.getEntitiesWithinAABB
                (
                    EntityLivingBase.class,
                    getEntityBoundingBox().grow(damageRadius, damageHeight, damageRadius)
                );

                for (EntityLivingBase mob : mobs) 
                {
                    mob.setFire(10);
                }
                return;
            }
        }

//Client side
        for (int angleStepAt = 0; angleStepAt < 45; angleStepAt += 5) 
        {
            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSpiral(world, 40,
            posX, posY, posZ, posX, posZ, textureIndex, 8, angleStepAt, visualRadius, riseSpeed));
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) 
    {
        lifetimeTicks = tag.getInteger("Lifetime");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) 
    {
        tag.setInteger("Lifetime", lifetimeTicks);
    }
}
