package dev.cammiescorner.boxtrot.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HeadFeatureRenderer.class)
public abstract class HeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public HeadFeatureRendererMixin(FeatureRendererContext<T, M> context) { super(context); }

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/entity/feature/HeadFeatureRenderer;translate(Lnet/minecraft/client/util/math/MatrixStack;Z)V"
	), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void boxtrot$render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info, ItemStack itemStack, Item item, boolean bl) {
		if(item == Items.BARREL) {
			matrixStack.pop();
			matrixStack.pop();
			matrixStack.push();
			matrixStack.push();

			if(!livingEntity.isSneaking()) {
				matrixStack.translate(0, 0.8, 0);
				matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(livingEntity.getYaw(g)));
			}
			else {
				matrixStack.translate(0, 0.225, 0);
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
			}

			matrixStack.scale(1F / 0.625F, 1F / 0.625F, 1F / 0.625F);
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
		}
	}
}
