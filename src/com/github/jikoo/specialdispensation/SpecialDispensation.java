package com.github.jikoo.specialdispensation;

import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockDispenser;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.DispenseBehaviorItem;
import net.minecraft.server.v1_8_R3.IDispenseBehavior;
import net.minecraft.server.v1_8_R3.ISourceBlock;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.World;

/**
 * Manual planting is for chumps.
 * 
 * @author Jikoo
 */
public class SpecialDispensation extends JavaPlugin {

	@Override
	public void onEnable() {
		BlockDispenser.N.a(Items.WHEAT_SEEDS, new CropDispenseBehavior(Blocks.WHEAT, Blocks.FARMLAND));
		BlockDispenser.N.a(Items.POTATO, new CropDispenseBehavior(Blocks.POTATOES, Blocks.FARMLAND));
		BlockDispenser.N.a(Items.CARROT, new CropDispenseBehavior(Blocks.CARROTS, Blocks.FARMLAND));
		BlockDispenser.N.a(Items.NETHER_WART, new CropDispenseBehavior(Blocks.NETHER_WART, Blocks.SOUL_SAND));
	}

	private class CropDispenseBehavior extends DispenseBehaviorItem {

		private final Block cropBlock;
		private final Block soilBlock;
		public CropDispenseBehavior(Block cropBlock, Block soilBlock) {
			this.cropBlock = cropBlock;
			this.soilBlock = soilBlock;
		}

		private boolean b = true;

		@Override
		protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
			World world = isourceblock.i();
			BlockPosition toCrop = isourceblock.getBlockPosition().shift(BlockDispenser.b(isourceblock.f()));

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
				return itemstack;
			}

			if (!event.getItem().equals(craftItem)) {
				ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
				IDispenseBehavior idispensebehavior = BlockDispenser.N.get(eventStack.getItem());
				if (idispensebehavior != IDispenseBehavior.a && idispensebehavior != this) {
					idispensebehavior.a(isourceblock, eventStack);
					return itemstack;
				}
			}

			--itemstack.count;

			if (toCrop.getY() > 0 && world.isEmpty(toCrop) && world.getType(toCrop.down()).getBlock() == soilBlock) {
				world.setTypeUpdate(toCrop, cropBlock.getBlockData());
				this.b = true;
			} else {
				this.b = false;
			}

			return itemstack;
		}

		@Override
		protected void a(ISourceBlock isourceblock) {
			if (this.b) {
				isourceblock.i().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
			} else {
				isourceblock.i().triggerEffect(1001, isourceblock.getBlockPosition(), 0);
			}
		}
	};
}
