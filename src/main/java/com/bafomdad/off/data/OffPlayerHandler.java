package com.bafomdad.off.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.bafomdad.off.OffMain;
import com.bafomdad.off.data.savers.PlayerSaver;

public class OffPlayerHandler {

	private static OffPlayerHandler INSTANCE;
	
	public ConcurrentHashMap<UUID, Set<PlayerSaver>> playerInfo = new ConcurrentHashMap<UUID, Set<PlayerSaver>>();
	
	public static OffPlayerHandler getInstance() {
		
		if (INSTANCE == null)
			INSTANCE = new OffPlayerHandler();
		
		return INSTANCE;
	}
	
	public Set<PlayerSaver> getPlayerInfo(UUID uuid) {
		
		if (playerInfo.get(uuid) == null) {
			Set<PlayerSaver> save = new HashSet<PlayerSaver>();
			playerInfo.put(uuid, save);
		}
		return playerInfo.get(uuid);
	}
	
	public void addPlayer(UUID uuid, ItemStack stack, int slot) {
		
		getPlayerInfo(uuid).add(new PlayerSaver(stack, slot));
	}
	
	public void handlePlayerErase(EntityPlayer player) {
		
		boolean canWrite = false;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack loopstack = player.inventory.getStackInSlot(i);
			if (!loopstack.isEmpty() && isModded(loopstack)) {
				addPlayer(player.getPersistentID(), loopstack, i);
				player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				canWrite = true;
			}
		}
		if (canWrite)
			OffWorldData.getInstance(0).setDirty(true);
	}
	
	public void handlePlayerRestore(EntityPlayer player) {
		
		Set<PlayerSaver> savedplayer = getPlayerInfo(player.getPersistentID());
		if (savedplayer != null) {
			for (PlayerSaver info : savedplayer) {
				if (player.inventory.getStackInSlot(info.slot).isEmpty())
					player.inventory.setInventorySlotContents(info.slot, info.stack);
				clearPlayerInfo(player, info);
			}
		}
		OffWorldData.getInstance(0).setDirty(true);
	}
	
	public void clearPlayerInfo(EntityPlayer player, PlayerSaver info) {
		
		if (player == null)
			return;
		
		Iterator<PlayerSaver> it = getPlayerInfo(player.getPersistentID()).iterator();
		while (it.hasNext()) {
			PlayerSaver save = it.next();
			if (info.contains(info))
				it.remove();
		}
	}
	
	private boolean isModded(ItemStack stack) {
		
		if (stack.getItem() == OffMain.sprayCan)
			return false;
		
		return !stack.getItem().getRegistryName().getResourceDomain().equals("minecraft");
	}
}
