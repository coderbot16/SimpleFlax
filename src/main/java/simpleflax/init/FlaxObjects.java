package simpleflax.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import simpleflax.SimpleFlax;
import simpleflax.blocks.BlockFlax;

@Mod.EventBusSubscriber
public class FlaxObjects {
	public static Item FLAX_SEEDS = null;
	public static Block FLAX_BLOCK = null;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		FLAX_BLOCK = new BlockFlax();
		FLAX_BLOCK.setRegistryName(SimpleFlax.MODID, "flax");
		FLAX_BLOCK.setUnlocalizedName(SimpleFlax.MODID + ".flax");

		event.getRegistry().register(FLAX_BLOCK);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		FLAX_SEEDS = new ItemSeeds(FLAX_BLOCK, Blocks.FARMLAND);
		FLAX_SEEDS.setRegistryName(SimpleFlax.MODID, "seeds");
		FLAX_SEEDS.setUnlocalizedName(SimpleFlax.MODID + ".seeds");

		event.getRegistry().register(FLAX_SEEDS);
	}
}