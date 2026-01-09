package org.sporotofpoorety.eternitymode.core;


import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;


import org.sporotofpoorety.eternitymode.Tags;
import org.sporotofpoorety.eternitymode.entity.projectile.*;


@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class EternityModeEntityRegistry 
{

//Register unspawnable entity
//with default render tracker range and frequency
    private static <T extends Entity> void registerUnspawnable
    (
        RegistryEvent.Register<EntityEntry> event, Class<T> entityClass, String name, int id
    ) 
    {
//In the entity 
//registry event, register...
        event.getRegistry().register
        (
//An entity entry builder for this class
            EntityEntryBuilder.<T>create()
                .entity(entityClass)
                .id(new ResourceLocation(Tags.MOD_ID, name), id)
                .name(name)
                .tracker(64, 1, true)
                .build()
        );
    }

//Register spawnable entity
//with default render tracker range and frequency
    private static <T extends Entity> void registerSpawnable
    (
        RegistryEvent.Register<EntityEntry> event, Class<T> entityClass, String name, int id, int eggMainColor, int eggSubColor
    ) 
    {
//In the entity 
//registry event, register...
        event.getRegistry().register
        (
//An entity entry builder for this class
            EntityEntryBuilder.<T>create()
                .entity(entityClass)
                .id(new ResourceLocation(Tags.MOD_ID, name), id)
                .name(name)
                .egg(eggMainColor, eggSubColor)
                .tracker(64, 1, true)
                .build()
        );
    }

//Register spawnable entity
//with specified render tracker range and frequency
    private static <T extends Entity> void registerSpawnable
    (
        RegistryEvent.Register<EntityEntry> event, Class<T> entityClass, String name, int id, int eggMainColor, int eggSubColor, int trackRange, int trackFrequency
    ) 
    {
//In the entity 
//registry event, register...
        event.getRegistry().register
        (
//An entity entry builder for this class
            EntityEntryBuilder.<T>create()
                .entity(entityClass)
                .id(new ResourceLocation(Tags.MOD_ID, name), id)
                .name(name)
                .egg(eggMainColor, eggSubColor)
                .tracker(trackRange, trackFrequency, true)
                .build()
        );
    }



//IDs go up
    private static int id = 6769;
//List of entities to register
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) 
    {

//Pass this entity register event, the entity class, name and id
        registerUnspawnable(event, EntityFlameShotBurst.class, "flame_shot_burst", id++);
        registerUnspawnable(event, EntityFlameShotLinear.class, "flame_shot_linear", id++);
    }
}
