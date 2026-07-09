package org.sporotofpoorety.eternitymode.core;


import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.sporotofpoorety.eternitymode.Tags;




public class EternityModeSoundEvents 
{
	public static final SoundEvent ENTITY_BLASTER_CHARGING = createSoundEvent("entity.blastercharging");	
	public static final SoundEvent ENTITY_BLASTER_SOUND = createSoundEvent("entity.blastersound");
	public static final SoundEvent ENTITY_DIZZY = createSoundEvent("entity.dizzy");
	public static final SoundEvent ENTITY_DIZZY_BOSS = createSoundEvent("entity.dizzyboss");
	public static final SoundEvent ENTITY_GAUSS_NUKE = createSoundEvent("entity.gaussnuke");
	public static final SoundEvent ENTITY_SCYTHE_SWING = createSoundEvent("entity.scytheswing");
	public static final SoundEvent ENTITY_SLAM_EXPLOSION = createSoundEvent("entity.slamexplosion");
	public static final SoundEvent ENTITY_STAR_WINDUP = createSoundEvent("entity.starwindup");
	public static final SoundEvent ENTITY_TACTICAL_NUKE = createSoundEvent("entity.tacticalnuke");
	public static final SoundEvent ENTITY_BRAINSLIME_CHARGE = createSoundEvent("entity.brainslime.slimecharge");
	public static final SoundEvent ENTITY_CREEPER_ANNOYED = createSoundEvent("entity.creeper.annoyed");
	public static final SoundEvent ENTITY_CREEPER_BUFF_ATTEMPT = createSoundEvent("entity.creeper.buffattempt");
	public static final SoundEvent ENTITY_CREEPER_BUFF_EXECUTE = createSoundEvent("entity.creeper.buffexecute");
	public static final SoundEvent ENTITY_CREEPER_ITEMBOX = createSoundEvent("entity.creeper.itembox");
	public static final SoundEvent ENTITY_CREEPER_NUKE = createSoundEvent("entity.creeper.nuke");
	public static final SoundEvent ENTITY_CREEPER_PARTY = createSoundEvent("entity.creeper.party");
	public static final SoundEvent ENTITY_FLAMESPEWER_IDLE = createSoundEvent("entity.flamespewer.idle");
	public static final SoundEvent ENTITY_GROVESPRITE_ANGRY = createSoundEvent("entity.grovesprite.angry");
	public static final SoundEvent ENTITY_GROVESPRITE_DEATH = createSoundEvent("entity.grovesprite.death");
	public static final SoundEvent ENTITY_GROVESPRITE_HURT = createSoundEvent("entity.grovesprite.hurt");
	public static final SoundEvent ENTITY_GROVESPRITE_IDLE = createSoundEvent("entity.grovesprite.idle");
	public static final SoundEvent ENTITY_GROVESPRITE_THANKS = createSoundEvent("entity.grovesprite.thanks");
	public static final SoundEvent ENTITY_HARPY_HURT = createSoundEvent("entity.harpy.hurt");
	public static final SoundEvent ENTITY_HARPY_IDLE = createSoundEvent("entity.harpy.idle");
	public static final SoundEvent ENTITY_MOTHERSPIDER_SCREECH = createSoundEvent("entity.motherspider.spiderscreech");
	public static final SoundEvent ENTITY_QUAZAR_LANDING_EXPLOSION = createSoundEvent("entity.quazar.landingexplosion");
	public static final SoundEvent ENTITY_QUAZAR_LEAP_WHOOSH = createSoundEvent("entity.quazar.leapwhoosh");
	public static final SoundEvent ENTITY_QUAZAR_NUKE_ALARM = createSoundEvent("entity.quazar.nukealarm");
	public static final SoundEvent ENTITY_TROLLAGER_ATTACK = createSoundEvent("entity.trollager.attack");
	public static final SoundEvent ENTITY_TROLLAGER_DEATH = createSoundEvent("entity.trollager.death");
	public static final SoundEvent ENTITY_TROLLAGER_HIT = createSoundEvent("entity.trollager.hit");
	public static final SoundEvent ENTITY_TROLLAGER_IDLE = createSoundEvent("entity.trollager.idle");
	public static final SoundEvent ENTITY_VOIDEYE_IDLE = createSoundEvent("entity.voideye.idle");

	
	private static SoundEvent createSoundEvent(final String soundName) 
    {
		final ResourceLocation soundID = new ResourceLocation(Tags.MOD_ID, soundName);
		return new SoundEvent(soundID).setRegistryName(soundID);
	}


	@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
	public static class RegistrationHandler 
    {
		@SubscribeEvent
		public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) 
        {
			event.getRegistry().registerAll
            (
                    ENTITY_BLASTER_CHARGING, 
                    ENTITY_BLASTER_SOUND, 
                    ENTITY_DIZZY,
                    ENTITY_DIZZY_BOSS,
                    ENTITY_GAUSS_NUKE,
                    ENTITY_SCYTHE_SWING,
                    ENTITY_SLAM_EXPLOSION,
                    ENTITY_STAR_WINDUP,
                    ENTITY_TACTICAL_NUKE,
					ENTITY_BRAINSLIME_CHARGE,
                    ENTITY_CREEPER_ANNOYED,
                    ENTITY_CREEPER_BUFF_ATTEMPT,
                    ENTITY_CREEPER_BUFF_EXECUTE,
                    ENTITY_CREEPER_ITEMBOX,
                    ENTITY_CREEPER_NUKE,
                    ENTITY_CREEPER_PARTY,
					ENTITY_FLAMESPEWER_IDLE,
					ENTITY_GROVESPRITE_ANGRY,
					ENTITY_GROVESPRITE_DEATH,
					ENTITY_GROVESPRITE_HURT,
					ENTITY_GROVESPRITE_IDLE,
					ENTITY_GROVESPRITE_THANKS,
					ENTITY_HARPY_HURT,
					ENTITY_HARPY_IDLE,
					ENTITY_MOTHERSPIDER_SCREECH,
	                ENTITY_QUAZAR_LANDING_EXPLOSION,
                    ENTITY_QUAZAR_LEAP_WHOOSH,
                    ENTITY_QUAZAR_NUKE_ALARM,
					ENTITY_TROLLAGER_ATTACK,
					ENTITY_TROLLAGER_DEATH,
					ENTITY_TROLLAGER_HIT,
					ENTITY_TROLLAGER_IDLE,
					ENTITY_VOIDEYE_IDLE
			);
		}
	}
}
