package com.github.jikoo.specialdispensation;

import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.BlockCocoa;
import net.minecraft.server.v1_9_R2.BlockDispenser;
import net.minecraft.server.v1_9_R2.BlockLog1;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.BlockWood.EnumLogVariant;
import net.minecraft.server.v1_9_R2.Blocks;
import net.minecraft.server.v1_9_R2.EnumColor;
import net.minecraft.server.v1_9_R2.EnumDirection;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.ISourceBlock;
import net.minecraft.server.v1_9_R2.ItemDye;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.World;

/**
 * CropDispenseBehavior for cocoa beans with additional handling for bonemeal and other dyes.
 * 
 * @author Jikoo
 */
public class DyeDispenseBehavior extends CropDispenseBehavior {

	public DyeDispenseBehavior() {
		super(Blocks.COCOA, Blocks.LOG, new EnumDirection[] { EnumDirection.NORTH,
				EnumDirection.SOUTH, EnumDirection.EAST, EnumDirection.WEST });
	}

	@Override
	protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
		if (this.changedDuringEvent(isourceblock, itemstack)) {
			return itemstack;
		}

		EnumColor color = EnumColor.fromInvColorIndex(itemstack.getData());
		if (color == EnumColor.BROWN) {
			// Cocoa, plant
			return this.handlePlanting(isourceblock, itemstack);
		}

		if (color != EnumColor.WHITE) {
			// Not bonemeal, use default DispenseItemBehavior
			return this.superBee(isourceblock, itemstack);
		}

		// Bonemeal, see DispenserRegistry
		final World world = isourceblock.getWorld();
		final BlockPosition blockposition = isourceblock.getBlockPosition().shift(BlockDispenser.e(isourceblock.f()));
		if (ItemDye.a(itemstack, world, blockposition)) {
			if (!world.isClientSide) {
				world.triggerEffect(2005, blockposition, 0);
			}
		} else {
			this.b = false;
		}
		return itemstack;
	}

	@Override
	protected boolean isSoil(IBlockData blockData) {
		return super.isSoil(blockData) && blockData.get(BlockLog1.VARIANT) == EnumLogVariant.JUNGLE;
	}

	@Override
	protected IBlockData getBlockData(Block block, EnumDirection soilDirection) {
		return block.getBlockData().set(BlockCocoa.FACING, soilDirection).set(BlockCocoa.AGE, 0);
	}
}
