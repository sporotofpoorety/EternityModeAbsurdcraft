package org.sporotofpoorety.eternitymode.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;


import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;
import org.sporotofpoorety.eternitymode.util.ExplosionUtil;




public class EntityExplosiveShockwave extends EntityWithOwner 
{

    public double speedX;
    public double speedY;
    public double speedZ;

    public boolean oscillationEnabled;
    public double oscillationDistance;
    public double oscillationRadianStep;
    public double oscillationX;
    public double oscillationZ;
    public double oscillationInitial;
 
    public int explosionTimer;
    public double explosionRadius;
    public float explosionDamage;
    public boolean explosionPush;
    public double explosionPushForce;
    public boolean explosionFire;
    public int explosionParticleType;

    public boolean harmlessSwitch;

    public int specialExplosionCounter;
    public int specialExplosionThreshold;

    public boolean subshockwavesEnabled;
    public int subshockwavesLifetime;

    public double subshockwavesSpeedX;
    public double subshockwavesSpeedY;
    public double subshockwavesSpeedZ;
    public double subshockwavesAccelerationVal;
 
    public int subshockwavesExplosionTimer;
    public double subshockwavesExplosionRadius;




    public EntityExplosiveShockwave(World world) 
    {
        super(world);
        setSize(0.5F, 0.5F);
//Should noclip by default ig
        this.noClip = true;
    }

    public EntityExplosiveShockwave(World worldIn, double x, double y, double z, 
    EntityLivingBase owner,
    int lifetimeMax, boolean hasGravity, float shockwaveStepHeight, double speedX, double speedY, double speedZ, double accelerationVal,
    boolean oscillationEnabled, double oscillationDistance, double oscillationRadianStepCount,
    int explosionTimer, double explosionRadius, float explosionDamage, 
    boolean explosionPush, double explosionPushForce, boolean explosionFire, int explosionParticleType, int specialExplosionThreshold) 
    {
        super(worldIn, x, y, z, owner);
        setSize(0.5F, 0.5F);
//Should noclip by default ig
        this.noClip = true;

        this.lifetimeMax = lifetimeMax;

        this.setNoGravity(!hasGravity);
        this.stepHeight = shockwaveStepHeight;
        this.motionX = speedX;
        this.motionY = speedY;
        this.motionZ = speedZ;
        this.accelerationVal = accelerationVal;

//Oscillation enabled
        this.oscillationEnabled = oscillationEnabled;
//Distance from one end to the other
        this.oscillationDistance = oscillationDistance;
//Sine wave step
        this.oscillationRadianStep = (2.0D * Math.PI) / oscillationRadianStepCount;
//Oscillate sideways
        this.oscillationX = Math.cos(Math.atan2(speedZ, speedX) + (0.5D * Math.PI));
        this.oscillationZ = Math.sin(Math.atan2(speedZ, speedX) + (0.5D * Math.PI));

        this.explosionTimer = explosionTimer;
        this.explosionRadius = explosionRadius;
        this.explosionDamage = explosionDamage;
        this.explosionPush = explosionPush;
        this.explosionPushForce = explosionPushForce;
        this.explosionFire = explosionFire;
        this.explosionParticleType = explosionParticleType;

        this.specialExplosionThreshold = specialExplosionThreshold;
	}

    public void setSubshockwaves(boolean subshockwavesEnabled, int subshockwavesLifetime,
    double subshockwavesSpeedX, double subshockwavesSpeedY, double subshockwavesSpeedZ, double subshockwavesAccelerationVal,
    int subshockwavesExplosionTimer, double subshockwavesExplosionRadius)
    {
        this.subshockwavesEnabled = subshockwavesEnabled;
        this.subshockwavesLifetime = subshockwavesLifetime;

        this.subshockwavesSpeedX = subshockwavesSpeedX;
        this.subshockwavesSpeedY = subshockwavesSpeedY;
        this.subshockwavesSpeedZ = subshockwavesSpeedZ;
        this.subshockwavesAccelerationVal = subshockwavesAccelerationVal;
     
        this.subshockwavesExplosionTimer = subshockwavesExplosionTimer;
        this.subshockwavesExplosionRadius = subshockwavesExplosionRadius;
    }


