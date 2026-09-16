package com.kanjpz.meowski.worldgen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY = registerKey("willow");

    public static final ResourceKey<ConfiguredFeature<?, ?>> OAK_KEY = registerKey("oak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_KEY = registerKey("birch");

    public static final ResourceKey<ConfiguredFeature<?, ?>> CATTAILS_KEY = registerKey("cattails");

    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_FLOOR_MIX_KEY = registerKey("forest_floor_mix");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_MOSS_PATCH_KEY = registerKey("forest_moss_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PODZOL_PATCH_KEY = registerKey("podzol_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_MOSS_CARPET_KEY = registerKey("forest_moss_carpet_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_BERRY_BUSH_KEY = registerKey("blue_berry_bush_patch");


    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_OAK_OVERRIDE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.withDefaultNamespace("oak"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        var blockGetter = context.lookup(Registries.BLOCK);

        register(context, WILLOW_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                        new FancyTrunkPlacer(
                                9,
                                5,
                                2),
                        BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
                        new CherryFoliagePlacer(
                                ConstantInt.of(4),
                                ConstantInt.of(1),
                                ConstantInt.of(4),
                                0.75F,
                                0.65F,
                                0.3F,
                                0.6F),
                        new TwoLayersFeatureSize(0, 0, 0))
                        .ignoreVines()
                        .build());

        register(context, OAK_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG.defaultBlockState()),
                        new FancyTrunkPlacer(
                                12,
                                5,
                                3),
                        BlockStateProvider.simple(Blocks.OAK_LEAVES.defaultBlockState()),
                        new CherryFoliagePlacer(
                                ConstantInt.of(4),
                                ConstantInt.of(2),
                                ConstantInt.of(4),
                                0.45F,
                                0.65F,
                                0.3F,
                                0.45F),
                        new TwoLayersFeatureSize(0, 0, 0))
                        .ignoreVines()
                        .build());

        register(context, BIRCH_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.BIRCH_LOG.defaultBlockState()),
                        new FancyTrunkPlacer(
                                11,
                                3,
                                1),
                        BlockStateProvider.simple(Blocks.BIRCH_LEAVES.defaultBlockState()),
                        new AcaciaFoliagePlacer(
                                ConstantInt.of(2),
                                ConstantInt.of(2)),
                        new TwoLayersFeatureSize(0, 0, 0))
                        .ignoreVines()
                        .build());

        register(context, CATTAILS_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        20, 6, 0,
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(ModBlocks.CATTAILS.get())),
                                BlockPredicateFilter.forPredicate(cattailsPlacementPredicate()))));

// Main floor blend — grass/coarse dirt/rooted dirt, moss removed from here
        register(context, FOREST_FLOOR_MIX_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        100, 4, 2, // more tries + bigger spread = more frequent coverage
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(forestFloorWeightedProvider()),
                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK)))));

// Moss — its own feature, tight radius so tries overlap into an actual clump
        register(context, FOREST_MOSS_PATCH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        60, // lots of tries...
                        2,  // ...concentrated in a small area = solid-looking patch, not scatter
                        2,
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(ModBlocks.FOREST_MOSS.get())),
                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK)))));

        register(context, PODZOL_PATCH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        60, // lots of tries...
                        2,  // ...concentrated in a small area = solid-looking patch, not scatter
                        2,
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(Blocks.PODZOL)),
                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK)))));

        register(context, FOREST_MOSS_CARPET_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        40, 4, 2, // moderate tries/spread = partial coverage, not blanket
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(ModBlocks.FOREST_MOSS_CARPET.get())),
                                BlockPredicateFilter.forPredicate(forestMossCarpetPlacementPredicate()))));

        register(context, BLUE_BERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        6, 4, 3, // fewer tries than vanilla's usual ~32 = noticeably rarer
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(ModBlocks.BLUE_BERRY_BUSH.get())),
                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.allOf(
                                                BlockPredicate.matchesBlocks(Blocks.AIR),
                                                BlockPredicate.matchesBlocks(BlockPos.ZERO.below(), Blocks.GRASS_BLOCK))))));

    }

    private static BlockPredicate cattailsPlacementPredicate() {
        return BlockPredicate.allOf(
                BlockPredicate.matchesBlocks(Blocks.WATER),
                BlockPredicate.matchesFluids(Fluids.WATER),
                BlockPredicate.matchesBlocks(BlockPos.ZERO.above(), Blocks.AIR),
                BlockPredicate.anyOf(
                        BlockPredicate.matchesTag(BlockPos.ZERO.below(), BlockTags.DIRT),
                        BlockPredicate.matchesTag(BlockPos.ZERO.below(), BlockTags.SAND),
                        BlockPredicate.matchesBlocks(
                                BlockPos.ZERO.below(),
                                Blocks.CLAY,
                                Blocks.MUD,
                                Blocks.GRAVEL)));
    }

    private static WeightedStateProvider forestFloorWeightedProvider() {
        return new WeightedStateProvider(
                SimpleWeightedRandomList.<BlockState>builder()
                        .add(Blocks.GRASS_BLOCK.defaultBlockState(), 60)
                        .add(Blocks.COARSE_DIRT.defaultBlockState(), 20)
                        .add(Blocks.ROOTED_DIRT.defaultBlockState(), 20)
                        .build());
    }

    private static BlockPredicate forestMossCarpetPlacementPredicate() {
        return BlockPredicate.allOf(
                BlockPredicate.matchesBlocks(Blocks.AIR), // the spot itself must be empty
                BlockPredicate.matchesBlocks(BlockPos.ZERO.below(), ModBlocks.FOREST_MOSS.get())); // moss specifically below — not dirt, not grass
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}