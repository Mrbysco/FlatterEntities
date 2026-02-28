package com.mrbysco.flatterentities;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;

public class FlatKeybinds {
	public static KeyMapping.Category CATEGORY = new KeyMapping.Category(Reference.modLoc("category"));
	public static final KeyMapping KEY_TOGGLE = new KeyMapping(
			"key." + Reference.MOD_ID + ".toggle",
			Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			CATEGORY);
}
