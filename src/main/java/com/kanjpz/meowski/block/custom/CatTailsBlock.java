package com.kanjpz.meowski.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class CatTailsBlock extends TallFlowerBlock
        implements SimpleWaterloggedBlock {

    public static final MapCodec<TallFlowerBlock> CODEC =
            simpleCodec(CatTailsBlock::new);

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;

    public CatTailsBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.getStateDefinition()
                        .any()
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(WATERLOGGED, false));
    }

    @Override
    public MapCodec<TallFlowerBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(HALF, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        BlockPos pos = context.getClickedPos();

        // We need one free block above because this is a 2-block plant.
        if (pos.getY() >= context.getLevel().getMaxBuildHeight() - 1) {
            return null;
        }

        // Don't place if the upper position is occupied.
        if (!context.getLevel()
                .getBlockState(pos.above())
                .canBeReplaced(context)) {

            return null;
        }

        BlockState state =
                super.getStateForPlacement(context);

        if (state == null) {
            return null;
        }

        boolean waterHere =
                context.getLevel()
                        .getFluidState(pos)
                        .getType() == Fluids.WATER;

        return state.setValue(WATERLOGGED, waterHere);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighbour,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighbourPos) {

        BlockState result =
                super.updateShape(
                        state,
                        direction,
                        neighbour,
                        level,
                        pos,
                        neighbourPos);

        // The vanilla double-plant code may decide this half is invalid.
        if (!result.is(this)) {
            return result;
        }

        /*
         * Keep the upper logical block synchronized
         * with the lower cattail.
         */
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER
                && direction == Direction.DOWN
                && neighbour.is(this)) {

            return result.setValue(
                    WATERLOGGED,
                    neighbour.getValue(WATERLOGGED)
            );
        }

        return result;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState floor,
            BlockGetter level,
            BlockPos pos) {

        return super.mayPlaceOn(floor, level, pos)
                || floor.is(BlockTags.SAND)
                || floor.is(Blocks.CLAY)
                || floor.is(Blocks.GRASS_BLOCK)
                || floor.is(Blocks.COARSE_DIRT)
                || floor.is(Blocks.DIRT)
                || floor.is(Blocks.MUD);
    }

    @Override
    public FluidState getFluidState(BlockState state) {

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER
                && state.getValue(WATERLOGGED)) {

            return Fluids.WATER.getSource(false);
        }

        return super.getFluidState(state);
    }
}