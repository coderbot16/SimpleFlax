package simpleflax.village;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import simpleflax.Config;

import java.util.List;
import java.util.Random;

public class FlaxVillageGen implements VillagerRegistry.IVillageCreationHandler {
	@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
		return new StructureVillagePieces.PieceWeight(ComponentFlaxField.class, Config.villageGenerationWeight, MathHelper.getInt(random, 2 + i, 4 + i * 2));
	}

	@Override
	public Class<?> getComponentClass() {
		return ComponentFlaxField.class;
	}

	@Override
	public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
		return ComponentFlaxField.createPiece(startPiece, pieces, random, x, y, z, facing, type);
	}
}
