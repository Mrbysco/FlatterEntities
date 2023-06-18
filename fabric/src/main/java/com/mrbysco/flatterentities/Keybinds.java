package com.mrbysco.flatterentities;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class Keybinds {

	public static void onKeyPress(Minecraft minecraft) {
		if (minecraft.screen != null) return;

		if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 292)) return;

		if (FlatKeybinds.KEY_TOGGLE.consumeClick()) {
			Flattener.renderingEnabled = !Flattener.renderingEnabled;
		}
	}
}
