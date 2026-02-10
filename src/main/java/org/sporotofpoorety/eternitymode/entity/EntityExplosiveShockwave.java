package org.sporotofpoorety.eternitymode.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;


import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;


import org.sporotofpoorety.eternitymode.entity.projectile.EntityFlameShotLinear;




public class EntityExplosiveShockwave extends Entity 
{

    EntityLivingBase owner;
    int lifetimeTicks;
    double speedX;
    double speedY;
    double speedZ;
    double accelerationRate;
    double accelerationCurrent;

    boolean oscillationEnabled;
    double oscillationDistance;
    double oscillationX;
    double oscillationZ;
    int oscillationOrientationDuration;
    int oscillationOrientationProgress;
    boolean oscillationOrientationCurrentlyPositive;
 
    int explosionTimer;
    float explosionRadius;
    int specialExplosionCounter;
    int specialExplosionThreshold;
    boolean fireParticlesSpawned;
    boolean setsFire;


    boolean fireballEnabled;
    int fireballAmount;
    
    int fireballMaxLifetime;

    double fireballSpeedHorizontal; 
    double fireballSpeedVertical;
    double fireballAccelerationRate; 
    double fireballGravitySpeed; 

    double fireballHitCheckSize; 
    boolean fireballProjectileStopsAtEntity; 
    boolean fireballProjectileStopsAtBlock;
    float fireballProjectileHitDamage;


    int fireballFireDuration;

    boolean subshockwavesEnabled;
    double subshockwavesSpeedX;
    double subshockwavesSpeedY;
    double subshockwavesSpeedZ;
    double subshockwavesAccelerationRate;
 
    int subshockwavesExplosionTimer;
    float subshockwavesExplosionRadius;
    boolean subshockwavesSetsFire;




    public EntityExplosiveShockwave(World world) 
    {
        super(world);
        setSize(0.5F, 0.5F);
    }

