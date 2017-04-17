package com.bafomdad.off.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bafomdad.off.data.savers.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OffHandler {

	public static OffHandler INSTANCE;
	
	public ConcurrentHashMap<Integer, ConcurrentHashMap<BlockPos, Set<ISaveInfo>>> saveInfo = new ConcurrentHashMap<Integer, ConcurrentHashMap<BlockPos, Set<ISaveInfo>>>();
	
	public ConcurrentHashMap<BlockPos, Set<ISaveInfo>> getSaveInfo(int dimId) {
		
		if (saveInfo.get(dimId) == null) {
			ConcurrentHashMap<BlockPos, Set<ISaveInfo>> save = new ConcurrentHashMap<BlockPos, Set<ISaveInfo>>();
			saveInfo.put(dimId, save);
		}
		return saveInfo.get(dimId);
	}
	
	public void refresh(World world, BlockPos pos) {
		
		if (!getSaveInfo(world.provider.getDimension()).containsKey(pos))
			getSaveInfo(world.provider.getDimension()).put(pos, Collections.newSetFromMap(new ConcurrentHashMap<ISaveInfo, Boolean>()));
	}
	
	public void addBlock(World world, BlockPos pos, IBlockState state) {
		
		refresh(world, pos);
		getSaveInfo(world.provider.getDimension()).get(pos).add(new BlockSaver(pos, state));
	}
	
	public void addItem(World world, BlockPos pos, ItemStack stack, int slot) {

		refresh(world, pos);
		getSaveInfo(world.provider.getDimension()).get(pos).add(new ItemSaver(pos, stack, slot));
	}
	
	public void addTile(World world, BlockPos pos, NBTTagCompound tag) {
		
		refresh(world, pos);
		getSaveInfo(world.provider.getDimension()).get(pos).add(new TileSaver(pos, tag));
	}
	
	public void handleErase(World world, BlockPos pos, EnumDataHandler data) {
		
		OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
	}
	
	public void handleRestore(World world, BlockPos pos, ISaveInfo info) {
		
		OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
	}
	
	public void clearQueue(int dimId) {
		
		getSaveInfo(dimId).clear();
	}
	
	public Collection<ISaveInfo> getAllInfo(World world) {
		
		Set<ISaveInfo> ret = Collections.newSetFromMap(new ConcurrentHashMap<ISaveInfo, Boolean>());
		for (Set<ISaveInfo> saveList : getSaveInfo(world.provider.getDimension()).values())
			ret.addAll(saveList);
		
		return ret;
	}
	
	public Set<Integer> getUnsavedDims() {
		
		return saveInfo.keySet();
	}
}
