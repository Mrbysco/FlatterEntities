package com.mrbysco.flatterentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
	public static KeyBinding KEY_TOGGLE = new KeyBinding(
			"key." + FlatterEntities.MOD_ID + ".toggle",
			Type.KEYSYM,
			InputMappings.UNKNOWN.getValue(),
			"category." + FlatterEntities.MOD_ID + ".main");

	public static void registerKeybinds(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(KEY_TOGGLE);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		final Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.screen != null && event.getAction() != GLFW.GLFW_PRESS) return;

		if (InputMappings.isKeyDown(minecraft.getWindow().getWindow(), 292)) return;

		if (KEY_TOGGLE.consumeClick()) {
			Flattener.renderingEnabled = !Flattener.renderingEnabled;
		}
	}
}
