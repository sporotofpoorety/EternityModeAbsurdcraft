package org.sporotofpoorety.eternitymode.config;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.sporotofpoorety.eternitymode.Tags;



public class EternityModeConfig 
{

	static Configuration config;


	public static void load(FMLPreInitializationEvent event) 
    {
		File dir = getEternityModeConfigurationLocation(event);
		if(!dir.exists())
		    { dir.mkdirs(); }

		config = new Configuration(new File(dir, "eternitymode.cfg"));
		reloadConfig();
		

		MinecraftForge.EVENT_BUS.register(new EternityModeConfig());
	}


	private static void reloadConfig() 
    {		
		EternityModeConfigGeneral.load(config);
		EternityModeConfigMobs.load(config);

		if (config.hasChanged()) 
        {
			config.save();
		}
	}


	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) 
    {
		if (event.getModID().equals(Tags.MOD_ID)) 
        {
			reloadConfig();
		}
	}
	

	public static File getEternityModeConfigurationLocation(FMLPreInitializationEvent event)
	{
		return new File(event.getModConfigurationDirectory(), "eternitymode");
	}
}
