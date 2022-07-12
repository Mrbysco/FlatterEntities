package com.mrbysco.flatterentities;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
	public static void registerKeybinds(final RegisterKeyMappingsEvent event) {
		event.register(FlatKeybinds.KEY_TOGGLE);
	}


	@SubscribeEvent
	public void onKeyInput(InputEvent.Key event) {
		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.screen != null && event.getAction() != GLFW.GLFW_PRESS) return;

		if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 292)) return;

		if (FlatKeybinds.KEY_TOGGLE.consumeClick()) {
			Flattener.renderingEnabled = !Flattener.renderingEnabled;
		}
	}
}
