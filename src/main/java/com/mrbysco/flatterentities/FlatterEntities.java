package com.mrbysco.flatterentities;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FlatterEntities.MOD_ID)
public class FlatterEntities {
	public static final String MOD_ID = "flatterentities";
	public static final Logger LOGGER = LogManager.getLogger();

	public FlatterEntities() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FlatConfig.clientSpec);
		eventBus.register(FlatConfig.class);

		//Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
				() -> "Trans Rights Are Human Rights",
				(remoteVersionString, networkBool) -> networkBool
		));
	}
}
