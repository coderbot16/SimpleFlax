package simpleflax.compat;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import simpleflax.blocks.BlockFlax;
import simpleflax.init.FlaxObjects;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class TheOneProbeCompat {
	public static void init() {
		TheOneProbe.theOneProbeImp.registerProvider(new FlaxDisplayProvider());

	}

	public static class FlaxDisplayProvider implements IProbeInfoProvider {
		@Override
		public String getID() {
			return "simpleflax:flax";
		}

		@Override
		public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
			if(state.getBlock() instanceof BlockFlax) {
				int age;

				if(state.getValue(BlockFlax.HALF) == BlockFlax.Half.UPPER) {
					age = state.getValue(BlockFlax.AGE) + 5;
				} else {
					age = Math.min(state.getValue(BlockFlax.AGE), 4);

					IBlockState above = world.getBlockState(data.getPos().up());
					if(above.getBlock() instanceof BlockFlax) {
						age += above.getValue(BlockFlax.AGE) + 1;
					}
				}

				int maxAge = FlaxObjects.FLAX_BLOCK.getMaxAge() * 2 + 1;

				if (age >= maxAge) {
					probeInfo.text(OK + "Fully grown");
				} else {
					probeInfo.text(LABEL + "Growth: " + WARNING + (age * 100) / maxAge + "%");
				}
			}
		}
	}
}
