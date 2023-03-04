package dev.cammiescorner.boxtrot.common.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.cammiescorner.boxtrot.BoxTrot;

public class BoxTrotModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> BoxTrotConfig.getScreen(parent, BoxTrot.MOD_ID);
	}
}
