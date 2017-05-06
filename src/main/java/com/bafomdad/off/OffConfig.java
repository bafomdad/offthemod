package com.bafomdad.off;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class OffConfig {

	public static boolean adminsOnly;
	public static boolean balanceRecipes;
//	public static boolean erasePlayers;
	public static int offlimit;
	
	public static void loadConfig(FMLPreInitializationEvent event) {
		

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		adminsOnly = config.get("main", "makeSprayCansAdminsOnly", false, "When this config is enabled, disables crafting recipes for the spray cans.").getBoolean();
		balanceRecipes = config.get("main", "balanceRecipesFairly", false, "If enabled, this will balance the recipes more fairly than the default recipes provided.").getBoolean();
//		erasePlayers = config.get("main", "allowErasingPlayers", false, "If enabled, will let spray cans erase/restore non-vanilla items held by the player's inventory").getBoolean();
		offlimit = config.get("main", "offLimits", 0, "Number of times things can be erased before auto-restoration takes effect. Leave 0 if you don't want this limit.").getInt();
		
		config.save();
	}
}
