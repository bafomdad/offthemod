package com.bafomdad.off.data;

import com.bafomdad.off.data.savers.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class OffWorldData extends WorldSavedData{
	
	public static final String ID = "OffWorldData";

	public OffWorldData(String name) {
		
		super(name);
	}
	
	public OffWorldData() {
		
		this(ID);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		int[] dims = tag.getIntArray("dims");
		
		for (int dimId : dims) {
			NBTTagList savedList = tag.getTagList("savedList" + dimId, 10);
			OffHandler.getInstance().clearQueue(dimId);
			for (int i = 0; i < savedList.tagCount(); i++) {
				NBTTagCompound saveTag = savedList.getCompoundTagAt(i);
				BlockSaver bSaver = new BlockSaver();
				if (bSaver.readNBT(saveTag) != null)
					OffHandler.getInstance().addBlock(dimId, bSaver.pos, bSaver.state);
				
				ItemSaver iSaver = new ItemSaver();
				if (iSaver.readNBT(saveTag) != null)
					OffHandler.getInstance().addItem(dimId, iSaver.pos, iSaver.stack, iSaver.slot);
				
				TileSaver tSaver = new TileSaver();
				if (tSaver.readNBT(saveTag) != null)
					OffHandler.getInstance().addTile(dimId, tSaver.pos, tSaver.nbt);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		Integer[] alldims = OffHandler.getInstance().getUnsavedDims().toArray(new Integer[0]);
		int[] savedDims = new int[alldims.length];
		for (int i = 0; i < alldims.length; i++)
			savedDims[i] = alldims[i];
		
		tag.setIntArray("savedList", savedDims);
		for (int d : savedDims) {
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(d);
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
		
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimId);
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
