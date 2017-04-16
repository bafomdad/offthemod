package com.bafomdad.off.proxies;

import com.bafomdad.off.init.InitItems;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		
		InitItems.init();
	}
	
	public void init(FMLInitializationEvent event) {
		
		InitItems.initRecipes();
	}
	
	public void initAllModels() {}
}