    public EntityExplosiveShockwave(World world, EntityLivingBase owner, double x, double y, double z, 
    int lifetimeTicks, boolean hasGravity, float shockwaveStepHeight, double speedX, double speedY, double speedZ, double accelerationRate,
    boolean oscillationEnabled, double oscillationDistance, int oscillationOrientationDuration,
    int explosionTimer, float explosionRadius, int specialExplosionThreshold, 
    boolean setsFire, boolean fireballEnabled, int fireballAmount,
    int fireballMaxLifetime, double fireballSpeedHorizontal, double fireballSpeedVertical,
    double fireballAccelerationRate, double fireballGravitySpeed,  
    double fireballHitCheckSize, boolean fireballProjectileStopsAtEntity, boolean fireballProjectileStopsAtBlock, float fireballProjectileHitDamage,
    int fireballFireDuration, boolean subshockwavesEnabled,
    double subshockwavesSpeedX, double subshockwavesSpeedY, double subshockwavesSpeedZ, double subshockwavesAccelerationRate,
    int subshockwavesExplosionTimer, float subshockwavesExplosionRadius, boolean subshockwavesSetsFire) 
    {
        this(world);
        this.owner = owner;
        setPosition(x, y, z);
        this.lifetimeTicks = lifetimeTicks;
        this.setNoGravity(!hasGravity);
        this.stepHeight = shockwaveStepHeight;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.accelerationRate = accelerationRate;
        this.accelerationCurrent = accelerationRate;

//Oscillation enabled
        this.oscillationEnabled = oscillationEnabled;
//Distance from one end to the other
        this.oscillationDistance = oscillationDistance;
//Oscillate sideways
        this.oscillationX = Math.cos(Math.atan2(speedZ, speedX) + (0.5D * Math.PI));
        this.oscillationZ = Math.sin(Math.atan2(speedZ, speedX) + (0.5D * Math.PI));
//Make sure oscillation duration is odd
//This is the duration to go from one end to the other
        this.oscillationOrientationDuration = oscillationOrientationDuration; 
        if ((oscillationOrientationDuration % 2) == 0) { oscillationOrientationDuration++; }
//Oscillation progress starts 
//in the middle, but it can be overriden by NBT
        this.oscillationOrientationProgress = (oscillationOrientationDuration / 2) + 1;
//Oscillation starts either positive or negative randomly
        int orientationStartsNegOrPos = this.rand.nextInt(2);
        this.oscillationOrientationCurrentlyPositive = (orientationStartsNegOrPos > 0) ? true : false; 

        this.explosionTimer = explosionTimer;
        this.explosionRadius = explosionRadius;
        this.fireParticlesSpawned = false;
        this.setsFire = setsFire;


        this.fireballEnabled = fireballEnabled;
        this.fireballAmount = fireballAmount;

        this.fireballMaxLifetime = fireballMaxLifetime;

        this.fireballSpeedHorizontal = fireballSpeedHorizontal; 
        this.fireballSpeedVertical = fireballSpeedVertical;
        this.fireballAccelerationRate = fireballAccelerationRate;
        this.fireballGravitySpeed = fireballGravitySpeed;

        this.fireballHitCheckSize = fireballHitCheckSize; 
        this.fireballProjectileStopsAtEntity = fireballProjectileStopsAtEntity; 
        this.fireballProjectileStopsAtBlock = fireballProjectileStopsAtBlock;
        this.fireballProjectileHitDamage = fireballProjectileHitDamage;

        this.fireballFireDuration = fireballFireDuration;

        this.subshockwavesEnabled = subshockwavesEnabled;
        this.subshockwavesSpeedX = subshockwavesSpeedX;
        this.subshockwavesSpeedY = subshockwavesSpeedY;
        this.subshockwavesSpeedZ = subshockwavesSpeedZ;
        this.subshockwavesAccelerationRate = subshockwavesAccelerationRate;
     
        this.subshockwavesExplosionTimer = subshockwavesExplosionTimer;
        this.subshockwavesExplosionRadius = subshockwavesExplosionRadius;
        this.subshockwavesSetsFire = subshockwavesSetsFire;
	}


    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() 
    {
        super.onUpdate();


        if (this.ticksExisted >= this.lifetimeTicks) 
        {
            setDead();
            return;
        }


//Yes, this has to be set manually
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;


/*
        if(!this.fireParticlesSpawned && world.isRemote)
        {
	        for(int i = 0; i < 50; i++)
            {
		        float r = world.rand.nextFloat();
		        double speed = 0.02/r * (1 + world.rand.nextDouble());//(world.rand.nextBoolean() ? 1 : -1) * (0.05 + 0.02 * world.rand.nextDouble());
		        ParticleBuilder.create(Type.MAGIC_FIRE)
			            .entity(this)
                        .pos(0, world.rand.nextDouble() * 3, 0)
			            .vel(0, 0, 0)
			            .scale(2)
			            .time(this.lifetimeTicks + world.rand.nextInt(10))
			            .spin(world.rand.nextDouble() * (this.explosionRadius - 0.5) + 0.5, speed)
			            .spawn(world);
	        }

	        for(int i = 0; i < 30; i++)
            {
		        float r = world.rand.nextFloat();
		        double speed = 0.02/r * (1 + world.rand.nextDouble());//(world.rand.nextBoolean() ? 1 : -1) * (0.05 + 0.02 * world.rand.nextDouble());
		        ParticleBuilder.create(Type.CLOUD)
                        .entity(this)
				        .pos(0, world.rand.nextDouble() * 2.5, 0)
				        .clr(DrawingUtils.mix(DrawingUtils.mix(0xffbe00, 0xff3600, r/0.6f), 0x222222, (r - 0.6f)/0.4f))
                        .time(this.lifetimeTicks + world.rand.nextInt(10))
				        .spin(r * (this.explosionRadius - 1) + 0.5, speed)
				        .spawn(world);
	        }

            this.fireParticlesSpawned = true;
        }
*/




        if (!world.isRemote) 
        {
//Periodic explosions
            if((this.ticksExisted % this.explosionTimer) == 0)
            {
                Entity entityResponsible = (this.owner != null) ? this.owner : this;

                this.world.newExplosion(entityResponsible, this.posX, this.posY + (this.explosionRadius / 1.5F), this.posZ, this.explosionRadius, this.setsFire, false);

//Increment explosion counter
                ++this.specialExplosionCounter;


//If at special explosion threshold
                if(specialExplosionCounter >= specialExplosionThreshold)
                {
//Reset explosion counter
                    this.specialExplosionCounter = 0;

//Do special explosion

//With potentially fireballs
                    if(this.fireballEnabled)
                    {
//Random initial angle for fireballs
                        double fireballRandomStartRadians = (double) rand.nextFloat() * (2 * Math.PI);

                        for(int fireballAt = 0; fireballAt < this.fireballAmount; fireballAt++)
                        {
                            if(this.owner != null)
                            {
//Spin that angle around for each fireball
//PS: I got screwed over by integer and double division again :(
                                double fireballCurrentRadians = fireballRandomStartRadians + (2.0D * Math.PI * fireballAt / fireballAmount);


                                EntityFlameShotLinear flameShot = new EntityFlameShotLinear
                                (
                                    this.world, this.owner,
                                    this.posX, this.posY, this.posZ,
                                    this.fireballMaxLifetime, 
                                    this.fireballSpeedHorizontal * Math.cos(fireballCurrentRadians) * (1.0D + (double) (rand.nextFloat() * 0.5F)), 
                                    this.fireballSpeedVertical * (1.0D + (double) (rand.nextFloat() * 0.2F)),
                                    this.fireballSpeedHorizontal * Math.sin(fireballCurrentRadians) * (1.0D + (double) (rand.nextFloat() * 0.5F)),
                                    this.fireballAccelerationRate, this.fireballGravitySpeed, 
                                    this.fireballHitCheckSize, this.fireballProjectileStopsAtEntity, 
                                    this.fireballProjectileStopsAtBlock, this.fireballProjectileHitDamage, 
                                    3, 2, 0.06D,
//Fireballs go outwards + up with slight randomization
                                    this.fireballFireDuration, false, 0.5F, false, false 
                                );

//Have to set flameshot to be at shockwave and not "true" owner
                                flameShot.setPosition
                                (
//Spin fireball origins around perimeter of explosion
                                    this.posX + (Math.cos(fireballCurrentRadians) * (double) (this.explosionRadius)),
                                    this.posY + 0.5D,
                                    this.posZ + (Math.sin(fireballCurrentRadians) * (double) (this.explosionRadius))
                                );

                                this.world.spawnEntity(flameShot);
                            }
                        }
                    }
//Potentially sub-shockwaves too
                    if(this.subshockwavesEnabled)
                    {
                        if(this.owner != null)
                        {
                            EntityExplosiveShockwave splitShockwave = new EntityExplosiveShockwave(this.world, this.owner, this.posX, this.posY, this.posZ, 
                            50, false, 3.0F, this.subshockwavesSpeedX, this.subshockwavesSpeedY, this.subshockwavesSpeedZ, this.subshockwavesAccelerationRate,
                            false, 3.0D, 9,
                            this.subshockwavesExplosionTimer, this.subshockwavesExplosionRadius, 69420,
                            this.subshockwavesSetsFire, false, 8,
                            200, 1.0D, 1.0D,
                            0.08D, 1.01D,  
                            0.3D, true, true, 5.0F,
                            20, false,
                            0.0D, 0.0D, 0.0D, 1.0D,
                            10, 3.0F, false);

		                    splitShockwave.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);

		                    this.getEntityWorld().spawnEntity(splitShockwave);
                        } 
                    }
                }
            }




            if(!this.hasNoGravity())
            {
                this.move(MoverType.SELF, 0.0D, -0.08D, 0.0D);
            }


//Shockwave motion
            this.motionX = this.speedX * this.accelerationCurrent;
            this.motionY = this.speedY * this.accelerationCurrent;
            this.motionZ = this.speedZ * this.accelerationCurrent;

/*
//Have to specify motion myself yes
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
*/


            this.move(MoverType.SELF, motionX, motionY, motionZ);




//If oscillation enabled
            if(this.oscillationEnabled)
            {
//Move positively or negatively
                if(this.oscillationOrientationCurrentlyPositive)
                {
//Distance divided by duration, times oscillation cos and sin
                    this.move(MoverType.SELF, (oscillationDistance / oscillationOrientationDuration) * oscillationX, 
                        0.0D, (oscillationDistance / oscillationOrientationDuration) * oscillationZ);                
                }
                else
                {
                    this.move(MoverType.SELF, -1.0D * (oscillationDistance / oscillationOrientationDuration) * oscillationX, 
                        0.0D, -1.0D * (oscillationDistance / oscillationOrientationDuration) * oscillationZ);       
                }


//Increment oscillation orientation progress
                if(this.oscillationOrientationProgress < oscillationOrientationDuration)
                {
                    oscillationOrientationProgress++;
                }
//If at max invert orientation positivity
                else
                {
                    oscillationOrientationCurrentlyPositive = !oscillationOrientationCurrentlyPositive;
//And reset oscillation orientation progress
                    oscillationOrientationProgress = 1;
                }
            }
            



/*
//Setting position too yes
            this.setPosition(this.posX, this.posY, this.posZ);
*/


//Increase acceleration
            this.accelerationCurrent *= this.accelerationRate;
        }
    }




    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("Lifetime", lifetimeTicks);
        compound.setBoolean("HasGravity", !this.hasNoGravity());
        compound.setFloat("ShockwaveStepHeight", this.stepHeight);
        compound.setDouble("SpeedX", this.speedX);
        compound.setDouble("SpeedY", this.speedY);
        compound.setDouble("SpeedZ", this.speedZ);
        compound.setDouble("AccelerationRate", this.accelerationRate);
        compound.setDouble("AccelerationCurrent", this.accelerationCurrent);

        compound.setBoolean("OscillationEnabled", this.oscillationEnabled);
        compound.setDouble("OscillationDistance", this.oscillationDistance);
        compound.setInteger("OscillationOrientationDuration", this.oscillationOrientationDuration);
        compound.setInteger("OscillationOrientationProgress", this.oscillationOrientationProgress);
        compound.setBoolean("OscillationOrientationCurrentlyPositive", this.oscillationOrientationCurrentlyPositive);

        compound.setInteger("ExplosionTimer", this.explosionTimer);
        compound.setFloat("ExplosionRadius", this.explosionRadius);
        compound.setInteger("SpecialExplosionCounter", this.specialExplosionCounter);
        compound.setInteger("SpecialExplosionThreshold", this.specialExplosionThreshold);
        compound.setBoolean("FireParticlesSpawned", this.fireParticlesSpawned);
        compound.setBoolean("SetsFire", this.setsFire);


        compound.setBoolean("FireballEnabled", this.fireballEnabled);
        compound.setInteger("FireballAmount", this.fireballAmount);

        compound.setInteger("FireballMaxLifetime", this.fireballMaxLifetime);

        compound.setDouble("FireballSpeedHorizontal", this.fireballSpeedHorizontal);
        compound.setDouble("FireballSpeedVertical", this.fireballSpeedVertical);
        compound.setDouble("FireballAccelerationRate", this.fireballAccelerationRate);
        compound.setDouble("FireballGravitySpeed", this.fireballGravitySpeed);

        compound.setDouble("FireballHitCheckSize", this.fireballHitCheckSize);
        compound.setBoolean("FireballProjectileStopsAtEntity", this.fireballProjectileStopsAtEntity);
        compound.setBoolean("FireballProjectileStopsAtBlock", this.fireballProjectileStopsAtBlock);
        compound.setFloat("FireballProjectileHitDamage", this.fireballProjectileHitDamage);

        compound.setInteger("FireballFireDuration", this.fireballFireDuration);

        compound.setBoolean("SubshockwavesEnabled", this.subshockwavesEnabled);
        compound.setDouble("SubshockwavesSpeedX", this.subshockwavesSpeedX);
        compound.setDouble("SubshockwavesSpeedY", this.subshockwavesSpeedY);
        compound.setDouble("SubshockwavesSpeedZ", this.subshockwavesSpeedZ);
        compound.setDouble("SubshockwavesAccelerationRate", this.subshockwavesAccelerationRate);
     
        compound.setInteger("SubshockwavesExplosionTimer", this.subshockwavesExplosionTimer);
        compound.setFloat("SubshockwavesExplosionRadius", this.subshockwavesExplosionRadius);
        compound.setBoolean("SubshockwavesSetsFire", this.subshockwavesSetsFire);
    }


    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("Lifetime")) { lifetimeTicks = compound.getInteger("Lifetime"); }
        if (compound.hasKey("HasGravity")) { this.setNoGravity(!compound.getBoolean("HasGravity")); }
        if (compound.hasKey("ShockwaveStepHeight")) { this.stepHeight = compound.getFloat("ShockwaveStepHeight"); }
        if (compound.hasKey("SpeedX")) { this.speedX = compound.getDouble("SpeedX"); }
        if (compound.hasKey("SpeedY")) { this.speedY = compound.getDouble("SpeedY"); }
        if (compound.hasKey("SpeedZ")) { this.speedZ = compound.getDouble("SpeedZ"); }
        if (compound.hasKey("AccelerationRate")) { this.accelerationRate = compound.getDouble("AccelerationRate"); }
        if (compound.hasKey("AccelerationCurrent")) { this.accelerationCurrent = compound.getDouble("AccelerationCurrent"); }

        if (compound.hasKey("OscillationEnabled")) { this.oscillationEnabled = compound.getBoolean("OscillationEnabled"); }
        if (compound.hasKey("OscillationDistance")) { this.oscillationDistance = compound.getDouble("OscillationDistance"); }
        this.oscillationX = Math.cos(Math.atan2(this.speedZ, this.speedX) + (0.5D * Math.PI));
        this.oscillationZ = Math.sin(Math.atan2(this.speedZ, this.speedX) + (0.5D * Math.PI));
        if (compound.hasKey("OscillationOrientationDuration")) { this.oscillationOrientationDuration = compound.getInteger("OscillationOrientationDuration"); }
        if (compound.hasKey("OscillationOrientationProgress")) { this.oscillationOrientationProgress = compound.getInteger("OscillationOrientationProgress"); }
        if (compound.hasKey("OscillationOrientationCurrentlyPositive")) 
            { this.oscillationOrientationCurrentlyPositive = compound.getBoolean("OscillationOrientationCurrentlyPositive"); }

        if (compound.hasKey("ExplosionTimer")) { this.explosionTimer = compound.getInteger("ExplosionTimer"); }
        if (compound.hasKey("ExplosionRadius")) { this.explosionRadius = compound.getFloat("ExplosionRadius"); }
        if (compound.hasKey("SpecialExplosionCounter")) { this.specialExplosionCounter = compound.getInteger("SpecialExplosionCounter"); }
        if (compound.hasKey("SpecialExplosionThreshold")) { this.specialExplosionThreshold = compound.getInteger("SpecialExplosionThreshold"); }
        if (compound.hasKey("FireParticlesSpawned")) { this.fireParticlesSpawned = compound.getBoolean("FireParticlesSpawned"); }
        if (compound.hasKey("SetsFire")) { this.setsFire = compound.getBoolean("SetsFire"); }


        if (compound.hasKey("FireballEnabled")) { this.fireballEnabled = compound.getBoolean("FireballEnabled"); }
        if (compound.hasKey("FireballAmount")) { this.fireballAmount = compound.getInteger("FireballAmount"); }

        if (compound.hasKey("FireballMaxLifetime")) { this.fireballMaxLifetime = compound.getInteger("FireballMaxLifetime"); }

        if (compound.hasKey("FireballSpeedHorizontal")) { this.fireballSpeedHorizontal = compound.getDouble("FireballSpeedHorizontal"); }
        if (compound.hasKey("FireballSpeedVertical")) { this.fireballSpeedVertical = compound.getDouble("FireballSpeedVertical"); }
        if (compound.hasKey("FireballAccelerationRate")) { this.fireballAccelerationRate = compound.getDouble("FireballAccelerationRate"); }
        if (compound.hasKey("FireballGravitySpeed")) { this.fireballGravitySpeed = compound.getDouble("FireballGravitySpeed"); }

        if (compound.hasKey("FireballHitCheckSize")) { this.fireballHitCheckSize = compound.getDouble("FireballHitCheckSize"); } 
        if (compound.hasKey("FireballProjectileStopsAtEntity")) { this.fireballProjectileStopsAtEntity = compound.getBoolean("FireballProjectileStopsAtEntity"); }
        if (compound.hasKey("FireballProjectileStopsAtBlock")) { this.fireballProjectileStopsAtBlock = compound.getBoolean("FireballProjectileStopsAtBlock"); }
        if (compound.hasKey("FireballProjectileHitDamage")) { this.fireballProjectileHitDamage = compound.getFloat("FireballProjectileHitDamage"); }

        if (compound.hasKey("FireballFireDuration")) { this.fireballFireDuration = compound.getInteger("FireballFireDuration"); }

        if (compound.hasKey("SubshockwavesEnabled")) { this.subshockwavesEnabled = compound.getBoolean("SubshockwavesEnabled"); }
        if (compound.hasKey("SubshockwavesSpeedX")) { this.subshockwavesSpeedX = compound.getDouble("SubshockwavesSpeedX"); } 
        if (compound.hasKey("SubshockwavesSpeedY")) { this.subshockwavesSpeedY = compound.getDouble("SubshockwavesSpeedY"); } 
        if (compound.hasKey("SubshockwavesSpeedZ")) { this.subshockwavesSpeedZ = compound.getDouble("SubshockwavesSpeedZ"); } 
        if (compound.hasKey("SubshockwavesAccelerationRate")) { this.subshockwavesAccelerationRate = compound.getDouble("SubshockwavesAccelerationRate"); } 

        if (compound.hasKey("SubshockwavesExplosionTimer")) { this.subshockwavesExplosionTimer = compound.getInteger("SubshockwavesExplosionTimer"); }
        if (compound.hasKey("SubshockwavesExplosionRadius")) { this.subshockwavesExplosionRadius = compound.getFloat("SubshockwavesExplosionRadius"); }
        if (compound.hasKey("SubshockwavesSetsFire")) { this.subshockwavesSetsFire = compound.getBoolean("SubshockwavesSetsFire"); }
    }
}
