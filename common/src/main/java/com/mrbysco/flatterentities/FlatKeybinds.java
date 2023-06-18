package com.mrbysco.flatterentities;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;

public class FlatKeybinds {
	public static KeyMapping KEY_TOGGLE = new KeyMapping(
			"key." + Reference.MOD_ID + ".toggle",
			Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			"category." + Reference.MOD_ID + ".main");
}
