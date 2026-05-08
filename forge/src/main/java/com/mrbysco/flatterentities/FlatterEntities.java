package com.mrbysco.flatterentities;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Reference.MOD_ID, dist = Dist.CLIENT)
public class FlatterEntities {

	public FlatterEntities(IEventBus eventBus, ModContainer container) {
		container.registerConfig(ModConfig.Type.CLIENT, FlatConfig.clientSpec);
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

		eventBus.addListener(this::onReload);

		eventBus.addListener(Keybinds::registerKeybinds);
		NeoForge.EVENT_BUS.register(new Keybinds());
	}

	private void onReload(final ModConfigEvent configEvent) {
		if (configEvent.getConfig().getModId().equals(Reference.MOD_ID)) {
			FlatConfig.reloadCache();
		}
	}
}