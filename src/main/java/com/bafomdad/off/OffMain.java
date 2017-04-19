package com.bafomdad.off;

import com.bafomdad.off.proxies.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by bafomdad on 4/15/2017.
 */
@Mod(modid=OffMain.ID, name=OffMain.NAME, version=OffMain.VERSION)
public class OffMain {

    public static final String ID = "offthemod";
    public static final String NAME = "Off the Mod";
    public static final String VERSION = "0.1.0";
    
    @SidedProxy(clientSide="com.bafomdad.off.proxies.ClientProxy", serverSide="com.bafomdad.off.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(ID)
    public static OffMain instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    	proxy.preInit(event);
    	proxy.initAllModels();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    	proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
