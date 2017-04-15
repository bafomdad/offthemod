package com.bafomdad.off;

import net.minecraftforge.fml.common.Mod;
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
    public static final String VERSION = "0.0.1";

    @Mod.Instance(ID)
    public static OFfMain instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
