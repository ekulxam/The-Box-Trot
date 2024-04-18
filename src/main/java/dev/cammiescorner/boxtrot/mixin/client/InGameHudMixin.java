package dev.cammiescorner.boxtrot.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.cammiescorner.boxtrot.BoxTrot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Shadow @Final private MinecraftClient client;
	@Shadow protected abstract void renderOverlay(GuiGraphics context, Identifier texture, float opacity);

	@Unique private static final Identifier BARREL_OVERLAY = BoxTrot.id("textures/misc/barrel_hole.png");

	@ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "FIELD",
			target = "net/minecraft/client/MinecraftClient.targetedEntity : Lnet/minecraft/entity/Entity;",
			ordinal = 0
	))
	public Entity boxtrot$hideAttackIndicator(Entity original) {
		if(original instanceof PlayerEntity player && player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && player.isSneaking())
			return null;

		return original;
	}

	@Inject(method = "render", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
	))
	public void boxtrot$renderOverlay(GuiGraphics context, float tickDelta, CallbackInfo ci) {
		if(client.player != null && client.player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)) {
			renderOverlay(context, BARREL_OVERLAY, 1F);
		}
	}
}
