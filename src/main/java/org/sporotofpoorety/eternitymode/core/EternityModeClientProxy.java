package org.sporotofpoorety.eternitymode.core;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;


import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;


import electroblob.wizardry.Wizardry;


import org.sporotofpoorety.eternitymode.client.render.*;
import org.sporotofpoorety.eternitymode.core.EternityModeCommonProxy;
import org.sporotofpoorety.eternitymode.entity.*;
import org.sporotofpoorety.eternitymode.entity.projectile.*;




public class EternityModeClientProxy extends EternityModeCommonProxy {

    @Override
    public void registerRenderers() 
    {
        RenderingRegistry.registerEntityRenderingHandler
        (
            EntityFlameShotBouncing.class,
		        manager -> new RenderProjectileBase(manager, 0.7f, new ResourceLocation(Wizardry.MODID, "textures/entity/fireball.png"), false)
        );

        RenderingRegistry.registerEntityRenderingHandler
        (
            EntityFlameShotHoming.class,
		        manager -> new RenderProjectileBase(manager, 2.8f, new ResourceLocation(Wizardry.MODID, "textures/entity/fireball.png"), false)
        );
/*
        RenderingRegistry.registerEntityRenderingHandler
        (
            EntityFlameShotLinear.class, manager -> new RenderProjectileBaseItem<EntityFlameShotLinear>(manager, 0.5F, Items.FIRE_CHARGE)
        );
*/

		RenderingRegistry.registerEntityRenderingHandler
        (
            EntityFlameShotLinear.class,
		        manager -> new RenderProjectileBase(manager, 1.4f, new ResourceLocation(Wizardry.MODID, "textures/entity/fireball.png"), false)
        );

		RenderingRegistry.registerEntityRenderingHandler
        (
            EntityFlameShotLinearSplits.class,
		        manager -> new RenderProjectileBase(manager, 2.8f, new ResourceLocation(Wizardry.MODID, "textures/entity/fireball.png"), false)
        );

	    RenderingRegistry.registerEntityRenderingHandler
        (
            EntityThrownBlock.class, 
            manager -> new RenderThrownBlock(manager)
        );

	    RenderingRegistry.registerEntityRenderingHandler
        (
            EntityMeteorBlock.class, 
            manager -> new RenderMeteorBlock(manager)
        );

	    RenderingRegistry.registerEntityRenderingHandler
        (
            EntityMeteorBlockHoming.class, 
            manager -> new RenderMeteorBlock(manager)
        );

        RenderingRegistry.registerEntityRenderingHandler(EntityDemonScythe.class, RenderDemonScythe::new);

		RenderingRegistry.registerEntityRenderingHandler
        (
            EntityDarkShotLinear.class,
		        manager -> new RenderProjectileBase(manager, 1.4f, new ResourceLocation(Wizardry.MODID, "textures/entity/darkness_orb.png"), true)
        );
    }
}
