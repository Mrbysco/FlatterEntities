package com.mrbysco.flatterentities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlatConfig {
	public static class Client {
		public final ModConfigSpec.ConfigValue<List<? extends String>> entityBlacklist;
		public final ModConfigSpec.ConfigValue<List<? extends String>> entityDimensionWhitelist;
		public final ModConfigSpec.ConfigValue<List<? extends String>> dimensionBlacklist;
		public final ModConfigSpec.BooleanValue invertDimensionBlacklist;

		Client(ModConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.push("client");

			entityBlacklist = builder
					.comment("A list of entities that won't show flat ever [Syntax: \"modid:entity\" ]\n" +
							"[Example: \"minecraft:cow\"]")
					.defineListAllowEmpty("entityBlacklist", () -> Collections.singletonList(""),
							String::new, FlatConfig::isValidResourceLocation);

			entityDimensionWhitelist = builder
					.comment("A list of entities that will show flat even when a dimension is blacklisted [Syntax: \"modid:entity,modid:dimension\" ]\n" +
							"[Example: \"minecraft:bee,minecraft:the_nether\"]")
					.defineListAllowEmpty("entityDimensionWhitelist", () -> Collections.singletonList(""),
							String::new, FlatConfig::isValidOption);

			dimensionBlacklist = builder
					.comment("A list of dimensions that won't have flat entities [Syntax: \"modid:dimension\" ]\n" +
							"[Example: \"minecraft:the_nether\"]")
					.defineListAllowEmpty("dimensionBlacklist", () -> Collections.singletonList(""),
							String::new, FlatConfig::isValidResourceLocation);

			invertDimensionBlacklist = builder
					.comment("Invert the Dimension Blacklist")
					.define("invertDimensionBlacklist", false);

			builder.pop();
		}
	}

	private static boolean isValidResourceLocation(Object object) {
		boolean flag = object instanceof String;
		if (flag) {
			String value = (String) object;
			if (value.isEmpty()) {
				return true;
			} else {
				return Identifier.tryParse(value) != null;
			}
		}
		return false;
	}

	public static boolean isValidOption(Object object) {
		boolean flag = object instanceof String;
		if (flag) {
			String value = (String) object;
			if (value.isEmpty()) {
				return true;
			} else {
				if (value.contains(",")) {
					String[] splitValue = value.split(",");
					if (splitValue.length == 2) {
						return Identifier.tryParse(splitValue[0]) != null && Identifier.tryParse(splitValue[1]) != null;
					}
				}
			}
		}
		return false;
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static void reloadCache() {
		Flattener.entityBlacklist.clear();
		for (String value : FlatConfig.CLIENT.entityBlacklist.get()) {
			if (!value.isEmpty()) {
				Identifier resourceLocation = Identifier.tryParse(value);
				if (resourceLocation != null) {
					EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(resourceLocation);
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
					Identifier entityLocation = Identifier.tryParse(splitValue[0]);
					Identifier worldLocation = Identifier.tryParse(splitValue[1]);
					if (entityLocation != null && worldLocation != null) {
						EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(entityLocation);
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
				Identifier resourceLocation = Identifier.tryParse(value);
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
