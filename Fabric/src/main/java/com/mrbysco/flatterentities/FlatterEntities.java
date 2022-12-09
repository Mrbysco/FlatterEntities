package com.mrbysco.flatterentities;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FlatterEntities implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoConfig.register(FlatConfig.class, Toml4jConfigSerializer::new);

		AutoConfig.getConfigHolder(FlatConfig.class).registerSaveListener((manager, data) -> {
			FlatterEntities.reloadCache();
			return InteractionResult.PASS;
		});

		KeyBindingHelper.registerKeyBinding(FlatKeybinds.KEY_TOGGLE);
		ClientTickEvents.END_CLIENT_TICK.register(client -> Keybinds.onKeyPress(client));

		CommonClass.init();
	}

	public static void reloadCache() {
		FlatConfig config = AutoConfig.getConfigHolder(FlatConfig.class).getConfig();

		Flattener.entityBlacklist.clear();
		for (String value : config.client.entityBlacklist) {
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
		for (String value : config.client.entityDimensionWhitelist) {
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

		Flattener.dimensionListIsWhitelist = config.client.invertDimensionBlacklist;
		Flattener.dimensionBlacklist.clear();
		for (String value : config.client.dimensionBlacklist) {
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
