package com.mrbysco.flatterentities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

@Mod(Reference.MOD_ID)
public class FlatterEntities {

	public FlatterEntities(IEventBus eventBus, ModContainer container, Dist dist) {
		if (dist.isClient()) {
			container.registerConfig(ModConfig.Type.CLIENT, FlatConfig.clientSpec);
			eventBus.register(FlatConfig.class);

			eventBus.addListener(Keybinds::registerKeybinds);
			NeoForge.EVENT_BUS.register(new Keybinds());
		}
	}

	public static void reloadCache() {
		Flattener.entityBlacklist.clear();
		for (String value : FlatConfig.CLIENT.entityBlacklist.get()) {
			if (!value.isEmpty()) {
				ResourceLocation resourceLocation = ResourceLocation.tryParse(value);
				if (resourceLocation != null) {
					EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);
					if (entityType != null) {
						Flattener.entityBlacklist.add(entityType);
					} else {
						Reference.LOGGER.error("Invalid entity blacklist value: {}, Unable to locate entity", value);
					}
				} else {
					Reference.LOGGER.error("Invalid entity blacklist value: {}, Are you sure this is the resource location of the entity?", value);
				}
			}
		}
		Flattener.entityDimensionWhitelist.clear();
		for (String value : FlatConfig.CLIENT.entityDimensionWhitelist.get()) {
			if (value.contains(",")) {
				String[] splitValue = value.split(",");
				if (splitValue.length == 2) {
					ResourceLocation entityLocation = ResourceLocation.tryParse(splitValue[0]);
					ResourceLocation worldLocation = ResourceLocation.tryParse(splitValue[1]);
					if (entityLocation != null && worldLocation != null) {
						EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityLocation);
						if (entityType != null) {
							ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, worldLocation);
							List<EntityType<?>> entityList = Flattener.entityDimensionWhitelist.getOrDefault(worldKey, new ArrayList<>());
							entityList.add(entityType);
							Flattener.entityDimensionWhitelist.put(worldKey, entityList);
						} else {
							Reference.LOGGER.error("Invalid entity dimension whitelist value: {}, Unable to locate entity", value);
						}
					}
				}
			}
		}

		Flattener.dimensionListIsWhitelist = FlatConfig.CLIENT.invertDimensionBlacklist.get();
		Flattener.dimensionBlacklist.clear();
		for (String value : FlatConfig.CLIENT.dimensionBlacklist.get()) {
			if (!value.isEmpty()) {
				ResourceLocation resourceLocation = ResourceLocation.tryParse(value);
				if (resourceLocation != null) {
					ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, resourceLocation);
					Flattener.dimensionBlacklist.add(worldKey);
				} else {
					Reference.LOGGER.error("Invalid dimension blacklist value: {}, Are you sure this is the resource location of the dimension?", value);
				}
			}
		}
	}
}