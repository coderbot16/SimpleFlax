package simpleflax.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import simpleflax.init.FlaxObjects;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerItemModels() {
		// TODO: For some reason, ModelLoader does not work here.
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(FlaxObjects.FLAX_SEEDS, 0, new ModelResourceLocation(FlaxObjects.FLAX_SEEDS.getRegistryName(), "inventory"));
	}
}
