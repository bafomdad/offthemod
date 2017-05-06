package com.bafomdad.off.data.savers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public interface ISaveInfo {
	
	public boolean contains(ISaveInfo info);
	
	public NBTTagCompound writeNBT();
	
	public ISaveInfo readNBT(NBTTagCompound tag);
	
	public BlockPos getPos();
}
