package org.sporotofpoorety.eternitymode.entity;

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



public class EntityParticleSpiral extends EntityWithOwner 
{

    protected int timeToArm;

    protected boolean doesDamage;
    protected int damageInterval;
    protected float damageAmount;
    protected double damageRadius;
    protected double damageHeight;

    protected int particleLifetime;
    protected double visualRadius;
    protected double riseSpeed;
    protected int textureIndex;


    public EntityParticleSpiral(World world) 
    {
        super(world);
        setSize(0.5F, 0.5F);

        this.lifetimeMax = 200;
        this.timeToArm = 40;

        this.doesDamage = true;
        this.damageInterval = 5;
        this.damageAmount = 10.0F;
        this.damageRadius = 4.0D;
        this.damageHeight = 8.0D;

        this.particleLifetime = 20;
        this.visualRadius = 4.0D;
        this.riseSpeed = 0.4D;
        this.textureIndex = 48;
    }

    public EntityParticleSpiral(World world, double x, double y, double z,
    EntityLivingBase owner, 
    int lifetimeMax, int timeToArm,
    boolean doesDamage, int damageInterval, float damageAmount, double damageRadius, double damageHeight, 
    int particleLifetime, double visualRadius, double riseSpeed, int textureIndex) 
    {
        super(world, x, y, z, owner);
        setSize(0.5F, 0.5F);

        this.lifetimeMax = lifetimeMax;
        this.timeToArm = timeToArm;

        this.doesDamage = doesDamage;
        this.damageInterval = damageInterval;
        this.damageAmount = damageAmount;
        this.damageRadius = damageRadius;
        this.damageHeight = damageHeight;

        this.particleLifetime = particleLifetime;
        this.visualRadius = visualRadius;
        this.riseSpeed = riseSpeed;
        this.textureIndex = textureIndex;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        super.writeEntityToNBT(compound);

        compound.setInteger("TimeToArm", timeToArm);

        compound.setBoolean("DoesDamage", doesDamage);
        compound.setInteger("DamageInterval", damageInterval);
        compound.setFloat("DamageAmount", damageAmount);
        compound.setDouble("DamageRadius", damageRadius);
        compound.setDouble("DamageHeight", damageHeight);

        compound.setInteger("ParticleLifetime", particleLifetime);
        compound.setDouble("VisualRadius", visualRadius);
        compound.setDouble("RiseSpeed", riseSpeed);
        compound.setInteger("TextureIndex", textureIndex);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) 
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("TimeToArm")) { timeToArm = compound.getInteger("TimeToArm"); }

        if (compound.hasKey("DoesDamage")) { doesDamage = compound.getBoolean("DoesDamage"); }
        if (compound.hasKey("DamageInterval")) { damageInterval = compound.getInteger("DamageInterval"); }
        if (compound.hasKey("DamageAmount")) { damageAmount = compound.getFloat("DamageAmount"); }
        if (compound.hasKey("DamageRadius")) { damageRadius = compound.getDouble("DamageRadius"); }
        if (compound.hasKey("DamageHeight")) { damageHeight = compound.getDouble("DamageHeight"); }

        if (compound.hasKey("ParticleLifetime")) { particleLifetime = compound.getInteger("ParticleLifetime"); }
        if (compound.hasKey("VisualRadius")) { visualRadius = compound.getDouble("VisualRadius"); }
        if (compound.hasKey("RiseSpeed")) { riseSpeed = compound.getDouble("RiseSpeed"); }
        if (compound.hasKey("TextureIndex")) { textureIndex = compound.getInteger("TextureIndex"); }
    }


    @Override
    protected void entityInit() {}


    @Override
    public void onUpdate() 
    {
        super.onUpdate();


        if(this.realTicksExisted <= timeToArm)
        {
            this.preArmLogic();   
        }
        else
        {
            this.postArmLogic();
        }


        this.performBasicMovement();
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
                double particlePositionY = this.posY + (this.rand.nextDouble() * this.damageHeight); 
                double particlePositionZ = this.posZ + (double) particleOffsetZ;

//Lava particles
                this.world.spawnParticle(EnumParticleTypes.LAVA, particlePositionX, particlePositionY, particlePositionZ, 0.0D, 0.0D, 0.0D);
            }
    }


    public void postArmLogic()
    {
        if (!world.isRemote) 
        {
            if(doesDamage && (this.realTicksExisted % this.damageInterval == 0))
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

}
