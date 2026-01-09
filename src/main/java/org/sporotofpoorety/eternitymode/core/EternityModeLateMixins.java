package org.sporotofpoorety.eternitymode.core;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;



@net.minecraftforge.fml.common.Optional.Interface(modid = "mixinbooter", iface = "zone.rong.mixinbooter.ILateMixinLoader")
public class EternityModeLateMixins implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.late.eternitymode.json");
    }
}
