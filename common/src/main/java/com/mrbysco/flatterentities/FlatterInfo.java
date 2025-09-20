package com.mrbysco.flatterentities;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public interface FlatterInfo {
	float flatterentities$getYawLerp();

	void flatterentities$setYawLerp(float yawLerp);

	EntityType<?> flatterentities$getEntityType();

	void flatterentities$setEntityType(EntityType<?> type);

	ResourceKey<Level> flatterentities$getDimension();

	void flatterentities$setDimension(ResourceKey<Level> dimension);
}
