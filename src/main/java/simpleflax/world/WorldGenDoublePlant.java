package simpleflax.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenDoublePlant extends WorldGenerator {
	private IBlockState lower;
	private IBlockState upper;

	public WorldGenDoublePlant(boolean notify, IBlockState lower, IBlockState upper) {
		super(notify);

		this.lower = lower;
		this.upper = upper;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		for (int i = 0; i < 2; ++i) {
			BlockPos base = pos.add(
					rand.nextInt(8) - rand.nextInt(8),
					rand.nextInt(4) - rand.nextInt(4),
					rand.nextInt(8) - rand.nextInt(8)
			);

			if(base.getY() < 0 || base.getY() > 254) {
				continue;
			}

			BlockPos top = base.up();

			if(!(world.isAirBlock(base) && world.isAirBlock(top))) {
				continue;
			}

			if(!Blocks.DOUBLE_PLANT.canPlaceBlockAt(world, base)) {
				continue;
			}

			this.setBlockAndNotifyAdequately(world, base, lower);
			this.setBlockAndNotifyAdequately(world, top, upper);
		}

		return true;
	}
}
