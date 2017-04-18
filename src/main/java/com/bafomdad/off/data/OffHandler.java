package com.bafomdad.off.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bafomdad.off.data.savers.*;
import com.bafomdad.off.items.ItemSprayCan;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class OffHandler {

	private static OffHandler INSTANCE;
	
	public ConcurrentHashMap<Integer, ConcurrentHashMap<BlockPos, Set<ISaveInfo>>> saveInfo = new ConcurrentHashMap<Integer, ConcurrentHashMap<BlockPos, Set<ISaveInfo>>>();
	
	public static OffHandler getInstance() {
		
		if (INSTANCE == null)
			INSTANCE = new OffHandler();
		
		return INSTANCE;
	}
	
	public ConcurrentHashMap<BlockPos, Set<ISaveInfo>> getSaveInfo(int dimId) {
		
		if (saveInfo.get(dimId) == null) {
			ConcurrentHashMap<BlockPos, Set<ISaveInfo>> save = new ConcurrentHashMap<BlockPos, Set<ISaveInfo>>();
			saveInfo.put(dimId, save);
		}
		return saveInfo.get(dimId);
	}
	
	public void refresh(int dimId, BlockPos pos) {
		
		if (!getSaveInfo(dimId).containsKey(pos))
			getSaveInfo(dimId).put(pos, Collections.newSetFromMap(new ConcurrentHashMap<ISaveInfo, Boolean>()));
	}
	
	public void addBlock(int dimId, BlockPos pos, IBlockState state, NBTTagCompound tag) {
		
		refresh(dimId, pos);
		getSaveInfo(dimId).get(pos).add(new BlockSaver(pos, state, tag));
	}
	
	public void addItem(int dimId, BlockPos pos, ItemStack stack, int slot) {

		refresh(dimId, pos);
		getSaveInfo(dimId).get(pos).add(new ItemSaver(pos, stack, slot));
	}
	
	public void handleErase(World world, BlockPos pos) {
		
		int dimId = world.provider.getDimension();
		if (ItemSprayCan.isVanillaInventory(world.getBlockState(pos).getBlock())) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof TileEntityLockableLoot) {
				TileEntityLockableLoot inv = (TileEntityLockableLoot)tile;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack loopstack = inv.getStackInSlot(i);
					if (!loopstack.isEmpty() && isModded(loopstack.getItem())) {
						OffHandler.getInstance().addItem(dimId, pos, loopstack, i);
						inv.setInventorySlotContents(i, ItemStack.EMPTY);
					}
				}
			}
			OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
			return;
		}
		if (world.isAirBlock(pos) || !isModded(world.getBlockState(pos).getBlock()))
			return;
		
		else {
			TileEntity tile = world.getTileEntity(pos);
			NBTTagCompound tileTag = null;
			if (tile != null) {
				tileTag = tile.writeToNBT(new NBTTagCompound());
				world.removeTileEntity(pos);
			}
			OffHandler.getInstance().addBlock(dimId, pos, world.getBlockState(pos), tileTag);
			world.setBlockToAir(pos);
			OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
			return;
		}
	}
	
	public void handleItemRestore(World world, BlockPos pos) {
		
		Set<ISaveInfo> savedInfo = getSavedInfo(world, pos);
		if (savedInfo != null) {
			for (ISaveInfo info: savedInfo) {
				if (info instanceof ItemSaver) {
					ItemSaver iSaver = (ItemSaver)info;
					TileEntity tile = world.getTileEntity(pos);
					if (tile != null && tile instanceof TileEntityLockableLoot) {
						if (iSaver.slot < ((TileEntityLockableLoot)tile).getSizeInventory())
							((TileEntityLockableLoot)tile).setInventorySlotContents(iSaver.slot, iSaver.stack);
						clearSavedInfo(world, info);
					}
				}
			}
		}
	}
	
	public void handleRestore(World world, BlockPos pos) {
		
		Set<ISaveInfo> savedInfo = getSavedInfo(world, pos);
		if (savedInfo != null) {
			for (ISaveInfo info: savedInfo) {
				if (info instanceof BlockSaver) {
					Block block = world.getBlockState(pos).getBlock();
					BlockPos toPlace = null;
					if (canReplace(block)) {
						BlockSaver bSaver = (BlockSaver)info;
						world.setBlockState(bSaver.pos, bSaver.state, 2);
						if (bSaver.tiledata != null) {
							TileEntity tile = TileEntity.create(world, bSaver.tiledata);
							world.setTileEntity(bSaver.pos, tile);
						}
						clearSavedInfo(world, info);
					}
					else {
						clearSavedInfo(world, info);
					}
				}
			}
		}
		OffWorldData.getInstance(world.provider.getDimension()).setDirty(true);
	}
	
	public void clearSavedInfo(World world, ISaveInfo info) {
		
		if (info == null || world == null)
			return;
		
		for (Set<ISaveInfo> loopinfo : getSaveInfo(world.provider.getDimension()).values()) {
			
			Iterator<ISaveInfo> it = loopinfo.iterator();
			while (it.hasNext()) {
				ISaveInfo save = it.next();
				if (info.contains(save)) {
					it.remove();
				}
			}
		}
	}
	
	public void clearQueue(int dimId) {
		
		getSaveInfo(dimId).clear();
	}
	
	public synchronized Set<ISaveInfo> getSavedInfo(World world, BlockPos pos) {
		
		if (world == null)
			return null;
		
		ConcurrentHashMap<BlockPos, Set<ISaveInfo>> info = getSaveInfo(world.provider.getDimension());
		if (info.containsKey(pos))
			return info.get(pos);
		
		return null;
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
	
    private boolean isModded(Object obj) {
    	
    	if (obj instanceof Block)
    		return !((Block)obj).getRegistryName().getResourceDomain().equals("minecraft");
    	if (obj instanceof Item)
    		return !((Item)obj).getRegistryName().getResourceDomain().equals("minecraft");
    	
    	return false;
    }
    
    private boolean canReplace(Block block) {
    	
    	return block == Blocks.AIR || block == Blocks.FLOWING_WATER || block == Blocks.FLOWING_LAVA;
    }
}
