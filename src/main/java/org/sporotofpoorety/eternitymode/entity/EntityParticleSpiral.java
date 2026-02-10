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



public class EntityParticleSpiral extends Entity 
{

    private int lifetimeTicks;
    private int timeToArm;
    private boolean setsFire;
    private int setsFireLength;
    private boolean doesDamage;
    private int damageInterval;
    private float damageAmount;
    private double damageRadius;
    private double damageHeight;
    private int particleLifetime;
    private double visualRadius;
    private double riseSpeed;
    private int textureIndex;




    public EntityParticleSpiral(World world) 
    {
        super(world);
        setSize(0.5F, 0.5F);
    }

    public EntityParticleSpiral(World world, double x, double y, double z, 
    int lifetimeTicks, int timeToArm, boolean hasGravity, boolean setsFire, int setsFireLength, 
    boolean doesDamage, int damageInterval, float damageAmount, double damageRadius, double damageHeight, 
    int particleLifetime, double visualRadius, double riseSpeed, int textureIndex) 
    {
        this(world);
        setPosition(x, y, z);
        this.lifetimeTicks = lifetimeTicks;
        this.timeToArm = timeToArm;
        this.setNoGravity(!hasGravity);
        this.setsFire = setsFire;
        this.setsFireLength = setsFireLength;
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
    protected void entityInit() {}


    @Override
    public void onUpdate() 
    {
        super.onUpdate();

        if (!world.isRemote && this.ticksExisted > lifetimeTicks) 
        {
            setDead();
            return;
        }


        if(!this.hasNoGravity())
        {
            this.move(MoverType.SELF, 0.0D, -0.08D, 0.0D);
        }


        if(this.ticksExisted > timeToArm)
        {
            if (!world.isRemote) 
            {
                if(doesDamage && (this.ticksExisted % this.damageInterval == 0))
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

                    if(this.setsFire)
                    {
                        for (EntityLivingBase mob : mobs) 
                        {
                            mob.setFire(this.setsFireLength);
                        }
                    }

                    return;
                }
            }

    //Client side
            for (int angleStepAt = 0; angleStepAt < 45; angleStepAt += 5) 
            {
                Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSpiral(world, particleLifetime,
                posX, posY, posZ, posX, posZ, textureIndex, 8, angleStepAt, visualRadius, riseSpeed));
            }
        }
//If this is arming
        else
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
    }




    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
        compound.setInteger("Lifetime", lifetimeTicks);
        compound.setInteger("TimeToArm", timeToArm);
        compound.setBoolean("HasGravity", !this.hasNoGravity());
        compound.setBoolean("SetsFire", setsFire);
        compound.setInteger("SetsFireLength", setsFireLength);
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
        if (compound.hasKey("Lifetime")) { lifetimeTicks = compound.getInteger("Lifetime"); }
        if (compound.hasKey("TimeToArm")) { timeToArm = compound.getInteger("TimeToArm"); }
        if (compound.hasKey("HasGravity")) { this.setNoGravity(!compound.getBoolean("HasGravity")); }
        if (compound.hasKey("SetsFire")) { setsFire = compound.getBoolean("SetsFire"); }
        if (compound.hasKey("SetsFireLength")) { setsFireLength = compound.getInteger("SetsFireLength"); }
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

}
