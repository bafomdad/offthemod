package com.bafomdad.off.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bafomdad.off.OffMain;
import com.bafomdad.off.data.OffHandler;

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
	
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean whatisthis) {
    	
    	switch(stack.getItemDamage()) {
    		case 0: list.add(I18n.format("tooltip." + OffMain.ID + ".off")); return;
    		case 1: list.add(I18n.format("tooltip." + OffMain.ID + ".on")); return;
    	}
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
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		
		if (!player.world.isRemote) {
			if (target instanceof EntityPlayer)
				return false;
			
			if (!canPlayerEdit(player.world, player, stack, target.getPosition(), EnumFacing.UP))
				return false;
			
			OffHandler.getInstance().handleEraseEntity(player.world, target);
			return true;
		}
		return false;
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
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int time) {
    	
    	if (!(player instanceof EntityPlayer))
    		return;
    	
    	RayTraceResult rtr = rayTrace(player.world, (EntityPlayer)player, true);
    	if (rtr == null)
    		return;
    	
    	else if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
    		if (!canPlayerEdit(player.world, (EntityPlayer)player, stack, rtr.getBlockPos(), rtr.sideHit))
    			return;
    		
    		Random rand = new Random();
    		player.world.spawnParticle(EnumParticleTypes.CLOUD, rtr.getBlockPos().offset(rtr.sideHit).getX() + 0.1D + (rand.nextFloat() / 2), rtr.getBlockPos().offset(rtr.sideHit).getY() + 0.1D, rtr.getBlockPos().offset(rtr.sideHit).getZ() + 0.1D + (rand.nextFloat() / 2), 0, 0, 0);
    		if (!player.world.isRemote) {
    			if (stack.getItemDamage() == 0) {
    				OffHandler.getInstance().handleErase(player.world, rtr.getBlockPos());
    				return;
    			}
    			if (stack.getItemDamage() == 1 && time % 5 == 0) {
    				restoreArea(player.world, rtr.getBlockPos());
    			}
    		}
    	}
    }
    
    private void restoreArea(World world, BlockPos posOrigin) {
    	
    	int range = 3;
    	List<BlockPos> poslist = new ArrayList<BlockPos>();
    	
    	for (BlockPos pos : BlockPos.getAllInBox(posOrigin.add(-range, -range, -range), posOrigin.add(range, range, range))) {
    		if (canReplace(world.getBlockState(pos)) || isVanillaInventory(world.getBlockState(pos).getBlock())) {
    			if (OffHandler.getInstance().getSavedInfo(world, pos) != null) {
    				poslist.add(pos);
    			}
    		}
    	}
    	Collections.shuffle(poslist, world.rand);
    	if (!poslist.isEmpty()) {
    		OffHandler.getInstance().handleRestore(world, poslist.get(0));
    	}
    }
    
    private boolean canPlayerEdit(World world, EntityPlayer player, ItemStack stack, BlockPos pos, EnumFacing facing) {
    	
    	if (!world.isBlockModifiable(player, pos))
    		return false;
    	
    	if (!player.canPlayerEdit(pos, facing, stack))
    		return false;
    	
    	return true;
    }
    
    public static boolean isVanillaInventory(Block block) {
    	
    	return block == Blocks.DROPPER || block == Blocks.DISPENSER || block == Blocks.TRAPPED_CHEST || block == Blocks.CHEST || block == Blocks.HOPPER || block instanceof BlockShulkerBox;
    }
    
    public static boolean canReplace(IBlockState state) {
    	
    	Block block = state.getBlock();
    	boolean flowingwater = state.getMaterial() == Material.WATER && block.getMetaFromState(state) != 0;
    	boolean flowinglava = state.getMaterial() == Material.LAVA && block.getMetaFromState(state) != 0;
    	boolean flowingfluids = block instanceof BlockFluidBase && ((BlockFluidBase)block).getMetaFromState(state) != 0;
    	
    	return state.getMaterial() == Material.AIR || flowingwater || flowinglava || flowingfluids;
    }
}