    @Override
    protected void entityInit() {}




    @Override
    public void onUpdate() 
    {
        super.onUpdate();


        if (!world.isRemote) 
        {
//Periodic explosions
            if((this.realTicksExisted % this.explosionTimer) == 0)
            {
                ExplosionUtil.performOptimizedExplosion(this.world, this.owner, this.posX, this.posY, this.posZ,
                    this.explosionRadius, !this.harmlessSwitch, this.explosionDamage, this.explosionPush, this.explosionPushForce, false, 9999.0F, this.explosionFire, 
                    true, this.explosionParticleType, false);

//Increment explosion counter
                ++this.specialExplosionCounter;


//If at special explosion threshold
                if(specialExplosionCounter >= specialExplosionThreshold)
                {
//Reset explosion counter
                    this.specialExplosionCounter = 0;

//Do special explosion

//Potentially sub-shockwaves 
                    if(this.subshockwavesEnabled)
                    {
                        if(this.owner != null)
                        {
                            EntityExplosiveShockwave splitShockwave = new EntityExplosiveShockwave(this.world, this.posX, this.posY, this.posZ,
                                this.owner,  
                                this.subshockwavesLifetime, false, 3.0F, 
                                this.subshockwavesSpeedX, this.subshockwavesSpeedY, this.subshockwavesSpeedZ, this.subshockwavesAccelerationVal,
                                false, 3.0D, 9,
                                this.subshockwavesExplosionTimer, this.subshockwavesExplosionRadius, this.explosionDamage, 
                                this.explosionPush, this.explosionPushForce, false, this.explosionParticleType, 69420);

		                    splitShockwave.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);

		                    this.getEntityWorld().spawnEntity(splitShockwave);
                        } 
                    }
                }
            }




//Shockwave motion
            this.performBasicMovement();




//If oscillation enabled
            if(this.oscillationEnabled)
            {
                this.performOscillation();
            }
        }
    }


    public void performOscillation()
    {
        double currentMovement = this.oscillationInitial + (Math.sin(this.realTicksExisted * this.oscillationRadianStep) * this.oscillationDistance);
        
        this.move(MoverType.SELF, currentMovement * this.oscillationX, 0.0, currentMovement * this.oscillationZ);
    }




    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);


        compound.setBoolean("HasGravity", !this.hasNoGravity());
        compound.setFloat("ShockwaveStepHeight", this.stepHeight);
        compound.setDouble("SpeedX", this.speedX);
        compound.setDouble("SpeedY", this.speedY);
        compound.setDouble("SpeedZ", this.speedZ);

        compound.setBoolean("OscillationEnabled", this.oscillationEnabled);
        compound.setDouble("OscillationDistance", this.oscillationDistance);
        compound.setDouble("OscillationRadianStep", this.oscillationRadianStep);
        compound.setDouble("OscillationInitial", this.oscillationInitial);

        compound.setInteger("ExplosionTimer", this.explosionTimer);
        compound.setDouble("ExplosionRadius", this.explosionRadius);
        compound.setFloat("ExplosionDamage", this.explosionDamage);
        compound.setBoolean("ExplosionPush", this.explosionPush);
        compound.setDouble("ExplosionPushForce", this.explosionPushForce);
        compound.setBoolean("ExplosionFire", this.explosionFire);
        compound.setInteger("ExplosionParticleType", this.explosionParticleType);

        compound.setBoolean("HarmlessSwitch", this.harmlessSwitch);

        compound.setInteger("SpecialExplosionCounter", this.specialExplosionCounter);
        compound.setInteger("SpecialExplosionThreshold", this.specialExplosionThreshold);

        compound.setBoolean("SubshockwavesEnabled", this.subshockwavesEnabled);
        compound.setInteger("SubshockwavesLifetime", this.subshockwavesLifetime);

        compound.setDouble("SubshockwavesSpeedX", this.subshockwavesSpeedX);
        compound.setDouble("SubshockwavesSpeedY", this.subshockwavesSpeedY);
        compound.setDouble("SubshockwavesSpeedZ", this.subshockwavesSpeedZ);
     
        compound.setInteger("SubshockwavesExplosionTimer", this.subshockwavesExplosionTimer);
        compound.setDouble("SubshockwavesExplosionRadius", this.subshockwavesExplosionRadius);
    }


    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);


        if (compound.hasKey("HasGravity")) { this.setNoGravity(!compound.getBoolean("HasGravity")); }
        if (compound.hasKey("ShockwaveStepHeight")) { this.stepHeight = compound.getFloat("ShockwaveStepHeight"); }
        if (compound.hasKey("SpeedX")) { this.speedX = compound.getDouble("SpeedX"); }
        if (compound.hasKey("SpeedY")) { this.speedY = compound.getDouble("SpeedY"); }
        if (compound.hasKey("SpeedZ")) { this.speedZ = compound.getDouble("SpeedZ"); }

        if (compound.hasKey("OscillationEnabled")) { this.oscillationEnabled = compound.getBoolean("OscillationEnabled"); }
        if (compound.hasKey("OscillationDistance")) { this.oscillationDistance = compound.getDouble("OscillationDistance"); }
        if (compound.hasKey("OscillationRadianStep")) { this.oscillationRadianStep = compound.getDouble("OscillationRadianStep"); }
        this.oscillationX = Math.cos(Math.atan2(this.speedZ, this.speedX) + (0.5D * Math.PI));
        this.oscillationZ = Math.sin(Math.atan2(this.speedZ, this.speedX) + (0.5D * Math.PI));
        if (compound.hasKey("OscillationInitial")) { this.oscillationInitial = compound.getDouble("OscillationInitial"); }

        if (compound.hasKey("ExplosionTimer")) { this.explosionTimer = compound.getInteger("ExplosionTimer"); }
        if (compound.hasKey("ExplosionRadius")) { this.explosionRadius = compound.getDouble("ExplosionRadius"); }
        if (compound.hasKey("ExplosionDamage")) { this.explosionDamage = compound.getFloat("ExplosionDamage"); }
        if (compound.hasKey("ExplosionPush")) { this.explosionPush = compound.getBoolean("ExplosionPush"); }
        if (compound.hasKey("ExplosionPushForce")) { this.explosionPushForce = compound.getDouble("ExplosionPushForce"); }
        if (compound.hasKey("ExplosionFire")) { this.explosionFire = compound.getBoolean("ExplosionFire"); }
        if (compound.hasKey("ExplosionParticleType")) { this.explosionParticleType = compound.getInteger("ExplosionParticleType"); }

        if (compound.hasKey("HarmlessSwitch")) { this.harmlessSwitch = compound.getBoolean("HarmlessSwitch"); }

        if (compound.hasKey("SpecialExplosionCounter")) { this.specialExplosionCounter = compound.getInteger("SpecialExplosionCounter"); }
        if (compound.hasKey("SpecialExplosionThreshold")) { this.specialExplosionThreshold = compound.getInteger("SpecialExplosionThreshold"); }

        if (compound.hasKey("SubshockwavesEnabled")) { this.subshockwavesEnabled = compound.getBoolean("SubshockwavesEnabled"); }
        if (compound.hasKey("SubshockwavesLifetime")) { this.subshockwavesLifetime = compound.getInteger("SubshockwavesLifetime"); }

        if (compound.hasKey("SubshockwavesSpeedX")) { this.subshockwavesSpeedX = compound.getDouble("SubshockwavesSpeedX"); } 
        if (compound.hasKey("SubshockwavesSpeedY")) { this.subshockwavesSpeedY = compound.getDouble("SubshockwavesSpeedY"); } 
        if (compound.hasKey("SubshockwavesSpeedZ")) { this.subshockwavesSpeedZ = compound.getDouble("SubshockwavesSpeedZ"); } 

        if (compound.hasKey("SubshockwavesExplosionTimer")) { this.subshockwavesExplosionTimer = compound.getInteger("SubshockwavesExplosionTimer"); }
        if (compound.hasKey("SubshockwavesExplosionRadius")) { this.subshockwavesExplosionRadius = compound.getDouble("SubshockwavesExplosionRadius"); }
    }




    @Override
    public boolean shouldRenderInPass(int pass) 
    {
        return false;
    }


    @Override
    public boolean canRenderOnFire() 
    {
        return false;
    }

}
