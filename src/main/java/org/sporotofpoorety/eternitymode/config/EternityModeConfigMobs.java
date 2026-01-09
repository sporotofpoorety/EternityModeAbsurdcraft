package org.sporotofpoorety.eternitymode.config;

import net.minecraftforge.common.config.Configuration;

public class EternityModeConfigMobs {

    public static int placeholderValue;

	public static void load(Configuration config) {
		String category1 = "Placeholder category";

//Adds config category
		config.addCustomCategoryComment(category1, "This is a placeholder category.");
//Format is category, key, default value, comment
        placeholderValue = config.get(category1, "Placeholder value", 2, "I'm just testing").getInt();
	}


    public static int getPlaceholderValue()
    {
        return placeholderValue;
    }

}
