package dev.cammiescorner.boxtrot.mixin;

import net.minecraft.block.BarrelBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrelBlock.class)
public class BarrelBlockMixin implements Equipment {
    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }
}
