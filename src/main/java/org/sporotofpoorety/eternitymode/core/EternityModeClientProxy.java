package org.sporotofpoorety.eternitymode.core;


import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


import org.sporotofpoorety.eternitymode.client.render.RenderProjectileItem;
import org.sporotofpoorety.eternitymode.core.EternityModeCommonProxy;
import org.sporotofpoorety.eternitymode.entity.projectile.*;



public class EternityModeClientProxy extends EternityModeCommonProxy {

    @Override
    public void registerRenderers() 
    {
        RenderingRegistry.registerEntityRenderingHandler
        (
            EntityFlameShotBurst.class, manager -> new RenderProjectileItem<EntityFlameShotBurst>(manager, 2.0F, Items.FIRE_CHARGE)
        );
        RenderingRegistry.registerEntityRenderingHandler
        (
            EntityFlameShotLinear.class, manager -> new RenderProjectileItem<EntityFlameShotLinear>(manager, 0.5F, Items.FIRE_CHARGE)
        );
    }
}
