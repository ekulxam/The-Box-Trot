package dev.cammiescorner.boxtrot.mixin;

import dev.cammiescorner.boxtrot.common.FakeBarrel;
import dev.cammiescorner.boxtrot.common.FakeBarrelInventory;
import dev.cammiescorner.boxtrot.common.packets.SyncStandingStillTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.OptionalInt;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements FakeBarrel {
	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);
	@Shadow public abstract OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory);
	@Shadow protected abstract void closeHandledScreen();

	@Unique private int stoodStillFor;
	@Unique private FakeBarrelInventory barrelInventory;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "tick", at = @At("HEAD"))
	public void boxtrot$tick(CallbackInfo info) {
		FakeBarrelInventory barrelInventory = getFakeBarrelInv();

		if(barrelInventory != null) {
			PlayerEntity target = barrelInventory.getTarget();

			if(!(target.isSneaking() && target.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)) || squaredDistanceTo(target) > 64)
				closeHandledScreen();

			PlayerInventory playerInv = target.getInventory();

			for(int i = 0; i < 27; i++)
				barrelInventory.setStack(i, playerInv.getStack(i + 9));
		}

		if(getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && isSneaking()) {
			ScaleData data = ScaleTypes.HITBOX_HEIGHT.getScaleData(this);
			data.setScale(1F / 1.5F);

			data = ScaleTypes.HITBOX_WIDTH.getScaleData(this);
			data.setScale(1F / 0.6F);

			data = ScaleTypes.EYE_HEIGHT.getScaleData(this);
			data.setScale(1F / 1.5F);

			if(world.isClient()) {
				if(getVelocity().horizontalLength() > 0)
					setStoodStillFor(0);
				else
					setStoodStillFor(getStoodStillFor() + 1);

				if(getStoodStillFor() == 10)
					setPosition(getBlockX() + 0.5, getY(), getBlockZ() + 0.5);

				SyncStandingStillTimer.send(stoodStillFor);
			}
		}
		else {
			setStoodStillFor(0);
			ScaleData data = ScaleTypes.HITBOX_HEIGHT.getScaleData(this);
			data.setScale(1F);

			data = ScaleTypes.HITBOX_WIDTH.getScaleData(this);
			data.setScale(1F);

			data = ScaleTypes.EYE_HEIGHT.getScaleData(this);
			data.setScale(1F);
		}
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void boxtrot$openInventory(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		if(entity instanceof PlayerEntity target && target.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && target.isSneaking()) {
			barrelInventory = new FakeBarrelInventory(target);

			openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player) ->
				GenericContainerScreenHandler.createGeneric9x3(syncId, inv, barrelInventory), new TranslatableText("container.barrel")
			));

			info.setReturnValue(ActionResult.success(world.isClient));
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void boxtrot$writeNbt(NbtCompound nbt, CallbackInfo info) {
		nbt.putInt("StoodStillFor", stoodStillFor);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void boxtrot$readNbt(NbtCompound nbt, CallbackInfo info) {
		stoodStillFor = nbt.getInt("StoodStillFor");
	}

	@Override
	public boolean isCollidable() {
		return getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && isSneaking();
	}

	@Override
	public boolean isPushable() {
		return !(getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && isSneaking());
	}

	@Override
	public FakeBarrelInventory getFakeBarrelInv() {
		return barrelInventory;
	}

	@Override
	public int getStoodStillFor() {
		return stoodStillFor;
	}

	@Override
	public void setStoodStillFor(int value) {
		stoodStillFor = value;
	}
}
