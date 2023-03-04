package dev.cammiescorner.boxtrot.common.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class BoxTrotConfig extends MidnightConfig {
	@Entry public static boolean doesBarrelFoolEndermen = true;
	@Entry public static boolean doesBarrelHideParticles = true;
	@Entry public static boolean doesBarrelFoolMobs = true;
	@Entry public static boolean doesBarrelFoolAttackers = false;
	@Entry public static boolean canOpenPlayerBarrels = true;
}
