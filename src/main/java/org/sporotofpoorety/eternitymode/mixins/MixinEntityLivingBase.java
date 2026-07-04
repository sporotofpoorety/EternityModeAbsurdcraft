package org.sporotofpoorety.eternitymode.mixins;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import javax.annotation.Nullable;


import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLivingBase;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;





//Mixin this class
@Mixin(value = EntityLivingBase.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityLivingBase implements IMixinEntityLivingBase
{

    @Unique
    private static final DataParameter<Boolean> HAS_AFTERIMAGES = EntityDataManager.<Boolean>createKey(EntityLivingBase.class, DataSerializers.BOOLEAN);




    @Inject
    (
        method = "entityInit",
        at = @At("TAIL")
    )
//On entity init
    private void entityInitNewDataParameter(CallbackInfo callInfo)
    {
        Entity selfEntity = (Entity) (Object) this;
//Register the new data parameters
        selfEntity.getDataManager().register(HAS_AFTERIMAGES, Boolean.valueOf(false));         
    }




//New getters

    public boolean getHasAfterimages()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Boolean)selfEntity.getDataManager().get(HAS_AFTERIMAGES)).booleanValue();
    }




//New setters

    public void setHasAfterimages(boolean hasAfterimages)
    {
        Entity selfEntity = (Entity) (Object) this;
        selfEntity.getDataManager().set(HAS_AFTERIMAGES, Boolean.valueOf(hasAfterimages)); 
    }

}
