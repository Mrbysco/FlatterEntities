package com.mrbysco.flatterentities.mixin;

import com.mrbysco.flatterentities.FlatterInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements FlatterInfo {
	@Unique
	public float flatterentities$yawLerp = 0.0F;
	@Unique
	public EntityType<?> flatterentities$entityType = null;
	@Unique
	public ResourceKey<Level> flatterentities$dimension = null;

	@Override
	public float flatterentities$getYawLerp() {
		return this.flatterentities$yawLerp;
	}

	@Override
	public void flatterentities$setYawLerp(float yawLerp) {
		this.flatterentities$yawLerp = yawLerp;
	}

	@Override
	public EntityType<?> flatterentities$getEntityType() {
		return this.flatterentities$entityType;
	}

	@Override
	public void flatterentities$setEntityType(EntityType<?> type) {
		this.flatterentities$entityType = type;
	}

	@Override
	public ResourceKey<Level> flatterentities$getDimension() {
		return this.flatterentities$dimension;
	}

	@Override
	public void flatterentities$setDimension(ResourceKey<Level> dimension) {
		this.flatterentities$dimension = dimension;
	}
}
