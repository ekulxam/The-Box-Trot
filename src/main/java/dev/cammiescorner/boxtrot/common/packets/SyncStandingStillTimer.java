package dev.cammiescorner.boxtrot.common.packets;

import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.common.FakeBarrel;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.Iterator;

public class SyncStandingStillTimer {
	public static final Identifier ID = BoxTrot.id("sync_standing_still_timer_server");

	public static void send(int value) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarInt(value);

		ClientPlayNetworking.send(ID, buf);
	}

	public static void handler(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		int value = buf.readVarInt();
		server.execute(() -> ((FakeBarrel) player).boxtrot$setStoodStillFor(value));
	}
}
