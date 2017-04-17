package com.bafomdad.off.data.savers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileSaver implements ISaveInfo {
	
	public BlockPos pos;
	public NBTTagCompound nbt;
	
	public TileSaver(BlockPos pos, NBTTagCompound nbt) {
		
		this.pos = pos;
		this.nbt = nbt;
	}
	
	public TileSaver() {}

	@Override
	public boolean contains(ISaveInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NBTTagCompound writeNBT() {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("coords", NBTUtil.createPosTag(pos));
		tag.setTag("tile", nbt.copy());
		
		return tag;
	}

	@Override
	public ISaveInfo readNBT(NBTTagCompound tag) {
		
		if (tag == null)
			return null;
		
		BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag("coords"));
		NBTTagCompound nbt = tag.getCompoundTag("tile");
		if (pos != null && nbt != null)
			return new TileSaver(pos, nbt);
		
		return null;
	}

}
