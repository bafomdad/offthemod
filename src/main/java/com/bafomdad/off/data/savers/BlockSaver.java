package com.bafomdad.off.data.savers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class BlockSaver implements ISaveInfo {
	
	public BlockPos pos;
	public IBlockState state;
	public NBTTagCompound tiledata;
	
	public BlockSaver(BlockPos pos, IBlockState state, NBTTagCompound tiledata) {
		
		this.pos = pos;
		this.state = state;
		this.tiledata = tiledata;
	}
	
	public BlockSaver() {}

	@Override
	public boolean contains(ISaveInfo info) {

		if (!(info instanceof BlockSaver))
			return false;
		
		boolean loc = pos.equals(((BlockSaver)info).pos);
		boolean block = state.equals(((BlockSaver)info).state);
		
		return loc && block;
	}

	@Override
	public NBTTagCompound writeNBT() {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("coords", NBTUtil.createPosTag(pos));
		NBTUtil.writeBlockState(tag, state);
		if (tiledata != null)
			tag.setTag("tile", tiledata);
		
		return tag;
	}

	@Override
	public ISaveInfo readNBT(NBTTagCompound tag) {

		if (tag == null)
			return null;
		
		BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag("coords"));
		IBlockState state = NBTUtil.readBlockState(tag);
		NBTTagCompound tiletag = tag.hasKey("tile") ? tag.getCompoundTag("tile") : null;
		if (pos != null && state != null)
			return new BlockSaver(pos, state, tiletag);
		
		return null;
	}
	
	@Override
	public String toString() {
		
		return "BlockSaver: " + pos.toString() + " / " + state.toString();
	}
}
