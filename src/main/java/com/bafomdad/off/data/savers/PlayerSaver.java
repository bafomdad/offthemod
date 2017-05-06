package com.bafomdad.off.data.savers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PlayerSaver {

	public ItemStack stack;
	public int slot;
	
	public PlayerSaver(ItemStack stack, int slot) {
		
		this.stack = stack;
		this.slot = slot;
	}
	
	public boolean contains(PlayerSaver info) {
		
		if (!(info instanceof PlayerSaver))
			return false;
		
		boolean item = ItemStack.areItemStackTagsEqual(stack, info.stack);
		boolean slotIndex = info.slot == slot;
		
		return item && slotIndex;
	}
	
	public NBTTagCompound writeNBT() {
		
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound itemTag = new NBTTagCompound();
		itemTag.setByte("Slot", (byte)slot);
		stack.writeToNBT(itemTag);
		tag.setTag("inventory", itemTag);
		
		return tag;
	}
	
	public static PlayerSaver readNBT(NBTTagCompound tag) {
		
		if (tag == null)
			return null;
		
		NBTTagCompound itemTag = tag.getCompoundTag("inventory");
		ItemStack stack = new ItemStack(itemTag);
		int slot = itemTag.getByte("Slot");
		
		if (stack != null && !stack.isEmpty() && slot >= 0)
			return new PlayerSaver(stack, slot);
		
		return null;
	}
}
