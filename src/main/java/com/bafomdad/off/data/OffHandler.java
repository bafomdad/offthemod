package com.bafomdad.off.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bafomdad.off.data.savers.*;
import com.bafomdad.off.items.ItemSprayCan;

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
	
	public void addEntity(int dimId, BlockPos pos, String name, NBTTagCompound tag) {
		
		refresh(dimId, pos);
		getSaveInfo(dimId).get(pos).add(new EntitySaver(pos, name, tag));
	}
	
	public void handleErase(World world, BlockPos pos) {
		
		OffLimiter.getInstance().checkRestoreLimit(world);
		int dimId = world.provider.getDimension();
		if (ItemSprayCan.isVanillaInventory(world.getBlockState(pos).getBlock())) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null && tile instanceof TileEntityLockableLoot) {
				TileEntityLockableLoot inv = (TileEntityLockableLoot)tile;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack loopstack = inv.getStackInSlot(i);
					if (!loopstack.isEmpty() && isModded(loopstack.getItem())) {
						addItem(dimId, pos, loopstack, i);
						inv.setInventorySlotContents(i, ItemStack.EMPTY);
					}
				}
			}
			OffWorldData.getInstance(dimId).setDirty(true);
			return;
		}
		if (ItemSprayCan.canReplace(world.getBlockState(pos)) || !isModded(world.getBlockState(pos).getBlock()))
			return;
		
		else {
			TileEntity tile = world.getTileEntity(pos);
			NBTTagCompound tileTag = null;
			if (tile != null) {
				tileTag = tile.writeToNBT(new NBTTagCompound());
				world.removeTileEntity(pos);
			}
			addBlock(dimId, pos, world.getBlockState(pos), tileTag);
			if (getMultiplaceState(world.getBlockState(pos)) != null) {
				handleMultiplaceState(world, pos, world.getBlockState(pos), false);
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
			else
				world.setBlockToAir(pos);	
			OffWorldData.getInstance(dimId).setDirty(true);
			return;
		}
	}
	
	public void handleEraseEntity(World world, EntityLivingBase target) {
		
		int dimId = world.provider.getDimension();
		BlockPos pos = target.getPosition();
		if (isModded(target)) {
			ResourceLocation res = EntityList.getKey(target);
			NBTTagCompound tag = target.writeToNBT(new NBTTagCompound());
			tag.setString("id", res.toString());
			addEntity(dimId, pos, res.toString(), tag);
			target.setDead();
		}
		OffWorldData.getInstance(dimId).setDirty(true);
	}
	
	public void handleRestoreEntity(World world, BlockPos pos, ISaveInfo info) {
		
		EntitySaver eSaver = (EntitySaver)info;
		Entity entity = EntityList.createEntityFromNBT(eSaver.nbt, world);
		if (entity != null) {
			NBTTagList taglist = eSaver.nbt.getTagList("Pos", 6);
			entity.setPosition(taglist.getDoubleAt(0), taglist.getDoubleAt(1), taglist.getDoubleAt(2));
			world.spawnEntity(entity);
		}
		clearSavedInfo(world, info);
	}
	
	public void handleRestoreItem(World world, BlockPos pos, ISaveInfo info) {
		
		ItemSaver iSaver = (ItemSaver)info;
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityLockableLoot) {
			if (iSaver.slot < ((TileEntityLockableLoot)tile).getSizeInventory() && ((TileEntityLockableLoot)tile).getStackInSlot(iSaver.slot).isEmpty() && !((TileEntityLockableLoot)tile).isLocked())
				((TileEntityLockableLoot)tile).setInventorySlotContents(iSaver.slot, iSaver.stack);
		}
		clearSavedInfo(world, info);
	}
	
	public void handleRestoreBlock(World world, BlockPos pos, ISaveInfo info) {
		
		if (ItemSprayCan.canReplace(world.getBlockState(pos))) {
			BlockSaver bSaver = (BlockSaver)info;
			world.setBlockState(bSaver.pos, bSaver.state, 2);
			handleMultiplaceState(world, bSaver.pos, bSaver.state, true);
			if (bSaver.tiledata != null) {
				TileEntity tile = TileEntity.create(world, bSaver.tiledata);
				world.setTileEntity(bSaver.pos, tile);
			}
			clearSavedInfo(world, info);
		}
		else
			clearSavedInfo(world, info);
	}
	
	public void handleMultiplaceState(World world, BlockPos pos, IBlockState state, boolean restore) {
		
		if (getMultiplaceState(state) == null)
			return;
		
		PropertyEnum prop = getMultiplaceState(state);
		String s = state.getValue(prop).toString();
		BlockPos copypos = BlockPos.ORIGIN;

		if (s.equals("lower"))
			copypos = pos.up();
		if (s.equals("upper"))
			copypos = pos.down();
		if (s.equals("head"))
			copypos = pos.offset(state.getValue(BlockHorizontal.FACING).getOpposite());
		if (s.equals("foot"))
			copypos = pos.offset(state.getValue(BlockHorizontal.FACING));
		
		if (!copypos.equals(BlockPos.ORIGIN)) {
			if (restore)
				world.setBlockState(copypos, state.cycleProperty(prop), 2);
			else
				world.setBlockState(copypos, Blocks.AIR.getDefaultState(), 2);
//				world.setBlockToAir(copypos);
		}
	}
	
	public void handleRestore(World world, BlockPos pos) {
		
		Set<ISaveInfo> savedInfo = getSavedInfo(world, pos);
		if (savedInfo != null) {
			for (ISaveInfo info: savedInfo) {
				if (info instanceof BlockSaver)
					handleRestoreBlock(world, pos, info);
				
				else if (info instanceof ItemSaver)
					handleRestoreItem(world, pos, info);
				
				else if (info instanceof EntitySaver)
					handleRestoreEntity(world, pos, info);
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
		if (info.containsKey(pos) && info.get(pos).size() > 0)
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
	
	private PropertyEnum getMultiplaceState(IBlockState state) {
		
		if (state.getProperties().isEmpty())
			return null;
		
		Block block = state.getBlock();
		if (block instanceof BlockBed)
			return BlockBed.PART;
		
		if (block instanceof BlockDoor)
			return BlockDoor.HALF;
		
		if (block instanceof BlockDoublePlant)
			return BlockDoublePlant.HALF;
		
		return null;
	}
	
    private boolean isModded(Object obj) {
    	
    	if (obj instanceof Block)
    		return !((Block)obj).getRegistryName().getResourceDomain().equals("minecraft");
    	if (obj instanceof Item)
    		return !((Item)obj).getRegistryName().getResourceDomain().equals("minecraft");
    	if (obj instanceof EntityLivingBase)
    		return !EntityList.getKey((EntityLivingBase)obj).getResourceDomain().equals("minecraft");
    	
    	return false;
    }
}
