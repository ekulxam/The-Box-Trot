package dev.cammiescorner.boxtrot.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/PlayerScreenHandler$1")
public abstract class ArmourSlotMixin extends Slot {
	public ArmourSlotMixin(Inventory inventory, int index, int x, int y) { super(inventory, index, x, y); }

	@Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
	public void boxtrot$insertBarrel(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		if(stack.getItem() == Items.BARREL && getIndex() == 39)
			info.setReturnValue(true);
	}
}
