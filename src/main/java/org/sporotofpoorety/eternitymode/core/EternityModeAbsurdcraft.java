package org.sporotofpoorety.eternitymode.core;


import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.sporotofpoorety.eternitymode.Tags;
import org.sporotofpoorety.eternitymode.config.EternityModeConfig;
import org.sporotofpoorety.eternitymode.core.EternityModeCommonProxy;



@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION,
        guiFactory = "org.sporotofpoorety.eternitymode.config.EternityModeFactoryGui", dependencies= "required-after:mixinbooter@[10.1,);required-after:ebwizardry")
public class EternityModeAbsurdcraft {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(
        clientSide = "org.sporotofpoorety.eternitymode.core.EternityModeClientProxy",
        serverSide = "org.sporotofpoorety.eternitymode.core.EternityModeCommonProxy"
    )
    public static EternityModeCommonProxy eternityModeProxy;


    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
//      LOGGER.info("Hello From {}!", Tags.MOD_NAME);


        eternityModeProxy.registerRenderers();
		EternityModeConfig.load(event);
    }

}
