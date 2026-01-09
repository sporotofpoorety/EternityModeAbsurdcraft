package org.sporotofpoorety.eternitymode.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sporotofpoorety.eternitymode.Tags;



@IFMLLoadingPlugin.Name(Tags.MOD_ID)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class EternityModeEarlyMixins implements IEarlyMixinLoader, IFMLLoadingPlugin {

	public EternityModeEarlyMixins() {

	}

	//mixin stuff here on out
	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.early.eternitymode.json");
    }

	public void onMixinConfigQueued(String mixinConfig) {
		System.out.println(mixinConfig+" has been queued");
	}
}
