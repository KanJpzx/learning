package com.kanjpz.meowski.worldgen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.material.Fluids;

import java.util.List;



public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> WILLOW_KEY = registerKey("willow");
    public static final ResourceKey<PlacedFeature> OAK_KEY = registerKey("oak");
    public static final ResourceKey<PlacedFeature> BIRCH_KEY = registerKey("birch");

    public static final ResourceKey<PlacedFeature> BUSH_KEY = registerKey("bush");
    public static final ResourceKey<PlacedFeature> CATTAILS_KEY = registerKey("cattails");

    public static final ResourceKey<PlacedFeature> FOREST_FLOOR_MIX_KEY = registerKey("forest_floor_mix");
    public static final ResourceKey<PlacedFeature> FOREST_MOSS_PATCH_KEY = registerKey("forest_moss_patch");
    public static final ResourceKey<PlacedFeature> PODZOL_PATCH_KEY = registerKey("podzol_patch");
    public static final ResourceKey<PlacedFeature> FOREST_MOSS_CARPET_KEY = registerKey("forest_moss_carpet_patch");

    public static final ResourceKey<PlacedFeature> BLUE_BERRY_BUSH_KEY = registerKey("blue_berry_bush_patch");

    public static final ResourceKey<PlacedFeature> BOULDER_KEY = registerKey("boulder");
    public static final ResourceKey<PlacedFeature> LARGE_BOULDER_KEY = registerKey("large_boulder");

    public static final ResourceKey<PlacedFeature> DENSE_FOREST_GRASS_KEY = registerKey("dense_forest_grass");
    public static final ResourceKey<PlacedFeature> DENSE_TALL_GRASS_KEY = registerKey("dense_tall_grass");
    public static final ResourceKey<PlacedFeature> DENSE_LARGE_FERN_KEY = registerKey("dense_large_fern");


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        var vanillaConfiguredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, WILLOW_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILLOW_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2,0.2f, 1),
                        ModBlocks.WILLOW_SAPLING.get()));

        register(context, OAK_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(4, 0.2f, 3),
                        Blocks.OAK_SAPLING));

        register(context, BIRCH_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BIRCH_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 1),
                        Blocks.BIRCH_SAPLING));

        register(context, BUSH_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BUSH_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2),
                        Blocks.OAK_SAPLING));

        register(context, CATTAILS_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CATTAILS_KEY),
                List.of(CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(62),
                                VerticalAnchor.absolute(62)),
                        BiomeFilter.biome()));

        register(context, FOREST_FLOOR_MIX_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.FOREST_FLOOR_MIX_KEY),
                List.of(CountPlacement.of(3), // was 1 — much more common now
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, FOREST_MOSS_PATCH_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.FOREST_MOSS_PATCH_KEY),
                List.of(CountPlacement.of(2), // occasional distinct moss clumps
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        register(context, PODZOL_PATCH_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PODZOL_PATCH_KEY),
                List.of(CountPlacement.of(2), // occasional distinct moss clumps
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, FOREST_MOSS_CARPET_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.FOREST_MOSS_CARPET_KEY),
                List.of(CountPlacement.of(3),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));
        register(context, BLUE_BERRY_BUSH_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BLUE_BERRY_BUSH_KEY),
                List.of(CountPlacement.of(1), // one small patch per chunk — rare, like a special find
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, BOULDER_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BOULDER_KEY),
                List.of(RarityFilter.onAverageOnceEvery(40),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(clearOfWaterPredicate()),
                        BiomeFilter.biome()));

        register(context, LARGE_BOULDER_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.LARGE_BOULDER_KEY),
                List.of(RarityFilter.onAverageOnceEvery(150),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(clearOfWaterPredicate()),
                        BiomeFilter.biome()));

        register(context, DENSE_FOREST_GRASS_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.DENSE_FOREST_GRASS_KEY),
                List.of(CountPlacement.of(1), // only need one call — 96 tries already covers the whole chunk
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        register(context, DENSE_TALL_GRASS_KEY, vanillaConfiguredFeatures.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS),
                List.of(CountPlacement.of(UniformInt.of(1, 1)), // 6-12 patches per chunk, randomized
                        InSquarePlacement.spread(),               // random XZ within the chunk
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        register(context, DENSE_LARGE_FERN_KEY, vanillaConfiguredFeatures.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN),
                List.of(CountPlacement.of(UniformInt.of(1, 1)),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));
    }
    private static BlockPredicate clearOfWaterPredicate() {
        return BlockPredicate.allOf(
                BlockPredicate.not(BlockPredicate.matchesFluids(Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(BlockPos.ZERO.below(), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(5, 0, 0), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(-5, 0, 0), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(0, 0, 5), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(0, 0, -5), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(3, 0, 3), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(-3, 0, -3), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(3, 0, -3), Fluids.WATER)),
                BlockPredicate.not(BlockPredicate.matchesFluids(new BlockPos(-3, 0, 3), Fluids.WATER)));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));
    }


    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}