package simpleflax.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import simpleflax.init.FlaxObjects;

import java.util.Random;

public class BlockFlaxWild extends BlockBush {
	public static final PropertyEnum<BlockFlax.Half> HALF = PropertyEnum.create("half", BlockFlax.Half.class);
	private static final AxisAlignedBB FLAX_WILD_AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	public BlockFlaxWild() {
		super();

		setCreativeTab(null);
		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		disableStats();

		setDefaultState(this.getDefaultState().withProperty(HALF, BlockFlax.Half.LOWER));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FLAX_WILD_AABB;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || (state.getBlock() instanceof BlockFlaxWild && state.getValue(HALF) == BlockFlax.Half.LOWER);
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		if(!(world.canSeeSky(pos) || world.getLight(pos) >= 8)) return false;

		IBlockState below = world.getBlockState(pos.down());

		if(state.getValue(HALF) == BlockFlax.Half.LOWER) {
			return below.getBlock().canSustainPlant(below, world, pos.down(), EnumFacing.UP, this);
		} else {
			return below.getBlock() == this && below.getValue(HALF) == BlockFlax.Half.LOWER;
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if(state.getValue(HALF) == BlockFlax.Half.UPPER) {
			return;
		}

		Random random = world instanceof World ? ((World)world).rand : new Random();

		int string = 1 + random.nextInt(3) + random.nextInt(1 + fortune);

		for(int i = 0; i < string; i++) {
			drops.add(new ItemStack(Items.STRING));
		}

		for(int attempt = 0; attempt < fortune + 3; attempt++) {
			if(random.nextBoolean()) {
				drops.add(new ItemStack(FlaxObjects.FLAX_SEEDS));
			}
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(state.getValue(HALF) == BlockFlax.Half.UPPER && world.getBlockState(pos.down()).getBlock() == this) {
			if(player.capabilities.isCreativeMode) {
				world.setBlockToAir(pos.down());
			} else {
				world.destroyBlock(pos.down(), true);
			}
		}
	}

	public IBlockState getStateFromMeta(int meta) {
		boolean half = meta >= 8;

		return this.getDefaultState().withProperty(HALF, half ? BlockFlax.Half.UPPER : BlockFlax.Half.LOWER);
	}

	public int getMetaFromState(IBlockState state) {
		boolean half = state.getValue(HALF) == BlockFlax.Half.UPPER;

		return half ? 8 : 0;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Plains;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HALF);
	}
}
