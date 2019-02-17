package simpleflax.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import simpleflax.blocks.BlockFlax;
import simpleflax.blocks.BlockFlaxWild;
import simpleflax.init.FlaxObjects;

import java.util.Random;

public class WildFlaxGenerator implements IWorldGenerator {
	private WorldGenDoublePlant wildFlax = new WorldGenDoublePlant(
			false,
			FlaxObjects.FLAX_BLOCK_WILD.getDefaultState(),
			FlaxObjects.FLAX_BLOCK_WILD.getDefaultState().withProperty(BlockFlaxWild.HALF, BlockFlax.Half.UPPER)
	);

	private int flaxPerChunk;

	public WildFlaxGenerator(int flaxPerChunk) {
		this.flaxPerChunk = flaxPerChunk;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
		if(world.provider.getDimension() != 0) {
			return;
		}

		BlockPos chunkPos = new BlockPos(chunkX * 16, 0, chunkZ * 16);

		for(int i = 0; i < flaxPerChunk; i++) {
			BlockPos at = chunkPos.add(
					8 + random.nextInt(16),
					0,
					8 + random.nextInt(16)
			);

			int maxY = world.getHeight(at).getY() + 32;


			if(maxY > 0) {
				at = at.add(0, random.nextInt(maxY), 0);

				wildFlax.generate(world, random, at);
			}
		}
	}
}
