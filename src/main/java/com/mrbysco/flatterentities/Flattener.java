package com.mrbysco.flatterentities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flattener {
	private static final List<EntityType<?>> entityBlacklist = new ArrayList<>();
	private static final Map<RegistryKey<World>, List<EntityType<?>>> entityDimensionWhitelist = new HashMap<>();
	private static final List<RegistryKey<World>> dimensionBlacklist = new ArrayList<>();
	private static boolean dimensionListIsWhitelist = false;

	public static void prepareFlatRender(double x, double z, float f, MatrixStack poseStack, Entity entityIn) {
		final EntityType<?> entityType = entityIn.getType();
		final RegistryKey<World> entityDimension = entityIn.getEntityWorld().getDimensionKey();
		final boolean entityInList = entityBlacklist.contains(entityIn.getType());
		final boolean worldInList = dimensionBlacklist.contains(entityDimension);
		boolean entityBlacklisted = entityBlacklist.isEmpty() || (!entityBlacklist.isEmpty() && entityInList);
		boolean dimensionFlat = dimensionBlacklist.isEmpty() || (!dimensionBlacklist.isEmpty() && dimensionListIsWhitelist == worldInList);
		boolean renderAnyway = false;

		if(!dimensionBlacklist.isEmpty() && !entityDimensionWhitelist.isEmpty()) {
			List<EntityType<?>> whitelist = entityDimensionWhitelist.getOrDefault(entityDimension, new ArrayList<>());
			renderAnyway = whitelist.contains(entityType) && !entityBlacklisted;
		}

		if (!entityBlacklisted && (dimensionFlat || renderAnyway)) {
			double angle1 = Math.atan2(z, x) / Math.PI * 180.0D;
			double angle2 = Math.floor((f - angle1) / 45.0D) * 45.0D;

			poseStack.rotate(Vector3f.YP.rotationDegrees((float) angle1));
			poseStack.scale(0.02F, 1.0F, 1.0F);
			poseStack.rotate(Vector3f.YP.rotationDegrees((float) angle2));
		}
	}

	public static void reloadCache () {
		entityBlacklist.clear();
		for (String value : FlatConfig.CLIENT.entityBlacklist.get()) {
			if(!value.isEmpty()) {
				ResourceLocation resourceLocation = ResourceLocation.tryCreate(value);
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
					ResourceLocation entityLocation = ResourceLocation.tryCreate(splitValue[0]);
					ResourceLocation worldLocation = ResourceLocation.tryCreate(splitValue[1]);
					if(entityLocation != null && worldLocation != null) {
						EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityLocation);
						if (entityType != null) {
							RegistryKey<World> worldKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, worldLocation);
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
				ResourceLocation resourceLocation = ResourceLocation.tryCreate(value);
				if (resourceLocation != null) {
					RegistryKey<World> worldKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, resourceLocation);
					dimensionBlacklist.add(worldKey);
				} else {
					FlatterEntities.LOGGER.error("Invalid dimension blacklist value: {}, Are you sure this is the resource location of the dimension?", value);
				}
			}
		}
	}
}