package com.bafomdad.off.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;

import com.bafomdad.off.OffConfig;
import com.bafomdad.off.data.savers.ISaveInfo;

public class OffLimiter {
	
	private static OffLimiter INSTANCE;

	public static OffLimiter getInstance() {
		
		if (INSTANCE == null)
			INSTANCE = new OffLimiter();
		
		return INSTANCE;
	}
	
	public boolean isOffLimited() {
		
		return OffConfig.offlimit > 0;
	}
	
	public void checkRestoreLimit(World world) {
		
		if (!isOffLimited())
			return;
		
		List<ISaveInfo> list = new ArrayList(OffHandler.getInstance().getAllInfo(world));
		if (!list.isEmpty() && list.size() > OffConfig.offlimit) {
			ISaveInfo info = list.get(0);
			OffHandler.getInstance().handleRestore(world, info.getPos());
		}
	}
}
