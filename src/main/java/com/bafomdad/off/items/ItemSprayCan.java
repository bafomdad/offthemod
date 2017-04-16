package com.bafomdad.off.items;

import com.bafomdad.off.OffMain;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;

public class ItemSprayCan extends Item {
	
	public enum SprayType {
		
		OFF("off"),
		ON("on");
		
		public final String name;
		
		private SprayType(String name) {
			
			this.name = name;
		}
	}

	public ItemSprayCan() {
		
		setRegistryName("spraycan");
		setUnlocalizedName(OffMain.ID + ".spraycan");
		setHasSubtypes(true);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.TOOLS);
		GameRegistry.register(this);
		this.addPropertyOverride(new ResourceLocation("spraying"), new IItemPropertyGetter() {
			
			@Override
			public float apply(ItemStack stack, World world, EntityLivingBase entity) {
				
				boolean flag = entity != null && entity.getActiveItemStack() == stack;
				
				if (stack.getItemDamage() == 1 && !flag) {

					return 0.1F;
				}
				else if (stack.getItemDamage() == 1 && flag) {

					return 0.3F;
				}
 				if (flag)
					return 0.2F;
				
				return 0;
			}
		});
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		
		return getUnlocalizedName() + "." + SprayType.values()[stack.getItemDamage()].name;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}
	
    public int getMaxItemUseDuration(ItemStack stack) {
    	
        return 2000;
    }
	
	@Override
    public EnumAction getItemUseAction(ItemStack stack) {
       
		return EnumAction.DRINK;
    }
	
    @Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() == this) {
			player.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult(EnumActionResult.PASS, stack);
	}
    
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
    	
    	RayTraceResult rtr = player.rayTrace(10, 5);
    	if (rtr == null)
    		return;
    	
//    	if (rtr.typeOfHit == RayTraceResult.Type.ENTITY) {
//    		System.out.println(rtr.entityHit);
//    	}
    	else if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
    		System.out.println(rtr.hitVec);
    	}
    }
}
