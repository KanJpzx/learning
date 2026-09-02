package com.kanjpz.meowski.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ForestMossBlock extends Block {
    public static final MapCodec<ForestMossBlock> CODEC =
            simpleCodec(ForestMossBlock::new);

    /*
     * A normal block is 16 pixels tall.
     * This collision is only 14 pixels tall, so the player's feet
     * appear to sink two pixels into the moss.
     */
    private static final VoxelShape COLLISION_SHAPE =
            Block.box(0, 0, 0, 16, 14, 16);

    public ForestMossBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<ForestMossBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return COLLISION_SHAPE;
    }
}