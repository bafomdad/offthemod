package com.bafomdad.off.data.savers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class EntitySaver implements ISaveInfo {
	
	public BlockPos pos;
	public String name;
	public NBTTagCompound nbt;
	
	public EntitySaver(BlockPos pos, String name, NBTTagCompound nbt) {
		
		this.pos = pos;
		this.name = name;
		this.nbt = nbt;
	}
	
	public EntitySaver() {}

	@Override
	public boolean contains(ISaveInfo info) {

		if (!(info instanceof EntitySaver))
			return false;
		
		boolean loc = pos.equals(((EntitySaver)info).pos);
		boolean str = name.equals(((EntitySaver)info).name);
		
		return loc && str;
	}

	@Override
	public NBTTagCompound writeNBT() {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("coords", NBTUtil.createPosTag(pos));
		tag.setString("entityname", name);
		tag.setTag("entitydata", nbt);
		
		return tag;
	}

	@Override
	public ISaveInfo readNBT(NBTTagCompound tag) {

		if (tag == null)
			return null;
		
		BlockPos pos = NBTUtil.getPosFromTag(tag.getCompoundTag("coords"));
		String str = tag.getString("entityname");
		NBTTagCompound nbt = tag.getCompoundTag("entitydata");
		if (pos != null && (str != null && !str.isEmpty()) && nbt != null)
			return new EntitySaver(pos, str, nbt);
		
		return null;
	}
	
	@Override
	public BlockPos getPos() {
		
		return pos;
	}
}
