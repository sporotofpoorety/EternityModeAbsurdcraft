package org.sporotofpoorety.eternitymode.util;


import net.minecraft.entity.Entity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import electroblob.wizardry.util.ParticleBuilder;




@SideOnly(Side.CLIENT)
public class ParticleUtil 
{

    public static void particlesFireball(Entity fireball,
    int particleLifetime, int particleDensity, double particleVelocity)
    {
    	for(int i = 0; i < particleDensity; i++)
        {
			if(fireball.ticksExisted > 1)
            {
				double dx = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
				double dy = (fireball.world.rand.nextDouble() - 0.5) * fireball.height + (fireball.height / 2) - 0.1;
				double dz = (fireball.world.rand.nextDouble() - 0.5) * fireball.width;
			    double v = particleVelocity;
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE)
						.pos(fireball.getPositionVector().add(dx - fireball.motionX, dy, dz - fireball.motionZ))
					    .vel(-v * dx, -v * dy, -v * dz).scale(fireball.width * 2).time(particleLifetime).spawn(fireball.world);
			}
		}
    }

}
