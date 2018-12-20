package simpleflax;

import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Logger;
import simpleflax.init.FlaxObjects;
import simpleflax.proxy.CommonProxy;
import simpleflax.village.ComponentFlaxField;
import simpleflax.village.FlaxVillageGen;

@Mod(modid = SimpleFlax.MODID, name = SimpleFlax.NAME, version = SimpleFlax.VERSION)
public class SimpleFlax
{
	public static final String MODID = "simpleflax";
	public static final String NAME = "Simple Flax";
	public static final String VERSION = "0.1.0";

	private static Logger logger;

	@SidedProxy(clientSide = "simpleflax.proxy.ClientProxy", serverSide = "simpleflax.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		Config.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerItemModels();

		MinecraftForge.addGrassSeed(new ItemStack(FlaxObjects.FLAX_SEEDS), 7);

		MapGenStructureIO.registerStructureComponent(ComponentFlaxField.class, "ViFF");

		if(Config.villageGenerationWeight != 0) {
			VillagerRegistry.instance().registerVillageCreationHandler(new FlaxVillageGen());
		}
	}
}
