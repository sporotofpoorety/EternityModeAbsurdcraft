package org.sporotofpoorety.eternitymode.mixins;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import javax.annotation.Nullable;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;


import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;




//Mixin this class
@Mixin(value = EntityPCosmical.class, remap = false)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityPCosmical
{

/*
    @WrapWithCondition
    (
        method = "func_70097_a",
        at = 
        @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLivingBase;func_70097_a(Lnet/minecraft/util/DamageSource;F)Z",
            ordinal = 1
        ),
        require = 1
    )
    private boolean cosmicalNoReflectDamage(EntityLivingBase target, DamageSource source, float amount) 
    {
        return false;
    }


    @WrapWithCondition
    (
        method = "func_70097_a",
        at = 
        @At
        (
            value = "INVOKE",
            target = "Lcom/dhanantry/scapeandrunparasites/entity/ai/misc/EntityPCosmical;attackEntityAsMobMinimum(Lnet/minecraft/entity/EntityLivingBase;F)Z"
        ),
        require = 1
    )
    private boolean cosmicalNoReflectDamageMinimum(EntityPCosmical self, EntityLivingBase target, float amount) 
    {
        return false;
    }
*/
}
