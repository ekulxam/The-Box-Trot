package dev.cammiescorner.boxtrot.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixn {
	@Shadow @Nullable public abstract LivingEntity getTarget();
	@Shadow public abstract void setTarget(@Nullable LivingEntity target);

	@Inject(method = "tick", at = @At("HEAD"))
	public void boxtrot$noTargetBox(CallbackInfo info) {
		if(getTarget() instanceof PlayerEntity player && player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && player.isSneaking())
			setTarget(null);
	}
}
