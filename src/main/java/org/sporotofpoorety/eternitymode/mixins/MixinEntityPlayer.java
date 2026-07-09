package org.sporotofpoorety.eternitymode.mixins;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.Iterator;


import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityPlayer;
import org.sporotofpoorety.eternitymode.util.PlayerMeleeUtil;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;





//Mixin this class
@Mixin(value = EntityPlayer.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityPlayer implements IMixinEntityPlayer
{

    @Inject
    (
        method = "attackTargetEntityWithCurrentItem", 
        at = 
        @At
        (
            value = "INVOKE", 
            target = "Lnet/minecraft/entity/player/EntityPlayer;resetCooldown()V", 
            shift = At.Shift.BEFORE
        ),
        require = 1
    )
//Store player latest swing charge for global access
    private void capturePlayerLatestCharge(Entity targetEntity, CallbackInfo callInfo) 
    {
        EntityPlayer self = (EntityPlayer) (Object) this;


/*
//Get player's latest charge
        float latestCharge = self.getCooledAttackStrength(0.5F);
*/
//Store player's latest charge
        PlayerMeleeUtil.setLatestCharge(self);
    }

}
