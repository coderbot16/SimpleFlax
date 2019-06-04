package simpleflax.blocks;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import simpleflax.init.FlaxObjects;

import java.util.Random;

public class BlockFlax extends BlockCrops implements IGrowable {
	// Prevent TOP from detecting the age property
	public static final PropertyInteger AGE = PropertyInteger.create("ayyge", 0, 5);
	public static final PropertyEnum<Half> HALF = PropertyEnum.create("half", Half.class);
	public static final int MAX_AGE = 5;

	// Growth pattern:
	// +1, +2, +3, +4 (0.8 -> 1.0 is 0.2, plus 0.2 of upper half)
	private static final double[] HEIGHTS = new double[] {
		0.2,
		0.3,
		0.5,
		0.8,
		1.0
	};

	private static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[] {
		new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, HEIGHTS[0], 1.0)	,
		new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, HEIGHTS[1], 1.0)	,
		new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, HEIGHTS[2], 1.0)	,
		new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, HEIGHTS[3], 1.0)	,
		new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, HEIGHTS[4], 1.0)
	};

	public BlockFlax() {
		super();

		setDefaultState(this.withAge(0).withProperty(HALF, Half.LOWER));
	}

	@Override
	public boolean isMaxAge(IBlockState state) {
		// The only thing that uses this is villagers when they want to grief our plants.
		// So, we block that.
		return false;
	}

	public boolean isMaxAgeForReal(IBlockState state)
	{
		return state.getValue(this.getAgeProperty()) >= this.getMaxAge();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOXES[Math.min(state.getValue(AGE), 4)];
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || (state.getBlock() instanceof BlockFlax && state.getValue(HALF) == Half.LOWER && state.getValue(AGE) == getMaxAge());
	}

	@Override
	protected PropertyInteger getAgeProperty() {
		return AGE;
	}

	@Override
	public int getMaxAge() {
		return 4;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		this.checkAndDropBlock(world, pos, state);

		if (!world.isAreaLoaded(pos, 1)) return; // prevent loading unloaded chunks from lighting checks
		if (world.getLightFromNeighbors(pos.up()) < 9) return;

		int age = state.getValue(AGE);
		Half half = state.getValue(HALF);

		if(age >= getMaxAge()) {
			// Can't grow any more.
			return;
		}

		float growthChance;
		if(half == Half.UPPER) {
			growthChance = getGrowthChance(this, world, pos.down());
		} else {
			growthChance = getGrowthChance(this, world, pos);
		}

		if(!ForgeHooks.onCropsGrowPre(world, pos, state, random.nextInt((int)(25.0F / growthChance) + 1) == 0)) return;

		world.setBlockState(pos, this.withAge(age + 1).withProperty(HALF, half));
		if(half == Half.LOWER && age + 1 == getMaxAge()) {
			world.setBlockState(pos.up(), this.withAge(0).withProperty(HALF, Half.UPPER));
		} else if(half == Half.UPPER && age + 1 >= getMaxAge() && world.getBlockState(pos.down()).getBlock() == this) {
			world.setBlockState(pos.down(), this.withAge(MAX_AGE).withProperty(HALF, Half.LOWER));
		}

		ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		if(state.getValue(HALF) == Half.UPPER) {
			return !this.isMaxAgeForReal(state);
		} else if(!this.isMaxAgeForReal(state)) {
			return true;
		} else {
			IBlockState upper = world.getBlockState(pos.up());

			return !this.isMaxAgeForReal(upper) && upper.getBlock() == this;
		}
	}

	@Override
	public void grow(World world, BlockPos pos, IBlockState state) {
		int age = state.getValue(AGE);
		Half half = state.getValue(HALF);

		if(half == Half.LOWER) {
			if(age < getMaxAge()) {
				int potentialAge = age + this.getBonemealAgeIncrease(world);
				int cappedAge = Math.min(potentialAge, getMaxAge());

				world.setBlockState(pos, this.withAge(cappedAge).withProperty(HALF, Half.LOWER));

				if(potentialAge >= getMaxAge()) {
					int additionalAge = Math.min(potentialAge - getMaxAge(), getMaxAge());

					if(world.isAirBlock(pos.up())) {
						world.setBlockState(pos.up(), this.withAge(additionalAge).withProperty(HALF, Half.UPPER));
					}

					if(additionalAge >= this.getMaxAge()) {
						world.setBlockState(pos, this.withAge(5).withProperty(HALF, Half.LOWER));
					}
				}

				// Success
				return;
			} else {
				pos = pos.up();
				state = world.getBlockState(pos);
				if(state.getBlock() != this) {
					return;
				}

				age = state.getValue(AGE);
				half = Half.UPPER;

				// Fall through, pass it on to the upper block handler instead.
				// This block is already grown.
			}
		}

		if(half == Half.UPPER) {
			age = Math.min(age + this.getBonemealAgeIncrease(world), getMaxAge());

			world.setBlockState(pos, this.withAge(age).withProperty(HALF, Half.UPPER));

			if(age >= getMaxAge() && world.getBlockState(pos.down()).getBlock() == this) {
				world.setBlockState(pos.down(), this.withAge(MAX_AGE).withProperty(HALF, Half.LOWER));
			}
		}
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		if(!(world.canSeeSky(pos) || world.getLight(pos) >= 8)) return false;

		IBlockState below = world.getBlockState(pos.down());

		if(state.getValue(HALF) == Half.LOWER) {
			return below.getBlock().canSustainPlant(below, world, pos.down(), EnumFacing.UP, this);
		} else {
			return below.getBlock() == this && below.getValue(HALF) == Half.LOWER && below.getValue(AGE) >= getMaxAge();
		}
	}

	@Override
	protected Item getSeed() {
		return FlaxObjects.FLAX_SEEDS;
	}

	@Override
	protected Item getCrop() {
		return Items.STRING;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if(state.getValue(HALF) == Half.UPPER) {
			return;
		}

		int age = state.getValue(AGE);
		Random random = world instanceof World ? ((World)world).rand : new Random();

		if(age >= MAX_AGE) {
			int string = 1 + random.nextInt(3) + random.nextInt(1 + fortune);

			for(int i = 0; i < string; i++) {
				drops.add(new ItemStack(Items.STRING));
			}
		}

		for(int attempt = 0; attempt < fortune + 3; attempt++) {
			if(random.nextInt(8) <= Math.min(age, 4)) {
				drops.add(new ItemStack(FlaxObjects.FLAX_SEEDS));
			}
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(state.getValue(HALF) == Half.UPPER && world.getBlockState(pos.down()).getBlock() == this) {
			if(player.capabilities.isCreativeMode) {
				world.setBlockToAir(pos.down());
			} else {
				world.destroyBlock(pos.down(), true);
			}
		}
	}

	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		// Note: broken!
		return this.isMaxAgeForReal(state) ? this.getCrop() : this.getSeed();
	}

	public IBlockState getStateFromMeta(int meta) {
		// format: [HAAA] (H = half, A = age)

		boolean half = meta >= 8;
		int age = Math.min(meta & 7, MAX_AGE);

		return this.withAge(age).withProperty(HALF, half ? Half.UPPER : Half.LOWER);
	}

	public int getMetaFromState(IBlockState state) {
		boolean half = state.getValue(HALF) == Half.UPPER;
		int age = state.getValue(AGE);

		return (half ? 8 : 0) | (age & 7);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AGE, HALF);
	}

	public enum Half implements IStringSerializable
	{
		LOWER,
		UPPER;

		public String toString()
		{
			return this.getName();
		}

		public String getName()
		{
			return this == UPPER ? "upper" : "lower";
		}
	}
}
