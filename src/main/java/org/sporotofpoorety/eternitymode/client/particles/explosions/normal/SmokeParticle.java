package org.sporotofpoorety.eternitymode.client.particles.explosions.normal;


import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import org.sporotofpoorety.eternitymode.config.ExplosiveEnhancementConfig;
import org.sporotofpoorety.eternitymode.core.EternityModeTextureRegistry;




@SideOnly(Side.CLIENT)
public class SmokeParticle extends Particle {
    public SmokeParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double velX, double velY,
                         double velZ, double power) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);

        this.particleScale = (float) power * 1.75f
                * (float) ExplosiveEnhancementConfig.smokeScale;

        this.particleMaxAge = (int) (this.rand.nextInt(35) + power * (this.rand.nextInt(20) + 3));
        this.particleMaxAge = Math.max(1, (int) (this.particleMaxAge
                / ExplosiveEnhancementConfig.smokeSpeed));

        this.motionX = velX;
        this.motionY = velY;
        this.motionZ = velZ;

        this.setParticleTexture(EternityModeTextureRegistry.SMOKE_SPRITES[0]);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        } else {
            if (this.particleAge == 12) {
                this.motionX = 0;
                this.motionY = 0.05D;
                this.motionZ = 0;
            }
            this.move(this.motionX, this.motionY, this.motionZ);

            int frame = (int) (((float) this.particleAge / this.particleMaxAge)
                    * EternityModeTextureRegistry.SMOKE_SPRITES.length);
            if (frame >= EternityModeTextureRegistry.SMOKE_SPRITES.length) {
                frame = EternityModeTextureRegistry.SMOKE_SPRITES.length - 1;
            }
            this.setParticleTexture(EternityModeTextureRegistry.SMOKE_SPRITES[frame]);
        }
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
