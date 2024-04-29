package dev.cammiescorner.boxtrot.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	@Shadow
	public abstract boolean isSneaking();

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@ModifyReturnValue(method = "shouldSpawnSprintingParticles", at = @At("RETURN"))
	private boolean noSprintParticles(boolean original){
		return BoxTrotConfig.doesBarrelHideParticles ? (!this.isSneaking() || !this.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)) && original : original;
	}

	@ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z"))
	private boolean sprintInBox(boolean original){
		return BoxTrotConfig.barrelSprint ? original || (this.isSneaking() && this.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)) : original;
	}
}
