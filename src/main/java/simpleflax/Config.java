package simpleflax;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	public static int seedWeight = 7;
	public static int villageGenerationWeight = 60;
	public static boolean simpleHarvestCompat = true;
	public static int flaxPerChunk = 0;

	public static void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		config.load();

		seedWeight = config.getInt("seedWeight", Configuration.CATEGORY_GENERAL, 7, 0, 100, "Weight of Flax Seeds in the tall grass drop list (Wheat Seeds = 10, 0 to disable)");
		villageGenerationWeight = config.getInt("villageGenerationWeight", Configuration.CATEGORY_GENERAL, 60, 0, 100, "Weight of Flax Fields generating in villages. The small houses are 3, Blacksmiths are 15. higher is lower (0 to disable).");
		simpleHarvestCompat = config.getBoolean("simpleHarvestCompat", Configuration.CATEGORY_GENERAL, true, "Whether Flax will be registered with SimpleHarvest");
		flaxPerChunk = config.getInt("flaxPerChunk", Configuration.CATEGORY_GENERAL, 0, 0, 100, "How much wild flax will be generated per chunk. If you want to enable this, a starting value of 8 is recommended.");

		config.save();
	}
}
