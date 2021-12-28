package com.mrbysco.flatterentities;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
	public static void registerKeybinds(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(FlatKeybinds.KEY_TOGGLE);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		final Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.screen != null && event.getAction() != GLFW.GLFW_PRESS) return;

		if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 292)) return;

		if (FlatKeybinds.KEY_TOGGLE.consumeClick()) {
			Flattener.renderingEnabled = !Flattener.renderingEnabled;
		}
	}
}
