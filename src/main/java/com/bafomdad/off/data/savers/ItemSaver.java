package com.bafomdad.off.data.savers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class ItemSaver implements ISaveInfo {
	
	private BlockPos pos;
	private ItemStack stack;
	private int slot;
	
	public ItemSaver(BlockPos pos, ItemStack stack, int slot) {
		
		this.pos = pos;
		this.stack = stack;
		this.slot = slot;
	}

	@Override
	public boolean contains(ISaveInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NBTTagCompound writeNBT() {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("coords", NBTUtil.createPosTag(pos));
		NBTTagCompound itemTag = new NBTTagCompound();
		itemTag.setByte("Slot", (byte)slot);
		stack.writeToNBT(itemTag);
		tag.setTag("item", itemTag);

		return tag;
	}

	@Override
	public ISaveInfo readNBT(NBTTagCompound tag) {
		
		if (tag == null)
			return null;
		
		BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag("coords"));
		NBTTagCompound itemTag = tag.getCompoundTag("item");
		ItemStack stack = new ItemStack(itemTag);
		int slot = itemTag.getByte("Slot");
		
		if (pos != null && (stack != null && !stack.isEmpty()) && slot >= 0)
			return new ItemSaver(pos, stack, slot);
		
		return null;
	}

}
