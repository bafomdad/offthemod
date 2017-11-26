package com.bafomdad.off.data;

import com.bafomdad.off.data.savers.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class OffWorldData extends WorldSavedData {
	
	public static final String ID = "OffWorldData";

	public OffWorldData(String name) {
		
		super(name);
	}
	
	public OffWorldData() {
		
		this(ID);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		int[] dims = tag.getIntArray("savedDims");
		
		for (int dimId : dims) {
			NBTTagList savedList = tag.getTagList("savedList" + dimId, 10);
			OffHandler.getInstance().clearQueue(dimId);
			for (int i = 0; i < savedList.tagCount(); i++) {
				NBTTagCompound saveTag = savedList.getCompoundTagAt(i);
				BlockSaver bSaver = new BlockSaver();
				if (bSaver.readNBT(saveTag) != null) {
					bSaver = (BlockSaver)bSaver.readNBT(saveTag);
					OffHandler.getInstance().addBlock(dimId, bSaver.pos, bSaver.state, bSaver.tiledata);
				}
				ItemSaver iSaver = new ItemSaver();
				if (iSaver.readNBT(saveTag) != null) {
					iSaver = (ItemSaver)iSaver.readNBT(saveTag);
					OffHandler.getInstance().addItem(dimId, iSaver.pos, iSaver.stack, iSaver.slot);
				}
				EntitySaver eSaver = new EntitySaver();
				if (eSaver.readNBT(saveTag) != null) {
					eSaver = (EntitySaver)eSaver.readNBT(saveTag);
					OffHandler.getInstance().addEntity(dimId, eSaver.pos, eSaver.name, eSaver.nbt);
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		Integer[] alldims = OffHandler.getInstance().getUnsavedDims().toArray(new Integer[0]);
		int[] savedDims = new int[alldims.length];
		for (int i = 0; i < alldims.length; i++)
			savedDims[i] = alldims[i];
		
		tag.setIntArray("savedDims", savedDims);
		for (int d : savedDims) {
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(d);
			if (world != null) {
				NBTTagList savedList = new NBTTagList();
				for (ISaveInfo info : OffHandler.getInstance().getAllInfo(world)) {
					savedList.appendTag(info.writeNBT());
				}
				tag.setTag("savedList" + d, savedList);
			}
		}
		
		return tag;
	}
	
	public static OffWorldData getInstance(int dimId) {
		
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimId);
		if (world != null) {
			WorldSavedData handler = world.getMapStorage().getOrLoadData(OffWorldData.class, ID);
			if (handler == null) {
				handler = new OffWorldData();
				world.getMapStorage().setData(ID, handler);
			}
			return (OffWorldData)handler;
		}
		return null;
	}
}
