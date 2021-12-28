package com.mrbysco.flatterentities;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Config(name = Reference.MOD_ID)
public class FlatConfig implements ConfigData {
	@CollapsibleObject
	public Client client = new Client();

	public static class Client {
		@Comment("A list of entities that won't show flat ever [Syntax: \"modid:entity\" ]\n" +
				"[Example: \"minecraft:cow\"]")
		public List<String> entityBlacklist = new ArrayList<>();

		@Comment("A list of entities that will show flat even when a dimension is blacklisted [Syntax: \"modid:entity,modid:dimension\" ]\n" +
				"[Example: \"minecraft:bee,minecraft:the_nether\"]")
		public List<String> entityDimensionWhitelist = new ArrayList<>();

		@Comment("A list of dimensions that won't have flat entities [Syntax: \"modid:dimension\" ]\n" +
				"[Example: \"minecraft:the_nether\"]")
		public List<String> dimensionBlacklist = new ArrayList<>();

		@Comment("Invert the Dimension Blacklist")
		public boolean invertDimensionBlacklist = false;
	}

	private static boolean isValidResourceLocation(String configValue) {
		return configValue.isEmpty() || ResourceLocation.tryParse(configValue) != null;
	}

	public static boolean isValidOption(String configValue) {
		String value = configValue;
		if(value.isEmpty()) {
			return true;
		} else {
			if(value.contains(",")) {
				String[] splitValue = value.split(",");
				if(splitValue.length == 2) {
					return ResourceLocation.tryParse(splitValue[0]) != null && ResourceLocation.tryParse(splitValue[1]) != null;
				}
			}
		}
		return false;
	}

	@Override
	public void validatePostLoad() throws ValidationException {
		List<String> entityBlacklist = client.entityBlacklist;
		if(!entityBlacklist.isEmpty()) {
			for (ListIterator<String> iter = entityBlacklist.listIterator(); iter.hasNext(); ) {
				String value = iter.next();
				if(!isValidResourceLocation(value)) {
					iter.set("");
				}
			}
			client.entityBlacklist = entityBlacklist;
		}

		List<String> entityDimensionWhitelist = client.entityDimensionWhitelist;
		if(!entityDimensionWhitelist.isEmpty()) {
			for (ListIterator<String> iter = entityDimensionWhitelist.listIterator(); iter.hasNext(); ) {
				String value = iter.next();
				if(!isValidOption(value)) {
					iter.set("");
				}
			}
			client.entityDimensionWhitelist = entityDimensionWhitelist;
		}

		List<String> dimensionBlacklist = client.dimensionBlacklist;
		if(!dimensionBlacklist.isEmpty()) {
			for (ListIterator<String> iter = dimensionBlacklist.listIterator(); iter.hasNext(); ) {
				String value = iter.next();
				if(!isValidResourceLocation(value)) {
					iter.set("");
				}
			}
			client.dimensionBlacklist = dimensionBlacklist;
		}
	}
}
