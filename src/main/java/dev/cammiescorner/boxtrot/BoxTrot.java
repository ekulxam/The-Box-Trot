package dev.cammiescorner.boxtrot;

import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncBoxTrotConfig;
import dev.cammiescorner.boxtrot.common.packets.SyncStandingStillTimer;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class BoxTrot implements ModInitializer {
	public static final String MOD_ID = "boxtrot";
	int timer = 600;

	@Override
	public void onInitialize(ModContainer mod) {
		MidnightConfig.init(MOD_ID, BoxTrotConfig.class);

		ServerPlayNetworking.registerGlobalReceiver(SyncStandingStillTimer.ID, SyncStandingStillTimer::handler);
		ServerTickEvents.END.register((server -> {
			timer--;
			if (timer <= 0) {
				timer = 600;
				SyncBoxTrotConfig.send(server);
			}
		}));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			SyncBoxTrotConfig.send(server);
		});
	}

	public static Identifier id(String name) {
		return new Identifier(MOD_ID, name);
	}
}
