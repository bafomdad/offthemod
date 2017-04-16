package com.bafomdad.off.data;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OffHandler {

	public static OffHandler INSTANCE;
	
	public ConcurrentHashMap<Integer, Set<ISaveInfo>> saveInfo = new ConcurrentHashMap<Integer, Set<ISaveInfo>>();
	
	public void addBlock(World world, BlockPos pos, IBlockState state) {
		
		OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
	}
	
	public void addItem(World world, BlockPos pos, ItemStack stack, int slot) {
		
		OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
	}
	
	public void addTile(World world, BlockPos pos, NBTTagCompound tag) {
		
		OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
	}
	
	public void restore(World world, BlockPos pos, ISaveInfo info) {
		
		OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
	}
}
