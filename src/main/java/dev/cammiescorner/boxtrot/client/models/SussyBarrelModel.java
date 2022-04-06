package dev.cammiescorner.boxtrot.client.models;

import dev.cammiescorner.boxtrot.BoxTrot;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class SussyBarrelModel<T extends Entity> extends EntityModel<T> {
	public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(BoxTrot.id("sussy_barrel"), "main");
	private final ModelPart barrel;

	public SussyBarrelModel(ModelPart root) {
		this.barrel = root.getChild("barrel");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData data = new ModelData();
		ModelPartData partdefinition = data.getRoot();

		partdefinition.addChild("barrel", ModelPartBuilder.create().uv(0, 0).cuboid(-8F, -16F, -8F, 16F, 16F, 16F, new Dilation(0F)), ModelTransform.pivot(0F, 24F, 0F));

		return TexturedModelData.of(data, 64, 64);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
		barrel.render(matrices, buffer, light, overlay);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}