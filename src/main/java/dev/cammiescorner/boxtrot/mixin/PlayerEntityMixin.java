package dev.cammiescorner.boxtrot.mixin;

import dev.cammiescorner.boxtrot.common.FakeBarrel;
import dev.cammiescorner.boxtrot.common.FakeBarrelInventory;
import dev.cammiescorner.boxtrot.common.config.BoxTrotConfig;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
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

@SuppressWarnings("UnreachableCode")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin implements FakeBarrel {
	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);
	@Shadow public abstract OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory);
	@Shadow protected abstract void closeHandledScreen();
	@Shadow public ScreenHandler currentScreenHandler;
	@Unique private int stoodStillFor;
	@Unique private FakeBarrelInventory barrelInventory;
	@Unique private int barrelYaw;
	@Unique private int barrelPitch;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "tick", at = @At("HEAD"))
	public void boxTick(CallbackInfo info) {
		FakeBarrelInventory barrelInventory = boxtrot$getFakeBarrelInv();

		if(barrelInventory != null && currentScreenHandler != null) {
			PlayerEntity target = barrelInventory.getTarget();

			if(!(target.isSneaking() && target.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL)) || this.squaredDistanceTo(target) > 64) {
				closeHandledScreen();
				this.barrelInventory = null;
				return;
			}

			PlayerInventory playerInv = target.getInventory();

			for(int i = 0; i < 27; i++)
				barrelInventory.setStack(i, playerInv.getStack(i + 9));
		}

		if(getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && isSneaking()) {
			ScaleData data = ScaleTypes.HITBOX_HEIGHT.getScaleData((PlayerEntity) (Object) this);
			data.setScale(1F / 1.5F);

			data = ScaleTypes.HITBOX_WIDTH.getScaleData((PlayerEntity) (Object) this);
			data.setScale(1F / 0.6F);

			data = ScaleTypes.EYE_HEIGHT.getScaleData((PlayerEntity) (Object) this);
			data.setScale(1F / 1.5F);

			if(getWorld().isClient()) {
				if(this.getVelocity().horizontalLength() > 0) {
					boxtrot$setStoodStillFor(0);
				} else {
					boxtrot$setStoodStillFor(boxtrot$getStoodStillFor() + 1);
				}

				if (boxtrot$getStoodStillFor() == 0 || boxtrot$getStoodStillFor() == 1) {
					barrelYaw = Math.round(this.getYaw() / 90f) * 90; // 90 degree increments to line up with the block
					barrelPitch = Math.round(this.getPitch() / 90f) * 90;
				} else if (boxtrot$getStoodStillFor() == 10) {
					this.setPosition(getBlockX() + 0.5, getY(), getBlockZ() + 0.5);
				}

				SyncStandingStillTimer.send(stoodStillFor);
			}
		} else {
			boxtrot$setStoodStillFor(0);
			ScaleData data = ScaleTypes.HITBOX_HEIGHT.getScaleData((PlayerEntity) (Object) this);
			data.setScale(1F);

			data = ScaleTypes.HITBOX_WIDTH.getScaleData((PlayerEntity) (Object) this);
			data.setScale(1F);

			data = ScaleTypes.EYE_HEIGHT.getScaleData((PlayerEntity) (Object) this);
			data.setScale(1F);
		}
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void openBoxInventory(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		if(BoxTrotConfig.canOpenPlayerBarrels && entity instanceof PlayerEntity target && target.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && target.isSneaking()) {
			barrelInventory = new FakeBarrelInventory(target);

			openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, player) ->
				GenericContainerScreenHandler.createGeneric9x3(syncId, inv, barrelInventory), Text.translatable("container.barrel")
			));

			info.setReturnValue(ActionResult.success(this.getWorld().isClient()));
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void writeBarrelNbt(NbtCompound nbt, CallbackInfo info) {
		nbt.putInt("StoodStillFor", stoodStillFor);
		nbt.putInt("BarrelYaw", barrelYaw);
		nbt.putInt("BarrelPitch", barrelPitch);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void readBarrelNbt(NbtCompound nbt, CallbackInfo info) {
		if (nbt.contains("StoodStillFor")) stoodStillFor = nbt.getInt("StoodStillFor");
		if (nbt.contains("BarrelYaw")) barrelYaw = nbt.getInt("BarrelYaw");
		if (nbt.contains("BarrelPitch")) barrelPitch = nbt.getInt("BarrelPitch");
	}

	@Override
	protected boolean isBarrelCollidable(boolean original) {
		return original || (this.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.BARREL) && isSneaking());
	}

	@Override
	protected boolean isBarrelPushable(boolean original) {
		return this.isBarrelCollidable(original);
	}

	@Override
	public FakeBarrelInventory boxtrot$getFakeBarrelInv() {
		return barrelInventory;
	}

	@Override
	public int boxtrot$getStoodStillFor() {
		return stoodStillFor;
	}

	@Override
	public void boxtrot$setStoodStillFor(int value) {
		stoodStillFor = value;
	}

	@Override
	public int boxtrot$getBarrelYaw() {
		return barrelYaw;
	}

	@Override
	public int boxtrot$getBarrelPitch() {
		return barrelPitch;
	}
}
