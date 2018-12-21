package simpleflax.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import simpleflax.init.FlaxObjects;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerItemModels() {
		ModelLoader.setCustomModelResourceLocation(FlaxObjects.FLAX_SEEDS, 0, new ModelResourceLocation(FlaxObjects.FLAX_SEEDS.getRegistryName(), "inventory"));
	}
}
