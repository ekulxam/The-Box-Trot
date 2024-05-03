package dev.cammiescorner.boxtrot.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.client.models.SussyBarrelModel;
import dev.cammiescorner.boxtrot.common.FakeBarrel;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Debug(export = true)
@Mixin(HeadFeatureRenderer.class)
public abstract class HeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
	@Shadow @Final private HeldItemRenderer heldItemRenderer;

	public HeadFeatureRendererMixin(FeatureRendererContext<T, M> context) { super(context); }

	@Unique private boolean isBarrel;
	@Unique private SussyBarrelModel<T> barrelModel;
	@Unique private static final ItemStack BARREL_STACK = new ItemStack(Items.BARREL);
	@Unique private static final Identifier SUSSY_BARREL = BoxTrot.id("textures/entity/sussy_barrel.png");

	@Inject(method = "<init>(Lnet/minecraft/client/render/entity/feature/FeatureRendererContext;Lnet/minecraft/client/render/entity/model/EntityModelLoader;Lnet/minecraft/client/render/item/HeldItemRenderer;)V", at = @At("TAIL"))
	private void boxtrot$setModel(FeatureRendererContext<T, M> context, EntityModelLoader loader, HeldItemRenderer heldItemRenderer, CallbackInfo info) {
		barrelModel = new SussyBarrelModel<T>(loader.getModelPart(SussyBarrelModel.MODEL_LAYER));
	}

	@WrapWithCondition(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/feature/HeadFeatureRenderer;translate(Lnet/minecraft/client/util/math/MatrixStack;Z)V"
	))
	private boolean boxtrot$noWeirdItemShit(MatrixStack matrices, boolean villager) {
		return !isBarrel;
	}

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
	), cancellable = true)
	private void boxtrot$render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info, @Local ItemStack itemStack) {
		isBarrel = itemStack.isOf(Items.BARREL) || livingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL); // trinkets compat

		if(isBarrel) {
			matrixStack.pop();
			matrixStack.push();

			matrixStack.translate(0, 0.625, 0);

			if(!livingEntity.isSneaking()) {
				matrixStack.translate(0, -1.375, 0);
				barrelModel.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(SUSSY_BARREL)), light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
			} else {
				if (BoxTrotConfig.barrelRotates && livingEntity instanceof AbstractClientPlayerEntity player) {
					matrixStack.multiply(Axis.Y_NEGATIVE.rotationDegrees(((FakeBarrel) player).boxtrot$getBarrelYaw()));
					matrixStack.multiply(Axis.X_POSITIVE.rotationDegrees(((FakeBarrel) player).boxtrot$getBarrelPitch()));
				}
				heldItemRenderer.renderItem(livingEntity, BARREL_STACK, ModelTransformationMode.NONE, false, matrixStack, vertexConsumerProvider, light);
			}

			matrixStack.pop();
			info.cancel();
		}
	}
}
