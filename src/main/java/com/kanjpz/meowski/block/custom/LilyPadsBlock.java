package com.kanjpz.meowski.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LilyPadsBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<net.minecraft.world.level.block.WaterlilyBlock> CODEC = simpleCodec(net.minecraft.world.level.block.WaterlilyBlock::new);
    protected static final VoxelShape AABB = Block.box((double)1.0F, (double)0.0F, (double)1.0F, (double)15.0F, (double)1.5F, (double)15.0F);

    public MapCodec<net.minecraft.world.level.block.WaterlilyBlock> codec() {
        return CODEC;
    }

    public LilyPadsBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level instanceof ServerLevel && entity instanceof Boat) {
            level.destroyBlock(new BlockPos(pos), true, entity);
        }

    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        FluidState fluidstate = level.getFluidState(pos);
        FluidState fluidstate1 = level.getFluidState(pos.above());
        return (fluidstate.getType() == Fluids.WATER || state.getBlock() instanceof IceBlock) && fluidstate1.getType() == Fluids.EMPTY;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {

    }
}
