package dev.cammiescorner.boxtrot.mixin;

import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {
	@Inject(method = "isPlayerStaring", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
	), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	public void boxtrot$cantSeeMe(PlayerEntity player, CallbackInfoReturnable<Boolean> info, ItemStack stack) {
		if(stack.isOf(Items.BARREL))
			info.setReturnValue(false);
	}
}
