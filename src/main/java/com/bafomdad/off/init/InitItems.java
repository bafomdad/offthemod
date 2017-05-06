package com.bafomdad.off.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.off.OffConfig;
import com.bafomdad.off.items.ItemSprayCan;
import com.bafomdad.off.items.ItemSprayCan.SprayType;

public class InitItems {
	
	public static Item sprayCan;

	public static void init() {
		
		sprayCan = new ItemSprayCan();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		
		ModelLoader.setCustomModelResourceLocation(sprayCan, 0, new ModelResourceLocation(sprayCan.getRegistryName() + SprayType.values()[0].name, "inventory"));
		ModelLoader.setCustomModelResourceLocation(sprayCan, 1, new ModelResourceLocation(sprayCan.getRegistryName() + SprayType.values()[1].name, "inventory"));
	}
	
	public static void initRecipes() {
		
		if (!OffConfig.adminsOnly) {
			Item core = (OffConfig.balanceRecipes) ? Items.CLAY_BALL : Items.NETHER_STAR;
			GameRegistry.addRecipe(new ItemStack(sprayCan, 1, 0), "RGR", "GNG", "RGR", 'R', new ItemStack(Items.DYE, 1, EnumDyeColor.ORANGE.getDyeDamage()), 'G', Blocks.GLASS, 'N', core);
			GameRegistry.addRecipe(new ItemStack(sprayCan, 1, 1), "RGR", "GNG", "RGR", 'R', new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()), 'G', Blocks.GLASS, 'N', core);
		}
	}
}
