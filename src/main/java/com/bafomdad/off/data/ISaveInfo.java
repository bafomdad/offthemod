package com.bafomdad.off.data;

import net.minecraft.nbt.NBTTagCompound;

public interface ISaveInfo {
	
	public boolean contains(ISaveInfo info);
	
	public NBTTagCompound writeNBT();
	
	public ISaveInfo readNBT(NBTTagCompound tag);
}
