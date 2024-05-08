package dev.cammiescorner.boxtrot.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

	// It's necessary

	public EntityMixin(EntityType<?> variant, World world) {
	}

	@Shadow
	public abstract boolean isSneaking();

	@Shadow
	public abstract World getWorld();

	@Shadow
	public abstract float getYaw();

	@Shadow
	public abstract float getPitch();

	@Shadow
	public abstract int getBlockZ();

	@Shadow
	public abstract double getY();

	@Shadow
	public abstract int getBlockX();

	@Shadow
	public abstract void setPosition(double x, double y, double z);

	@Shadow
	public abstract Vec3d getVelocity();

	@Shadow
	public abstract double squaredDistanceTo(Entity entity);

	@ModifyReturnValue(method = "isCollidable", at = @At("RETURN"))
	protected boolean isBarrelCollidable(boolean original){
		return original;
	}

	@ModifyReturnValue(method = "isPushable", at = @At("RETURN"))
	protected boolean isBarrelPushable(boolean original){
		return original;
	}
}
