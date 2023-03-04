package dev.cammiescorner.boxtrot;

import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncStandingStillTimer;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class BoxTrot implements ModInitializer {
	public static final String MOD_ID = "boxtrot";

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, BoxTrotConfig.class);

		ServerPlayNetworking.registerGlobalReceiver(SyncStandingStillTimer.ID, SyncStandingStillTimer::handler);
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
