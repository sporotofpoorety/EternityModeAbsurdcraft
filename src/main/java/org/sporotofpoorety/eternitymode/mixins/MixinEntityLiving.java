package org.sporotofpoorety.eternitymode.mixins;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import javax.annotation.Nullable;


import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLiving;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;


import org.sporotofpoorety.eternitymode.client.particles.ParticleSpiral; 



//Mixin this class
@Mixin(value = EntityLiving.class, remap = true)
//Abstract since mixins should not be instantiated
public abstract class MixinEntityLiving implements IMixinEntityLiving
{

//Named this way for compatibility
    @Unique
    private static final DataParameter<Boolean> IS_ABSURDCRAFT_STUNNED = EntityDataManager.<Boolean>createKey(EntityLiving.class, DataSerializers.BOOLEAN);


//Named this way for compatibility
    @Unique private int absurdcraftStunnedTimer;




    @Inject
    (
//Inject in this method
        method = "onUpdate",
//At tail (low priority after all)
        at = @At("TAIL")
    )
    private void onUpdateStunTimer(CallbackInfo callInfo)
    {
        if(this.absurdcraftStunnedTimer > 0)
        {
            Entity selfEntity = (Entity) (Object) this;

            if((selfEntity.ticksExisted % 9) == 0)
            {
                for (int angleStepAt = 0; angleStepAt < 9; angleStepAt++) 
                {
                    Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSpiral(selfEntity.world, 9,
                    selfEntity.posX, selfEntity.posY + (double) (selfEntity.height * 1.15F), selfEntity.posZ, selfEntity.posX, selfEntity.posZ, 
                    65, 40, angleStepAt, (double) (selfEntity.width * 1.15F), 0.0D));
                }
            }

            
            if(--absurdcraftStunnedTimer <= 0)
            {
                this.setAbsurdcraftStunned(false);
            }
        }
    }



    @WrapWithCondition
    (
        method = "despawnEntity",
        at = 
        @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLiving;setDead()V"
        ),
//Slice
        slice = @Slice(
//From this reference point
            from = 
            @At
            (
                value = "CONSTANT",
                args = "doubleValue=16384.0D"
            ),
//To another, later reference point
            to = 
            @At
            (
                value = "CONSTANT",
                args = "doubleValue=1024.0D"
            )
        )
    )
    private boolean flyingHelperLiving(EntityLiving self) 
    {
        return false;
    }



    @Inject
    (
//Inject in this method
        method = "entityInit",
//At tail
        at = @At("TAIL")
    )
//On entity init
    private void entityInitNewDataParameter(CallbackInfo callInfo)
    {
        Entity selfEntity = (Entity) (Object) this;
//Register the new data parameter
        selfEntity.getDataManager().register(IS_ABSURDCRAFT_STUNNED, Boolean.valueOf(false));         
    }


    @Inject
    (
        method = "writeEntityToNBT",
        at = @At("TAIL")
    )
    private void writeNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
//New NBT below
        compound.setBoolean("AbsurdcraftStunned", this.getAbsurdcraftStunned());

        compound.setInteger("AbsurdcraftStunnedTimer", this.getAbsurdcraftStunnedTimer());
    }


    @Inject
    (
        method = "readEntityFromNBT",
        at = @At("TAIL")
    )
    private void readNewNBT(NBTTagCompound compound, CallbackInfo callInfo)
    {
//New NBT below
        if (compound.hasKey("AbsurdcraftStunned")) {
            this.setAbsurdcraftStunned(compound.getBoolean("AbsurdcraftStunned"));
        }

        if (compound.hasKey("AbsurdcraftStunnedTimer")) {
            this.setAbsurdcraftStunnedTimer(compound.getInteger("AbsurdcraftStunnedTimer"));
        }
    }




//New getters


//Named this way for compatibility
    public boolean getAbsurdcraftStunned()
    {
        Entity selfEntity = (Entity) (Object) this;
        return ((Boolean)selfEntity.getDataManager().get(IS_ABSURDCRAFT_STUNNED)).booleanValue();
    }

    public int getAbsurdcraftStunnedTimer()
    {
        return this.absurdcraftStunnedTimer;
    }


//New setters


    public void setAbsurdcraftStunned(boolean isStunned)
    {
        Entity selfEntity = (Entity) (Object) this;
        selfEntity.getDataManager().set(IS_ABSURDCRAFT_STUNNED, Boolean.valueOf(isStunned)); 
    }

    public void setAbsurdcraftStunnedTimer(int time)
    {
        this.absurdcraftStunnedTimer = time;
    }

}
