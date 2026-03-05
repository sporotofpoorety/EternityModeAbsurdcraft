package org.sporotofpoorety.eternitymode.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.client.particles.ParticleSpiral;
import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;



public class EntityParticleShield extends EntityWithOwner 
{

    protected int lifetimeTicks;
    protected int timeToArm;

    protected double shieldRadius;

    public ArrayList<Vec3d> particleDirs = new ArrayList<>();

    protected int particleLifetime;




    public EntityParticleShield(World world) 
    {
        super(world);
        setSize(0.5F, 0.5F);

        this.lifetimeTicks = 200;
        this.timeToArm = 40;

        this.shieldRadius = 2.0D;

        this.particleLifetime = 20;
    }

    public EntityParticleShield(World world, double x, double y, double z, 
    int lifetimeTicks, int timeToArm,
    double shieldRadius,
    int particleLifetime) 
    {
        this(world);
        setPosition(x, y, z);

        this.lifetimeTicks = lifetimeTicks;
        this.timeToArm = timeToArm;

        this.shieldRadius = shieldRadius;

        this.particleLifetime = particleLifetime;
    }



    @Override
    protected void entityInit() {}
/*

    @Override
    public void onUpdate() 
    {
        super.onUpdate();

        if (!world.isRemote && this.ticksExisted > lifetimeTicks) 
        {
            setDead();
            return;
        }


        this.performBasicMovement();


//Pre-arm logic
        if(this.ticksExisted <= this.timeToArm)
        {
            this.preArmLogic();   
        }
//Post-arm logic
        else
        {
            this.postArmLogic();
        }
    }


    public void preArmLogic()
    {
        for (int particleAt = 0; particleAt < 2; particleAt++)
        {
            float randomAngle = this.rand.nextFloat() * (2F * (float) Math.PI);
            float randomExtent = this.rand.nextFloat() * (float) this.damageRadius;

//Particle offset at random angle and distance from center
            float particleOffsetX = MathHelper.sin(randomAngle) * randomExtent;
            float particleOffsetZ = MathHelper.cos(randomAngle) * randomExtent;

//Offset from the spiral center
            double particlePositionX = this.posX + (double) particleOffsetX;
            double particlePositionZ = this.posZ + (double) particleOffsetZ;

//Lava particles
            this.world.spawnParticle(EnumParticleTypes.LAVA, particlePositionX, this.posY + 0.5D, particlePositionZ, 0.0D, 0.0D, 0.0D);
        }
    }


    public void postArmLogic()
    {
        if (!world.isRemote) 
        {
            if(doesDamage && (this.ticksExisted % this.damageInterval == 0))
            {
                this.spiralDamageLogic();
            }
        }

//Client side?
        for (int angleStepAt = 0; angleStepAt < 45; angleStepAt += 5) 
        {
            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSpiral(this.world, particleLifetime,
            posX, posY, posZ, posX, posZ, textureIndex, 8, angleStepAt, visualRadius, riseSpeed));
        }
    }


    public void spiralDamageLogic()
    {
        List<EntityLivingBase> mobs =
        world.getEntitiesWithinAABB
        (
            EntityLivingBase.class,
            this.getEntityBoundingBox().offset(0.0D, damageHeight / 2.0D, 0.0D).grow(damageRadius, damageHeight, damageRadius)
        );
        
        for (EntityLivingBase mob : mobs) 
        {
            attackEntityFrom(DamageSource.GENERIC, this.damageAmount);
        }
    }
*/
}
