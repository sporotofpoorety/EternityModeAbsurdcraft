package org.sporotofpoorety.eternitymode.config;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import org.sporotofpoorety.eternitymode.Tags;

public class EternityModeGuiConfig extends GuiConfig {

	public EternityModeGuiConfig(GuiScreen parentScreen) 
	{
		super(parentScreen, getConfigElements(), Tags.MOD_ID, false, false, "eternitymode.config.title");
	}

	private static List<IConfigElement> getConfigElements() 
	{
		return EternityModeConfig.config.getCategoryNames().stream()
				.map(categoryName -> new ConfigElement(EternityModeConfig.config.getCategory(categoryName).setLanguageKey("eternitymode.config." + categoryName)))
				.collect(Collectors.toList());
	}
}
