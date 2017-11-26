package com.bafomdad.off;

import com.bafomdad.off.items.ItemSprayCan;
import com.bafomdad.off.items.ItemSprayCan.SprayType;
import com.bafomdad.off.proxies.CommonProxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by bafomdad on 4/15/2017.
 */
@Mod.EventBusSubscriber
@Mod(modid=OffMain.ID, name=OffMain.NAME, version=OffMain.VERSION)
public class OffMain {

    public static final String ID = "offthemod";
    public static final String NAME = "Off the Mod";
    public static final String VERSION = "@VERSION@";
    
    @SidedProxy(clientSide="com.bafomdad.off.proxies.ClientProxy", serverSide="com.bafomdad.off.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(ID)
    public static OffMain instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	
		event.getModMetadata().version = VERSION;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    	proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	
    	proxy.postInit(event);
    }
    
	public static Item sprayCan;
	
	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event) {
		
		sprayCan = new ItemSprayCan();
		event.getRegistry().register(sprayCan);
	}
    
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
    	
		ModelLoader.setCustomModelResourceLocation(sprayCan, 0, new ModelResourceLocation(sprayCan.getRegistryName() + SprayType.values()[0].name, "inventory"));
		ModelLoader.setCustomModelResourceLocation(sprayCan, 1, new ModelResourceLocation(sprayCan.getRegistryName() + SprayType.values()[1].name, "inventory"));
    }
    
	public static void initRecipes() {
		
		if (!OffConfig.adminsOnly) {
			Item core = (OffConfig.balanceRecipes) ? Items.CLAY_BALL : Items.NETHER_STAR;
			GameRegistry.addShapedRecipe(new ResourceLocation(OffMain.ID, "offspraycan"), null, new ItemStack(sprayCan, 1, 0), "RGR", "GNG", "RGR", 'R', new ItemStack(Items.DYE, 1, EnumDyeColor.ORANGE.getDyeDamage()), 'G', Blocks.GLASS, 'N', core);
			GameRegistry.addShapedRecipe(new ResourceLocation(OffMain.ID, "onspraycan"), null, new ItemStack(sprayCan, 1, 1), "RGR", "GNG", "RGR", 'R', new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()), 'G', Blocks.GLASS, 'N', core);
		}
	}
	
	@Config(modid=OffMain.ID)
	public static class OffConfig {

		@Comment({"When this config is enabled, disables crafting recipes for the spray cans."})
		public static boolean adminsOnly = false;
		@Comment({"If enabled, this will balance the recipes more fairly than the default recipes provided."})
		public static boolean balanceRecipes = false;
		@Comment({"Number of objects that can be erased before auto-restoration on the oldest erased objects takes effect. Leave 0 if you don't want this limit."})
		public static int offlimit = 0;
	}
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		
		if (event.getModID().equals(OffMain.ID))
			ConfigManager.load(OffMain.ID, Config.Type.INSTANCE);
	}
}
