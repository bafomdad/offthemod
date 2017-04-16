package com.bafomdad.off.data;

import net.minecraft.nbt.NBTTagCompound;
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

		
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

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
