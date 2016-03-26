package com.github.jikoo.specialdispensation;

import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.BlockDispenser;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.DispenseBehaviorItem;
import net.minecraft.server.v1_9_R1.EnumDirection;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.IDispenseBehavior;
import net.minecraft.server.v1_9_R1.ISourceBlock;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;

/**
 * IDispenseBehavior for crop planting.
 * 
 * @author Jikoo
 */
public class CropDispenseBehavior extends DispenseBehaviorItem {

	private final Block cropBlock, soilBlock;
	private final EnumDirection[] soilDirections;

	protected boolean b = true;

	public CropDispenseBehavior(Block cropBlock, Block soilBlock) {
		this(cropBlock, soilBlock, new EnumDirection[] { EnumDirection.DOWN });
	}

	public CropDispenseBehavior(Block cropBlock, Block soilBlock, EnumDirection[] soilDirections) {
		this.cropBlock = cropBlock;
		this.soilBlock = soilBlock;
		this.soilDirections = soilDirections;
	}

	@Override
	protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
		if (this.changedDuringEvent(isourceblock, itemstack)) {
			return itemstack;
		}
		return handlePlanting(isourceblock, itemstack);
	}

	protected ItemStack superBee(ISourceBlock isourceblock, ItemStack itemstack) {
		return super.b(isourceblock, itemstack);
	}

	protected boolean changedDuringEvent(ISourceBlock isourceblock, ItemStack itemstack) {
		World world = isourceblock.getWorld();

		org.bukkit.block.Block block = world.getWorld().getBlockAt(
				isourceblock.getBlockPosition().getX(),
				isourceblock.getBlockPosition().getY(),
				isourceblock.getBlockPosition().getZ());
		CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

		BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(),
				new Vector(0, 0, 0));
		if (!BlockDispenser.eventFired) {
			world.getServer().getPluginManager().callEvent(event);
		}

		if (event.isCancelled()) {
			return true;
		}

		if (!event.getItem().equals(craftItem)) {
			ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
			IDispenseBehavior idispensebehavior = BlockDispenser.REGISTRY.get(eventStack.getItem());
			if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
				idispensebehavior.a(isourceblock, eventStack);
				return true;
			}
		}
		return false;
	}

	protected ItemStack handlePlanting(ISourceBlock isourceblock, ItemStack itemstack) {
		World world = isourceblock.getWorld();
		BlockPosition toCrop = isourceblock.getBlockPosition().shift(BlockDispenser.e(isourceblock.f()));

		if (toCrop.getY() < 1 || !world.isEmpty(toCrop)) {
			this.b = false;
			return itemstack;
		}

		for (EnumDirection direction : soilDirections) {
			if (!this.isSoil(world.getType(toCrop.shift(direction)))) {
				continue;
			}
			--itemstack.count;
			// Magic value 2: physics update flag - do block update, not comparators
			world.setTypeAndData(toCrop, this.getBlockData(cropBlock, direction), 2);
			this.b = true;
			return itemstack;
		}

		this.b = false;
		return itemstack;
	}

	protected boolean isSoil(IBlockData blockData) {
		return blockData.getBlock() == soilBlock;
	}

	protected IBlockData getBlockData(Block block, EnumDirection soilDirection) {
		return block.getBlockData();
	}

	@Override
	protected void a(ISourceBlock isourceblock) {
		if (this.b) {
			isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
		} else {
			isourceblock.getWorld().triggerEffect(1001, isourceblock.getBlockPosition(), 0);
		}
	}
}
