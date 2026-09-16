package com.kanjpz.meowski.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class BoulderFeature extends Feature<BoulderConfiguration> {

    public BoulderFeature(Codec<BoulderConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BoulderConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BoulderConfiguration config = context.config();

        BlockPos.MutableBlockPos lumpCenter = context.origin().mutable();

        // settle onto solid ground instead of floating above it
        while (lumpCenter.getY() > level.getMinBuildHeight() + 1 && level.isEmptyBlock(lumpCenter.below())) {
            lumpCenter.move(0, -1, 0);

        }

        int lumps = config.lumps().sample(random);
        boolean placedAny = false;

        for (int i = 0; i < lumps; i++) {
            int r = config.radius().sample(random);
            int rx = r + random.nextInt(2);
            int ry = Math.max(1, r - 1 + random.nextInt(2)); // a bit flatter vertically, more "boulder" than "sphere"
            int rz = r + random.nextInt(2);

            for (BlockPos pos : BlockPos.betweenClosed(
                    lumpCenter.offset(-rx, -ry, -rz),
                    lumpCenter.offset(rx, ry, rz))) {

                double dx = (pos.getX() - lumpCenter.getX()) / (double) rx;
                double dy = (pos.getY() - lumpCenter.getY()) / (double) ry;
                double dz = (pos.getZ() - lumpCenter.getZ()) / (double) rz;
                double dist = dx * dx + dy * dy + dz * dz;

                // jitter the ellipsoid edge so it doesn't look like a perfect blob
                if (dist <= 1.0 + (random.nextDouble() * 0.15 - 0.075)) {
                    BlockState existing = level.getBlockState(pos);
                    if (existing.isAir()
                            || existing.canBeReplaced()
                            || existing.is(BlockTags.DIRT)
                            || existing.is(BlockTags.BASE_STONE_OVERWORLD)
                            || existing.is(Blocks.GRASS_BLOCK)) {
                        level.setBlock(pos, config.stateProvider().getState(random, pos), 3);
                        placedAny = true;
                    }
                }
            }

            // shift the next lump so it overlaps and fuses into one irregular mass
            lumpCenter.move(-1 + random.nextInt(3), -random.nextInt(2), -1 + random.nextInt(3));
        }

        return placedAny;
    }
}