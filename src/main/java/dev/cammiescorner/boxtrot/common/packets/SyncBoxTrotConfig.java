package dev.cammiescorner.boxtrot.common.packets;

import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.common.FakeBarrel;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.KickCommand;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncBoxTrotConfig {
	public static final Identifier ID = BoxTrot.id("sync_box_trot_config");

	public static void send(MinecraftServer server) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCollection(BoxTrotConfig.getConfigValues(), PacketByteBuf::writeBoolean);
		ServerPlayNetworking.send(server.getPlayerManager().getPlayerList(), ID, buf);
	}

	public static void handler(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		List<Boolean> configValues = buf.readCollection(ArrayList::new, PacketByteBuf::readBoolean);
		if (!configValues.equals(BoxTrotConfig.getConfigValues())) {
			handler.onDisconnected(Text.translatable("boxtrot.multiplayer.configdesync"));
		}
	}
}
