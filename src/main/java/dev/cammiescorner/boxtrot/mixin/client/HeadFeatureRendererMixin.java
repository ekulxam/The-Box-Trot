package dev.cammiescorner.boxtrot.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.cammiescorner.boxtrot.BoxTrot;
import dev.cammiescorner.boxtrot.client.models.SussyBarrelModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HeadFeatureRenderer.class)
public abstract class HeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public HeadFeatureRendererMixin(FeatureRendererContext<T, M> context) { super(context); }

	@Unique private boolean isBarrel;
	@Unique private SussyBarrelModel<T> barrelModel;
	@Unique private static final Identifier SUSSY_BARREL = BoxTrot.id("textures/entity/sussy_barrel.png");

	@Inject(method = "<init>(Lnet/minecraft/client/render/entity/feature/FeatureRendererContext;Lnet/minecraft/client/render/entity/model/EntityModelLoader;)V", at = @At("TAIL"))
	private void boxtrot$setModel(FeatureRendererContext<T, M> context, EntityModelLoader loader, CallbackInfo info) {
		barrelModel = new SussyBarrelModel<T>(loader.getModelPart(SussyBarrelModel.MODEL_LAYER));
	}

	@WrapWithCondition(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/feature/HeadFeatureRenderer;translate(Lnet/minecraft/client/util/math/MatrixStack;Z)V"
	))
	private boolean boxtrot$noWeirdItemShit(MatrixStack matrices, boolean villager) {
		return !isBarrel;
	}

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;getHeldItemRenderer()Lnet/minecraft/client/render/item/HeldItemRenderer;"
	), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void boxtrot$render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info, ItemStack itemStack, Item item, boolean bl) {
		isBarrel = item == Items.BARREL;

		if(isBarrel) {
			matrixStack.pop();
			matrixStack.pop();
			matrixStack.push();
			matrixStack.push();

			matrixStack.translate(0, 0.625, 0);

			if(!livingEntity.isSneaking()) {
				matrixStack.translate(0, -0.425, 0);
				matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(livingEntity.getYaw(g) + 180));

				barrelModel.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(SUSSY_BARREL)), i, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
				info.cancel();
				matrixStack.pop();
			}
		}
	}
}
