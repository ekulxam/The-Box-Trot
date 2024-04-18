package dev.cammiescorner.boxtrot;

import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncStandingStillTimer;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class BoxTrot implements ModInitializer {
	public static final String MOD_ID = "boxtrot";

	@Override
	public void onInitialize(ModContainer mod) {
		MidnightConfig.init(MOD_ID, BoxTrotConfig.class);

		ServerPlayNetworking.registerGlobalReceiver(SyncStandingStillTimer.ID, SyncStandingStillTimer::handler);
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
