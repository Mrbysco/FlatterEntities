package com.mrbysco.flatterentities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flattener {
	public static boolean renderingEnabled = true;

	private static final List<EntityType<?>> entityBlacklist = new ArrayList<>();
	private static final Map<ResourceKey<Level>, List<EntityType<?>>> entityDimensionWhitelist = new HashMap<>();
	private static final List<ResourceKey<Level>> dimensionBlacklist = new ArrayList<>();
	private static boolean dimensionListIsWhitelist = false;

	public static void prepareFlatRendering(float f, double x, double z, PoseStack poseStack, Entity entityIn) {
		if(renderingEnabled) {
			final EntityType<?> entityType = entityIn.getType();
			final ResourceKey<Level> entityDimension = entityIn.getCommandSenderWorld().dimension();
			final boolean entityInList = entityBlacklist.contains(entityIn.getType());
			final boolean worldInList = dimensionBlacklist.contains(entityDimension);
			boolean entityBlacklisted = !entityBlacklist.isEmpty() && entityInList;
			boolean dimensionFlat = dimensionBlacklist.isEmpty() || (!dimensionBlacklist.isEmpty() && dimensionListIsWhitelist == worldInList);
			boolean renderAnyway = false;

			if(!dimensionBlacklist.isEmpty() && !entityDimensionWhitelist.isEmpty()) {
				List<EntityType<?>> whitelist = entityDimensionWhitelist.getOrDefault(entityDimension, new ArrayList<>());
				renderAnyway = whitelist.contains(entityType) && !entityBlacklisted;
			}

			if (!entityBlacklisted && (dimensionFlat || renderAnyway)) {
				double angle1 = Mth.wrapDegrees(Math.atan2(z, x) / Math.PI * 180.0D);
				double angle2 = Mth.wrapDegrees(Math.floor((f - angle1) / 45.0) * 45.0D);
				
				final CameraType viewPoint = Minecraft.getInstance().options.getCameraType();
				boolean isPlayer = entityIn == Minecraft.getInstance().player;
				float offset = 0;
				if(isPlayer && entityIn instanceof LivingEntity livingEntity) {
					offset = Mth.wrapDegrees(livingEntity.yHeadRot - livingEntity.yHeadRotO);
				}

				if(isPlayer) {
					if(viewPoint == CameraType.FIRST_PERSON || viewPoint == CameraType.THIRD_PERSON_BACK) {
						angle1 = -90.0F - offset;
					}
					if(viewPoint == CameraType.THIRD_PERSON_FRONT) {
						angle1 = 90 + offset;
					}
				}

				poseStack.mulPose(Vector3f.YP.rotationDegrees((float) angle1));

				poseStack.scale(0.02F, 1.0F, 1.0F);

				if(isPlayer) {
					if(viewPoint == CameraType.FIRST_PERSON || viewPoint == CameraType.THIRD_PERSON_BACK) {
						angle2 = 90 + offset;
					}
					if(viewPoint == CameraType.THIRD_PERSON_FRONT) {
						angle2 = -90 - offset;
					}
				}
				poseStack.mulPose(Vector3f.YP.rotationDegrees((float) angle2));
			}
		}
	}

	public static void reloadCache () {
		entityBlacklist.clear();
		for (String value : FlatConfig.CLIENT.entityBlacklist.get()) {
			if(!value.isEmpty()) {
				ResourceLocation resourceLocation = ResourceLocation.tryParse(value);
				if (resourceLocation != null) {
					EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(resourceLocation);
					if (entityType != null) {
						entityBlacklist.add(entityType);
					} else {
						FlatterEntities.LOGGER.error("Invalid entity blacklist value: {}, Unable to locate entity", value);
					}
				} else {
					FlatterEntities.LOGGER.error("Invalid entity blacklist value: {}, Are you sure this is the resource location of the entity?", value);
				}
			}
		}
		entityDimensionWhitelist.clear();
		for (String value : FlatConfig.CLIENT.entityDimensionWhitelist.get()) {
			if (value.contains(",")) {
				String[] splitValue = value.split(",");
				if(splitValue.length == 2) {
					ResourceLocation entityLocation = ResourceLocation.tryParse(splitValue[0]);
					ResourceLocation worldLocation = ResourceLocation.tryParse(splitValue[1]);
					if(entityLocation != null && worldLocation != null) {
						EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityLocation);
						if (entityType != null) {
							ResourceKey<Level> worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, worldLocation);
							List<EntityType<?>> entityList = entityDimensionWhitelist.getOrDefault(worldKey, new ArrayList<>());
							entityList.add(entityType);
							entityDimensionWhitelist.put(worldKey, entityList);
						} else {
							FlatterEntities.LOGGER.error("Invalid entity dimension whitelist value: {}, Unable to locate entity", value);
						}
					}
				}
			}
		}

		dimensionListIsWhitelist = FlatConfig.CLIENT.invertDimensionBlacklist.get();
		dimensionBlacklist.clear();
		for (String value : FlatConfig.CLIENT.dimensionBlacklist.get()) {
			if(!value.isEmpty()) {
				ResourceLocation resourceLocation = ResourceLocation.tryParse(value);
				if (resourceLocation != null) {
					ResourceKey<Level> worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, resourceLocation);
					dimensionBlacklist.add(worldKey);
				} else {
					FlatterEntities.LOGGER.error("Invalid dimension blacklist value: {}, Are you sure this is the resource location of the dimension?", value);
				}
			}
		}
	}
}