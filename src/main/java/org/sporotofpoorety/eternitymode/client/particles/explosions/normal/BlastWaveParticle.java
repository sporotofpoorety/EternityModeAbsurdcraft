package org.sporotofpoorety.eternitymode.client.particles.explosions.normal;


import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import org.sporotofpoorety.eternitymode.config.ExplosiveEnhancementConfig;
import org.sporotofpoorety.eternitymode.core.EternityModeTextureRegistry;




@SideOnly(Side.CLIENT)
public class BlastWaveParticle extends Particle {
    private final double initialScale;

    public BlastWaveParticle(
            World world,
            double x, double y, double z,
            double velX, double velY, double velZ
    ) {
        super(world, x, y + 0.5D, z);

        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        this.particleScale = (float) velX * (float) ExplosiveEnhancementConfig.blastWaveScale;
        this.initialScale = this.particleScale;
        this.particleMaxAge = Math.max(1, (int) ((15 + (Math.floor(velX / 5))) / ExplosiveEnhancementConfig.blastWaveSpeed));
        this.particleAlpha = 1.0F;

        this.setParticleTexture(EternityModeTextureRegistry.BLASTWAVE_SPRITES[0]);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        } else {
            int frame = (int) (((float) this.particleAge / this.particleMaxAge) * EternityModeTextureRegistry.BLASTWAVE_SPRITES.length);

            if (frame >= EternityModeTextureRegistry.BLASTWAVE_SPRITES.length) {
                frame = EternityModeTextureRegistry.BLASTWAVE_SPRITES.length - 1;
            }

            this.setParticleTexture(EternityModeTextureRegistry.BLASTWAVE_SPRITES[frame]);
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f4 = this.particleScale;

        float f = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float f1 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float f2 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        float u1 = this.particleTexture.getMinU();
        float u2 = this.particleTexture.getMaxU();

        float v1 = this.particleTexture.getMinV();
        float v2 = this.particleTexture.getMaxV();

        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;

        buffer.pos((double) f - f4, (double) f1, (double) f2 - f4).tex((double) u2, (double) v2)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(j, k)
                .endVertex();
        buffer.pos((double) f - f4, (double) f1, (double) f2 + f4).tex((double) u2, (double) v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(j, k)
                .endVertex();
        buffer.pos((double) f + f4, (double) f1, (double) f2 + f4).tex((double) u1, (double) v1)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(j, k)
                .endVertex();
        buffer.pos((double) f + f4, (double) f1, (double) f2 - f4).tex((double) u1, (double) v2)
                .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
                .lightmap(j, k)
                .endVertex();
    }
}
