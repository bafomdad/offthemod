package com.bafomdad.off.data;



import com.bafomdad.off.OffMain;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviorSprayCan extends BehaviorDefaultDispenseItem {

	@Override
	protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		
		World world = source.getWorld();
		EnumFacing facing = world.getBlockState(source.getBlockPos()).getValue(BlockDispenser.FACING);
		BlockPos pos = source.getBlockPos().offset(facing);
		if (stack.getItem() == OffMain.sprayCan) {
			switch(stack.getItemDamage()) {
				case 0: OffHandler.getInstance().handleErase(world, pos); return stack;
				case 1: OffHandler.getInstance().handleRestore(world, pos); return stack;
			}
		}
		return super.dispenseStack(source, stack);
	}
}
