package dev.cammiescorner.boxtrot.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class FakeBarrelInventory extends SimpleInventory {
	private final PlayerEntity target;
	private final World world;

	public FakeBarrelInventory(PlayerEntity target) {
		super(27);
		this.target = target;
		this.world = target.getWorld();

		for(int i = 0; i < 27; i++)
			setStack(i, target.getInventory().getStack(i + 9));
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		super.setStack(slot, stack);
		target.getInventory().setStack(slot + 9, stack);
	}

	@Override
	public void onOpen(PlayerEntity player) {
		super.onOpen(player);
		world.playSound(null, target.getX(), target.getY() + 0.5, target.getZ(), SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void onClose(PlayerEntity player) {
		super.onClose(player);
		world.playSound(null, target.getX(), target.getY() + 0.5, target.getZ(), SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
	}

	public PlayerEntity getTarget() {
		return target;
	}
}
