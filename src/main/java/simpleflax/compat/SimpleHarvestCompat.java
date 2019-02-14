package simpleflax.compat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simpleflax.blocks.BlockFlax;
import simpleflax.init.FlaxObjects;
import tehnut.harvest.*;

import javax.annotation.Nullable;
import java.util.Random;

public class SimpleHarvestCompat {
	public static void init() {
		HarvestConfig config = Harvest.config;

		// Register our flax crops with SimpleHarvest.
		Crop top = new Crop(
				new BlockStack(FlaxObjects.FLAX_BLOCK, 4 | 8),
				new BlockStack(FlaxObjects.FLAX_BLOCK)
		);

		config.getCrops().add(top);
		config.getCropMap().put(top.getInitialBlock(), top);

		Crop bottom = new Crop(
				new BlockStack(FlaxObjects.FLAX_BLOCK, 5),
				new BlockStack(FlaxObjects.FLAX_BLOCK)
		);

		config.getCrops().add(bottom);
		config.getCropMap().put(bottom.getInitialBlock(), bottom);

		// Then, register our replant handler.

		Harvest.CUSTOM_HANDLERS.put(FlaxObjects.FLAX_BLOCK, new FlaxReplantHandler());
	}

	public static class FlaxReplantHandler implements IReplantHandler {
		public void handlePlant(World world, BlockPos pos, IBlockState state, EntityPlayer player, @Nullable TileEntity tileEntity) {
			if(world.isRemote) {
				return;
			}

			int age = state.getValue(BlockFlax.AGE);

			if(state.getValue(BlockFlax.HALF) == BlockFlax.Half.UPPER) {
				// Move to the lower block.
				pos = pos.down();
				state = world.getBlockState(pos);

				// Sanity check for the plant.
				if(state.getBlock() != FlaxObjects.FLAX_BLOCK || state.getValue(BlockFlax.HALF) == BlockFlax.Half.UPPER) {
					// Malformed plant. Delete the bad half and bail out.
					world.setBlockToAir(pos.up());

					return;
				}

				// Otherwise, it works!
				age = state.getValue(BlockFlax.AGE);
			}

			Random random = world.rand;

			if(age >= BlockFlax.MAX_AGE) {
				int string = 1 + random.nextInt(3);

				for(int i = 0; i < string; i++) {
					Block.spawnAsEntity(world, pos, new ItemStack(Items.STRING));
				}
			}

			boolean firstSeed = true;
			for(int attempt = 0; attempt < 3; attempt++) {
				if(random.nextInt(8) <= Math.min(age, 4)) {
					if(firstSeed) {
						firstSeed = false;
						continue;
					}

					Block.spawnAsEntity(world, pos, new ItemStack(FlaxObjects.FLAX_SEEDS));
				}
			}

			world.setBlockToAir(pos.up());
			world.setBlockState(pos, FlaxObjects.FLAX_BLOCK.withAge(0).withProperty(BlockFlax.HALF, BlockFlax.Half.LOWER));
		}
	}
}
