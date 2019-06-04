package simpleflax;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.oredict.OreDictionary;
import simpleflax.compat.SimpleHarvestCompat;
import simpleflax.compat.TheOneProbeCompat;
import simpleflax.init.FlaxObjects;
import simpleflax.proxy.CommonProxy;
import simpleflax.village.ComponentFlaxField;
import simpleflax.village.FlaxVillageGen;
import simpleflax.world.WildFlaxGenerator;

@Mod(modid = SimpleFlax.MODID, name = SimpleFlax.NAME, version = SimpleFlax.VERSION)
public class SimpleFlax
{
	public static final String MODID = "simpleflax";
	public static final String NAME = "Simple Flax";
	public static final String VERSION = "0.2.0";

	@SidedProxy(clientSide = "simpleflax.proxy.ClientProxy", serverSide = "simpleflax.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		OreDictionary.registerOre("seedFlax", FlaxObjects.FLAX_SEEDS);
		OreDictionary.registerOre("cropFlax", Items.STRING);

		if(Config.seedWeight != 0) {
			MinecraftForge.addGrassSeed(new ItemStack(FlaxObjects.FLAX_SEEDS), Config.seedWeight);
		}

		MapGenStructureIO.registerStructureComponent(ComponentFlaxField.class, "ViFF");

		if(Config.villageGenerationWeight != 0) {
			VillagerRegistry.instance().registerVillageCreationHandler(new FlaxVillageGen());
		}

		GameRegistry.registerWorldGenerator(new WildFlaxGenerator(Config.flaxPerChunk), 2);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if(Config.simpleHarvestCompat && Loader.isModLoaded("harvest")) {
			SimpleHarvestCompat.init();
		}

		if(Loader.isModLoaded("theoneprobe")) {
			TheOneProbeCompat.init();
		}
	}
}
