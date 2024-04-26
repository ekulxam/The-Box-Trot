package dev.cammiescorner.boxtrot.client;

import dev.cammiescorner.boxtrot.client.models.SussyBarrelModel;
import dev.cammiescorner.boxtrot.common.packets.SyncBoxTrotConfig;
import dev.cammiescorner.boxtrot.mixin.client.WorldRendererAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class BoxTrotClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		EntityModelLayerRegistry.registerModelLayer(SussyBarrelModel.MODEL_LAYER, SussyBarrelModel::getTexturedModelData);

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();

			if(client.crosshairTarget instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof PlayerEntity target && target.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && target.isSneaking() && (target.getX() == target.getBlockX() + 0.5 && target.getZ() == target.getBlockZ() + 0.5)) {
				Vec3d camPos = context.camera().getPos();

				((WorldRendererAccessor) client.worldRenderer).boxtrot$drawBlockOutline(
						context.matrixStack(), context.consumers().getBuffer(RenderLayer.getLines()), client.player,
						camPos.getX(), camPos.getY(), camPos.getZ(), target.getBlockPos(), Blocks.BARREL.getDefaultState());
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(SyncBoxTrotConfig.ID, SyncBoxTrotConfig::handler);
	}
}
