package com.mrbysco.flatterentities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

public class FlatConfig {
	public static class Client {
		public final ConfigValue<List<? extends String>> entityBlacklist;
		public final ConfigValue<List<? extends String>> entityDimensionWhitelist;
		public final ConfigValue<List<? extends String>> dimensionBlacklist;
		public final BooleanValue invertDimensionBlacklist;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.push("client");

			entityBlacklist = builder
					.comment("A list of entities that won't show flat ever [Syntax: \"modid:entity\" ]\n" +
							"[Example: \"minecraft:cow\"]")
					.defineListAllowEmpty(Collections.singletonList("entityBlacklist"), () -> Collections.singletonList(""),
							FlatConfig::isValidResourceLocation);

			entityDimensionWhitelist = builder
					.comment("A list of entities that will show flat even when a dimension is blacklisted [Syntax: \"modid:entity,modid:dimension\" ]\n" +
							"[Example: \"minecraft:bee,minecraft:the_nether\"]")
					.defineListAllowEmpty(Collections.singletonList("entityDimensionWhitelist"), () -> Collections.singletonList(""),
							FlatConfig::isValidOption);

			dimensionBlacklist = builder
					.comment("A list of dimensions that won't have flat entities [Syntax: \"modid:dimension\" ]\n" +
							"[Example: \"minecraft:the_nether\"]")
					.defineListAllowEmpty(Collections.singletonList("dimensionBlacklist"), () -> Collections.singletonList(""),
							FlatConfig::isValidResourceLocation);

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
				return ResourceLocation.tryParse(value) != null;
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
						return ResourceLocation.tryParse(splitValue[0]) != null && ResourceLocation.tryParse(splitValue[1]) != null;
					}
				}
			}
		}
		return false;
	}

	public static final ForgeConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		Reference.LOGGER.debug("Loaded Flatter Entities' config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		Reference.LOGGER.warn("Flatter Entities' config just got changed on the file system!");
	}

	@SubscribeEvent
	public static void onReload(final ModConfigEvent configEvent) {
		if (configEvent.getConfig().getModId().equals(Reference.MOD_ID)) {
			FlatterEntities.reloadCache();
		}
	}
}
