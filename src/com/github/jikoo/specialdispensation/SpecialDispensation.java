package com.github.jikoo.specialdispensation;

import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_9_R1.BlockDispenser;
import net.minecraft.server.v1_9_R1.Blocks;
import net.minecraft.server.v1_9_R1.Items;

/**
 * Manual planting is for chumps.
 * 
 * @author Jikoo
 */
public class SpecialDispensation extends JavaPlugin {

	@Override
	public void onEnable() {
		// See DispenserRegistry
		BlockDispenser.REGISTRY.a(Items.WHEAT_SEEDS, new CropDispenseBehavior(Blocks.WHEAT, Blocks.FARMLAND));
		BlockDispenser.REGISTRY.a(Items.POTATO, new CropDispenseBehavior(Blocks.POTATOES, Blocks.FARMLAND));
		BlockDispenser.REGISTRY.a(Items.CARROT, new CropDispenseBehavior(Blocks.CARROTS, Blocks.FARMLAND));
		BlockDispenser.REGISTRY.a(Items.BEETROOT_SEEDS, new CropDispenseBehavior(Blocks.BEETROOT, Blocks.FARMLAND));
		BlockDispenser.REGISTRY.a(Items.NETHER_WART, new CropDispenseBehavior(Blocks.NETHER_WART, Blocks.SOUL_SAND));
		// This will overwrite the default for dye, which will trigger debug logging. It's unimportant.
		BlockDispenser.REGISTRY.a(Items.DYE, new DyeDispenseBehavior());
	}

}
