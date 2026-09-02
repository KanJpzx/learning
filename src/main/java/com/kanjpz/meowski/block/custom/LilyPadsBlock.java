package com.kanjpz.meowski.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LilyPadsBlock extends BushBlock {
    public static final MapCodec<LilyPadsBlock> CODEC =
            simpleCodec(LilyPadsBlock::new);

    protected static final VoxelShape SHAPE =
            Block.box(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);

    @Override
    public MapCodec<LilyPadsBlock> codec() {
        return CODEC;
    }

    public LilyPadsBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        super.entityInside(state, level, pos, entity);

        if (level instanceof ServerLevel && entity instanceof Boat) {
            level.destroyBlock(pos, true, entity);
        }
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        FluidState fluidBelow = level.getFluidState(pos);
        FluidState fluidAbove = level.getFluidState(pos.above());

        return (fluidBelow.getType() == Fluids.WATER
                || state.getBlock() instanceof IceBlock)
                && fluidAbove.getType() == Fluids.EMPTY;
    }
}