package com.bafomdad.off.proxies;

import com.bafomdad.off.OffMain;
import com.bafomdad.off.data.BehaviorSprayCan;

import net.minecraft.block.BlockDispenser;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {}
	
	public void init(FMLInitializationEvent event) {
		
		OffMain.initRecipes();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(OffMain.sprayCan, new BehaviorSprayCan());
	}
}
