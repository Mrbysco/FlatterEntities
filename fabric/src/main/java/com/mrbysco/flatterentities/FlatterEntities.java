package com.mrbysco.flatterentities;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.neoforged.fml.config.ModConfig;

public class FlatterEntities implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ConfigRegistry.INSTANCE.register(Reference.MOD_ID, ModConfig.Type.CLIENT, FlatConfig.clientSpec);

		ModConfigEvents.reloading(Reference.MOD_ID).register((config) -> {
			FlatConfig.reloadCache();
		});

		KeyMappingHelper.registerKeyMapping(FlatKeybinds.KEY_TOGGLE);
		ClientTickEvents.END_CLIENT_TICK.register(Keybinds::onKeyPress);
	}
}
