package org.sporotofpoorety.eternitymode.mixins;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import javax.annotation.Nullable;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;


import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;


import org.sporotofpoorety.eternitymode.entity.EntityExplosiveShockwave;
import org.sporotofpoorety.eternitymode.util.AbsurdcraftMathUtils;




//Mixin this class
@Mixin(value = EntityShyco.class, remap = false)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityShyco
{


    @Inject
    (
        method = "spawnShock",
        at = @At("HEAD"),
        cancellable = true
    )
//Different shockwave entirely
    private void overridePriLongarmsShockwave(CallbackInfo callInfo) 
    {
        EntityShyco self = (EntityShyco) (Object) this;
        Entity selfEntity = (Entity) (Object) this;

        if(!selfEntity.world.isRemote)
        {
            EntityLiving selfEntityLiving = (EntityLiving) (Object) this;

            EntityLivingBase attackTarget = selfEntityLiving.getAttackTarget();


            if (attackTarget != null)
            {
//Predictive aim
                Vec3d predictiveAim = AbsurdcraftMathUtils.generatePredictiveAimVectorNoVertical(selfEntity.posX, selfEntity.posY, selfEntity.posZ, attackTarget, 2.0D);

                double baseRadians = Math.atan2(predictiveAim.z, predictiveAim.x);


                for(int angleAt = -1; angleAt <= 1; angleAt++)
                {
                    EntityExplosiveShockwave shockwave = new EntityExplosiveShockwave(selfEntity.world, self, selfEntity.posX, selfEntity.posY, selfEntity.posZ, 
                    50, true, 3.0F, 2.0D * Math.cos(baseRadians + (Math.PI * 0.25D * angleAt)), 0.0D, 2.0D * Math.sin(baseRadians + (Math.PI * 0.25D * angleAt)), 1.0D,
                    true, 3.0D, 15, 
                    2, 3.0F, 6, false, false, 8,
                    100, 0.15D, 1.5D,
                    1.0D, 0.0D,
                    0.3D, true, true, 5.0F,
                    20, false,
                    0.0D, 4.0D, 0.0D, 1.01D,
                    10, 3.0F, false);
	                shockwave.setLocationAndAngles(selfEntity.posX, selfEntity.posY, selfEntity.posZ, selfEntity.rotationYaw, 0.0F);

	                selfEntity.getEntityWorld().spawnEntity(shockwave);
                }
            }
        } 


        callInfo.cancel();
    }
}
