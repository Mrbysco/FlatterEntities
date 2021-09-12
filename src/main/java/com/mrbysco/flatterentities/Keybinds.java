package com.mrbysco.flatterentities;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
	public static KeyMapping KEY_TOGGLE = new KeyMapping(
			"key." + FlatterEntities.MOD_ID + ".toggle",
			Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			"category." + FlatterEntities.MOD_ID + ".main");

	public static void registerKeybinds(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(KEY_TOGGLE);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		final Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.screen != null && event.getAction() != GLFW.GLFW_PRESS) return;

		if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 292)) return;

		if (KEY_TOGGLE.consumeClick()) {
			Flattener.renderingEnabled = !Flattener.renderingEnabled;
		}
	}
}
