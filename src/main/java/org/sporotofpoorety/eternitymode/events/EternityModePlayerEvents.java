package org.sporotofpoorety.eternitymode.events;


import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import org.sporotofpoorety.eternitymode.config.EternityModeConfigGeneral;
import org.sporotofpoorety.eternitymode.config.EternityModeConfigPlayerAttributes;
import org.sporotofpoorety.eternitymode.util.MiscUtil;

import com.tmtravlr.potioncore.PotionCoreAttributes;




public class EternityModePlayerEvents 
{

//Event listeners RNG
    private final Random rand = new Random();




/*
Assigning UUIDs to these 
attribute modifiers is needed 
as a flag against their reapplication
*/
    public void setPlayerAdjustedAttributes(EntityPlayer player) 
    {

//UUIDs for the attribute modifiers
        UUID playerBaseHealthModifierUUID = UUID.fromString("07a94464-2de6-4fb5-9e12-28123d01cfdf");
        UUID playerBaseAttackModifierUUID = UUID.fromString("51e0beb4-2d6f-4a59-888e-cf513b11f4a0");
        UUID playerBaseSpeedModifierUUID = UUID.fromString("69267fce-6c5b-4910-a333-93381084209b");
        UUID playerBaseJumpModifierUUID = UUID.fromString("6d0dfd45-15fd-4e5b-9701-9c37742fd6f4");
        UUID playerBaseStepModifierUUID = UUID.fromString("64beb265-9835-425e-ad81-de5367255c2c");
      
//Player attribute instances,
//gotten from EntityLivingBase using IAttributes
        IAttributeInstance playerBaseHealthInstance = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        IAttributeInstance playerBaseAttackInstance = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        IAttributeInstance playerBaseSpeedInstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        IAttributeInstance playerBaseJumpInstance = player.getEntityAttribute(PotionCoreAttributes.JUMP_HEIGHT);
        IAttributeInstance playerBaseStepInstance = player.getEntityAttribute(PotionCoreAttributes.STEP_HEIGHT);

//Remove modifiers to keep the list with only one copy of each
        playerBaseHealthInstance.removeModifier(playerBaseHealthModifierUUID);
        playerBaseAttackInstance.removeModifier(playerBaseAttackModifierUUID);
        playerBaseSpeedInstance.removeModifier(playerBaseSpeedModifierUUID);
        playerBaseJumpInstance.removeModifier(playerBaseJumpModifierUUID);
        playerBaseStepInstance.removeModifier(playerBaseStepModifierUUID);

//Create modifiers for each attribute
        AttributeModifier playerBaseHealthModifier
//80 
            = new AttributeModifier(playerBaseHealthModifierUUID, "generic.maxHealth", EternityModeConfigPlayerAttributes.playerBaseHealthIncrease, 0);
        AttributeModifier playerBaseAttackModifier 
//3
            = new AttributeModifier(playerBaseAttackModifierUUID, "generic.attackDamage", EternityModeConfigPlayerAttributes.playerBaseAttackIncrease, 0);
        AttributeModifier playerBaseSpeedModifier   
//0.3
            = new AttributeModifier(playerBaseSpeedModifierUUID, "generic.movementSpeed", EternityModeConfigPlayerAttributes.playerBaseSpeedIncrease, 0);
        AttributeModifier playerBaseJumpModifier 
//1.2
            = new AttributeModifier(playerBaseJumpModifierUUID, "potioncore.jumpHeight", EternityModeConfigPlayerAttributes.playerBaseJumpIncrease, 0);
        AttributeModifier playerBaseStepModifier
//1.0 
            = new AttributeModifier(playerBaseStepModifierUUID, "potioncore.stepHeight", 1.0, 0);

//Finally, apply them to the player
        playerBaseHealthInstance.applyModifier(playerBaseHealthModifier);
        playerBaseAttackInstance.applyModifier(playerBaseAttackModifier);
        playerBaseSpeedInstance.applyModifier(playerBaseSpeedModifier);
        playerBaseJumpInstance.applyModifier(playerBaseJumpModifier);
        playerBaseStepInstance.applyModifier(playerBaseStepModifier);
    }




    @SubscribeEvent
    public void playerLoginAttributesAndLoginCount(PlayerEvent.PlayerLoggedInEvent event)
    { 
//Get player
        EntityPlayer player = event.player;
//Get NBT in portable format
        NBTTagCompound playerNBT = player.getEntityData();


//Get persistent NBT
        NBTTagCompound forgeData = MiscUtil.compoundInCompound("ForgeData", playerNBT);
        NBTTagCompound playerPersisted = MiscUtil.compoundInCompound("PlayerPersisted", forgeData);
//And log count
        Integer logCount = playerPersisted.getInteger("logCount");


//Get new log count
        int newLogCount = (logCount == null) ? 1 : (logCount + 1);

//If first login ever
        if (newLogCount == 1) 
        {
//Adjust attributes
            setPlayerAdjustedAttributes(event.player);
//Set to full health
            player.setHealth(200.0F);
//          GameStageHelper.addStage(player, "phase0"); 
        }

//Set logCount to new log count
        playerPersisted.setInteger("logCount", newLogCount);
    }




//Player properly respawns at full health
    @SubscribeEvent
    public void onPlayerRespawnAttributesAndHeal(PlayerEvent.PlayerRespawnEvent event) 
    {
        setPlayerAdjustedAttributes(event.player);
        event.player.setHealth(200.0F);
    }




//Healing optionally scales with player health
    @SubscribeEvent
    public void onLivingHealPlayerScale(LivingHealEvent event) 
    {
//If player being healed
        if (event.getEntity() instanceof EntityPlayer) 
        {
//Scale heal
            float scale = (((EntityPlayer) event.getEntity()).getMaxHealth() / 200.0F);
            if(scale > 0.0F) { event.setAmount(event.getAmount() * scale); }
        }
    }




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

//If player wasn't in creative
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
        if(EternityModeConfigGeneral.softHardcoreEnabled && !(event.player.isCreative()))
        {
//Get persistent data (ForgeData)
            NBTTagCompound persistentData = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);


//Set permadeath end tick
            long permadeathEndTick = event.player.world.getTotalWorldTime() + (EternityModeConfigGeneral.softHardcoreLength * 20);

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
                    EternityModeConfigGeneral.softHardcoreLength * 20,
                    0,
                    false,
                    true
                ));


//Susceptibility message

/*
                    event.player.sendMessage(new TextComponentTranslation
                    (
                            "chat.type.text",
                            "Gabriel the Disgraced",
                            new TextComponentString("§cMy immortality is waning, if i die again too shortly, i'll be gone for good.")
                    ));
*/
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
/*
                event.player.sendMessage
                    (new TextComponentString("§cGAME OVER."));
*/
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
                        event.player.sendMessage(new TextComponentTranslation
                        (
                                "chat.type.text",
                                "Gabriel the Disgraced",
                                new TextComponentString("§cMy immortality has had time to regenerate, i suppose i'm safe... for now.")
                        ));
*/
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
