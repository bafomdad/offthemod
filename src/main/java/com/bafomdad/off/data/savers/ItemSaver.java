package com.bafomdad.off.data.savers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class ItemSaver implements ISaveInfo {
	
	public BlockPos pos;
	public ItemStack stack;
	public int slot;
	
	public ItemSaver(BlockPos pos, ItemStack stack, int slot) {
		
		this.pos = pos;
		this.stack = stack;
		this.slot = slot;
	}
	
	public ItemSaver() {}

	@Override
	public boolean contains(ISaveInfo info) {

		if (!(info instanceof ItemSaver))
			return false;
		
		boolean loc = pos.equals(((ItemSaver)info).pos);
		boolean item = ItemStack.areItemStackTagsEqual(stack, ((ItemSaver)info).stack);
		boolean slotIndex = ((ItemSaver)info).slot == slot;
		
		return loc && item && slotIndex;
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
