package dev.cammiescorner.boxtrot.mixin;

import net.minecraft.block.BarrelBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equippable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrelBlock.class)
public class BarrelBlockMixin implements Equippable {

	@Override
	public EquipmentSlot getPreferredSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.BLOCK_BARREL_OPEN;
	}
}
