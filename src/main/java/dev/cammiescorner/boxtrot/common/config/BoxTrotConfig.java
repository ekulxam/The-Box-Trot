package dev.cammiescorner.boxtrot.common.config;

import eu.midnightdust.lib.config.MidnightConfig;

import java.util.ArrayList;
import java.util.List;

public class BoxTrotConfig extends MidnightConfig {
	@Entry public static boolean doesBarrelFoolEndermen = true;
	@Entry public static boolean doesBarrelHideParticles = true;
	@Entry public static boolean doesBarrelFoolMobs = true;
	@Entry public static boolean doesBarrelFoolAttackers = false;
	@Entry public static boolean canOpenPlayerBarrels = true;
	@Entry public static boolean barrelRotates = true;
	@Entry public static boolean barrelSprint = true;
	public static List<Boolean> getConfigValues(){
		List<Boolean> configValues =  new ArrayList<>();
		configValues.add(doesBarrelHideParticles);
		configValues.add(barrelRotates);
		configValues.add(barrelSprint);
		return configValues;
	}
}
