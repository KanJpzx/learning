package com.kanjpz.meowski.worldgen;

import com.kanjpz.meowski.block.ModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Builds a "forest floor" surface rule: a continuous, all-over blend of grass block,
 * coarse dirt, rooted dirt and forest moss - the same kind of mechanism vanilla uses
 * for things like podzol/mycelium transitions, but tuned for a scattered, organic look
 * instead of vanilla's smooth transitions.
 *
 * This does NOT replace your existing biome/placed features wholesale - it only decides
 * what block sits at the very top of the ground. Trees, flowers, etc. still place on
 * top exactly as before.
 */
public class ModSurfaceRules {

    public static SurfaceRules.RuleSource forestFloor() {

        SurfaceRules.RuleSource moss = SurfaceRules.state(
                ModBlocks.FOREST_MOSS.get().defaultBlockState());
        SurfaceRules.RuleSource coarseDirt = SurfaceRules.state(
                Blocks.COARSE_DIRT.defaultBlockState());
        SurfaceRules.RuleSource rootedDirt = SurfaceRules.state(
                Blocks.ROOTED_DIRT.defaultBlockState());

        // Each condition only fires in the "extreme" tail of its own noise field, so the
        // three patch types are rare individually but their overlaps + gaps look chaotic
        // rather than tiled. Order = priority: moss patches win over coarse dirt, which
        // wins over rooted dirt speckle; anything left over falls through to vanilla's
        // own grass rule (we don't emit a default here - see ModNoiseGeneratorSettings).

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(ModNoises.FOREST_MOSS_PATCHES, 0.0, 0.0),
                        moss),
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(ModNoises.COARSE_DIRT_PATCHES, 0.0, 0.0),
                        coarseDirt),
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(ModNoises.ROOTED_DIRT_SPECKLE, 0.0, 0.0),
                        rootedDirt)
        );
    }
}
