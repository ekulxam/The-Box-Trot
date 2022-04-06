package dev.cammiescorner.boxtrot.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) { super(ctx, model, shadowRadius); }

	@Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	private void boxtrot$ceaseRendering(AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
		if(player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && player.isSneaking()) {
			for(FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> feature : features)
				if(feature instanceof HeadFeatureRenderer)
					feature.render(matrixStack, vertexConsumerProvider, i, player, 0, 0, g, 0, 0, 0);

			info.cancel();
		}
	}

	@Inject(method = "setModelPose", at = @At("TAIL"))
	private void boxtrot$hideModelParts(AbstractClientPlayerEntity player, CallbackInfo info) {
		if(player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)) {
			MinecraftClient client = MinecraftClient.getInstance();
			boolean renderArms = client.getCameraEntity() == player && client.options.getPerspective().isFirstPerson();

			getModel().head.visible = false;
			getModel().hat.visible = false;
			getModel().body.visible = false;
			getModel().jacket.visible = false;

			getModel().leftArm.visible = renderArms;
			getModel().leftSleeve.visible = renderArms;
			getModel().rightArm.visible = renderArms;
			getModel().rightSleeve.visible = renderArms;
		}
	}

	@Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	public void boxtrot$hideName(AbstractClientPlayerEntity player, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
		if(player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && player.isSneaking())
			info.cancel();
	}
}
