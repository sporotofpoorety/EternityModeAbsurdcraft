package org.sporotofpoorety.eternitymode.config;

import net.minecraftforge.common.config.Configuration;

public class EternityModeConfigGeneral
{

    public static boolean softHardcoreEnabled;
    public static int softHardcoreLength;

	public static void load(Configuration config) 
    {
//Adds config category
		String category1 = "Roguelike Rules";
		config.addCustomCategoryComment(category1, "Rules which govern roguelike mechanics");

//Format is category, key, default value, comment
        softHardcoreEnabled = config.get(category1, "Soft-Hardcore enabled", false, "Enables a mode similar to Hardcore Mode - Players can still respawn, but if they die within a short period of time after respawning, the death is permanent.").getBoolean();
        softHardcoreLength = config.get(category1, "Soft-Hardcore length", 600, "When dying in Soft-Hardcore, left susceptible to permadeath for this many seconds.").getInt();
	}


    public static boolean getSoftHardcoreEnabled()
    {
        return softHardcoreEnabled;
    }

    public static int getSoftHardcoreLength()
    {
        return softHardcoreLength;
    }

}
