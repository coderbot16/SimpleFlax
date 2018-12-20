package simpleflax;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	public static int seedWeight = 7;

	public static void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		config.load();

		seedWeight = config.getInt("seedWeight", Configuration.CATEGORY_GENERAL, 7, 1, 100, "Weight of Flax Seeds in the tall grass drop list (Wheat Seeds = 10)");

		config.save();
	}
}
