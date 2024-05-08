package dev.cammiescorner.boxtrot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {
	@ModifyExpressionValue(method = "isPlayerStaring", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
	))
	public boolean barrelWorksLikePumpkin(boolean original, @Local ItemStack stack) {
		return original || (BoxTrotConfig.doesBarrelFoolEndermen && stack.isOf(Items.BARREL));
    }
}
