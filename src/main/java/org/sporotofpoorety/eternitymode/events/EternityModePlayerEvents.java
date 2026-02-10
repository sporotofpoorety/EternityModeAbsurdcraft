package org.sporotofpoorety.eternitymode.events;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import org.sporotofpoorety.eternitymode.config.EternityModeConfigGeneral;




public class EternityModePlayerEvents {


//On entity death
    @SubscribeEvent
    public void playerRoguelikeDeath(LivingDeathEvent event) 
    {
//If it was a player that died
        if(event.getEntityLiving() instanceof EntityPlayer)
        {
//Get player
            EntityPlayer player = ((EntityPlayer) event.getEntityLiving());

//If player is creative mode do nothing
            if(player.isCreative())
            {
                return;
            }
            else
            {
//Get player persistent data
                NBTTagCompound persistentData = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);


//If player has permadeath NBT
                if(persistentData.hasKey("PermadeathEnabled") && persistentData.hasKey("PermadeathEndTick"))
                {
//If permadeath NBT enabled
                    if(persistentData.getBoolean("PermadeathEnabled"))
                    {
//Permadeath flag
                        persistentData.setBoolean("PermadeathActivated", true);
//Write to persistent data
                        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentData);
                    }
                }
            }
        }
    }




//On player respawn
    @SubscribeEvent
    public void playerRoguelikeRespawn(PlayerRespawnEvent event) 
    {
//If soft-hardcore enabled and player not in creative mode
        if(EternityModeConfigGeneral.getSoftHardcoreEnabled() && !(event.player.isCreative()))
        {
//Get persistent data (ForgeData)
            NBTTagCompound persistentData = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);


//Set permadeath end tick
            long permadeathEndTick = event.player.world.getTotalWorldTime() + (EternityModeConfigGeneral.getSoftHardcoreLength() * 20);

//Set permadeath true
            persistentData.setBoolean("PermadeathEnabled", true);
//Set permadeath end time
            persistentData.setLong("PermadeathEndTick", permadeathEndTick);
//Write to persistent data
            event.player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentData);


//No indicators if already permadead
            if(!(persistentData.hasKey("PermadeathActivated"))
            || (persistentData.hasKey("PermadeathActivated") && !persistentData.getBoolean("PermadeathActivated")))
            {
//Visual indicator
                event.player.addPotionEffect(new PotionEffect
                (
                    MobEffects.GLOWING,
                    EternityModeConfigGeneral.getSoftHardcoreLength() * 20,
                    0,
                    false,
                    true
                ));


//Susceptibility message

/*
                event.player.sendMessage
                    (new TextComponentString("§cMy immortality is waning, if i die again too shortly, i'll be gone for good."));
*/

                    event.player.sendMessage(new TextComponentTranslation
                    (
                            "chat.type.text",
                            "Gabriel the Disgraced",
                            new TextComponentString("§cMy immortality is waning, if i die again too shortly, i'll be gone for good.")
                    ));

            }
        }
    }




//On player tick
    @SubscribeEvent
    public void playerRoguelikeTick(PlayerTickEvent event) 
    {
//Get player persistent data
        NBTTagCompound persistentData = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);


//If permadeath activated
        if(persistentData.hasKey("PermadeathActivated") && persistentData.getBoolean("PermadeathActivated"))
        {
//If not already in spectator mode
            if (!event.player.isSpectator()) 
            {
//End run
                event.player.setGameType(GameType.SPECTATOR);
//With permadeath message
                event.player.sendMessage
                    (new TextComponentString("§cGAME OVER."));
            }
        }


//If player has permadeath NBT
        if(persistentData.hasKey("PermadeathEnabled") && persistentData.hasKey("PermadeathEndTick"))
        {
//If player's permadeath is enabled
            if(persistentData.getBoolean("PermadeathEnabled"))
            {
//Get permadeath NBT timer
                long permadeathEndTime = persistentData.getLong("PermadeathEndTick");

//If world ticks reached permadeath NBT end time
                if(event.player.world.getTotalWorldTime() >= permadeathEndTime)
                {
//If permadeath activated don't send message
                    if(persistentData.hasKey("PermadeathActivated") && !persistentData.getBoolean("PermadeathActivated"))
                    {
/*
                        event.player.sendMessage
                            (new TextComponentString("§cMy immortality has had time to regenerate, i guess i'm safe... for now."));
*/

                        event.player.sendMessage(new TextComponentTranslation
                        (
                                "chat.type.text",
                                "Gabriel the Disgraced",
                                new TextComponentString("§cMy immortality has had time to regenerate, i suppose i'm safe... for now.")
                        ));
                    }

//Disable permadeath
                    persistentData.setBoolean("PermadeathActivated", false);
                    persistentData.setBoolean("PermadeathEnabled", false);
//Write to persistent data
                    event.player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentData);
                }
            }
        }
    }

}
