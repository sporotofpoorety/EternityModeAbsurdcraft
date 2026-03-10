package org.sporotofpoorety.eternitymode.config;

import net.minecraftforge.common.config.Configuration;

public class EternityModeConfigPlayerAttributes
{

    public static int playerBaseHealthIncrease;
    public static int playerBaseAttackIncrease;
    public static double playerBaseSpeedIncrease;
    public static double playerBaseJumpIncrease;


	public static void load(Configuration config) 
    {
//Adds config category
		String category1 = "Player attributes";
		config.addCustomCategoryComment(category1, "Increases to player base stats");

//Format is category, key, default value, comment
        playerBaseHealthIncrease = config.get(category1, "Player base health increase", 80, "Increase player base health by this amount.").getInt();
        playerBaseAttackIncrease = config.get(category1, "Player base attack increase", 4, "Increase player base attack by this amount.").getInt();
        playerBaseSpeedIncrease = config.get(category1, "Player base speed increase", 0.035, "Increase player base speed by this amount.").getDouble();
        playerBaseJumpIncrease = config.get(category1, "Player base jump increase", 1.2, "Increase player base jump by this amount.").getDouble();
	}
}
