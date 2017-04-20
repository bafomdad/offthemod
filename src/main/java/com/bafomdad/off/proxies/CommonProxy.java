package com.bafomdad.off.proxies;

import com.bafomdad.off.data.BehaviorSprayCan;
import com.bafomdad.off.init.InitItems;

import net.minecraft.block.BlockDispenser;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		
		InitItems.init();
	}
	
	public void init(FMLInitializationEvent event) {
		
		InitItems.initRecipes();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(InitItems.sprayCan, new BehaviorSprayCan());
	}
	
	public void initAllModels() {}
}
