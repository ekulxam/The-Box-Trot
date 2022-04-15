package dev.cammiescorner.boxtrot.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

	public LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

	@WrapWithCondition(method = "tickStatusEffects", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
	))
	private boolean boxtrot$noParticles(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		return !(isSneaking() && getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL));
	}

	@Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
	private void boxtrot$noTarget(LivingEntity target, CallbackInfoReturnable<Boolean> info) {
		if(target instanceof PlayerEntity player && player.isSneaking() && player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL))
			info.setReturnValue(false);
	}

	@Inject(method = "canSee", at = @At("HEAD"), cancellable = true)
	private void boxtrot$noSee(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if(entity instanceof PlayerEntity player && player.isSneaking() && player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL))
			info.setReturnValue(false);
	}
}
