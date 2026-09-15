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
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
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
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_MOSS_KEY = registerKey("forest_moss_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COARSE_DIRT_PATCH_KEY = registerKey("coarse_dirt_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROOTED_DIRT_PATCH_KEY = registerKey("rooted_dirt_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_FLOOR_MIX_KEY = registerKey("forest_floor_mix");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUD_PATCH_KEY = registerKey("mud_patch");


    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_OAK_OVERRIDE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.withDefaultNamespace("oak"));



    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        var blockGetter = context.lookup(Registries.BLOCK);

        register(context, WILLOW_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                        new FancyTrunkPlacer(
                                9,  // base height
                                5,  // random extra height A
                                2),   // random extra height B
                        // WHAT BLOCK MAKES THE LEAVES?
                        BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),

                        // HOW DOES THE CANOPY GENERATE?
                        new CherryFoliagePlacer(
                                ConstantInt.of(4), // radius
                                ConstantInt.of(1), // offset
                                ConstantInt.of(4), // foliage height

                                0.75F, // holes in wide bottom layer
                                0.65F, // corner hole chance
                                0.3F, // hanging leaves chance
                                0.6F), // chance hanging leaves extend farther
                        // HOW MUCH SPACE THE TREE NEEDS
                        new TwoLayersFeatureSize(0, 0, 0))
                            .ignoreVines()
                            .build());

        register(context, OAK_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG.defaultBlockState()),
                        new FancyTrunkPlacer(
                                10,
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
                                9,
                                5,
                                2),
                        BlockStateProvider.simple(Blocks.BIRCH_LEAVES.defaultBlockState()),

                        new AcaciaFoliagePlacer(
                                ConstantInt.of(2), // radius — bigger = wider flat top
                                ConstantInt.of(2)),// offset
                        new TwoLayersFeatureSize(0, 0, 0))
                        .ignoreVines()
                        .build());

        register(context, CATTAILS_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        10,
                        6,
                        0,
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(ModBlocks.CATTAILS.get())),
                                BlockPredicateFilter.forPredicate(cattailsPlacementPredicate()))));

        register(context, FOREST_MOSS_KEY, Feature.DISK,
                new DiskConfiguration(
                        RuleBasedBlockStateProvider.simple(ModBlocks.FOREST_MOSS.get()),
                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK),
                        UniformInt.of(2, 4), // radius
                        1));                 // depth

        register(context, COARSE_DIRT_PATCH_KEY, Feature.DISK,
                new DiskConfiguration(
                        RuleBasedBlockStateProvider.simple(Blocks.COARSE_DIRT),
                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK, Blocks.DIRT),
                        UniformInt.of(1, 2),
                        2));

        register(context, ROOTED_DIRT_PATCH_KEY, Feature.DISK,
                new DiskConfiguration(

                        RuleBasedBlockStateProvider.simple(Blocks.ROOTED_DIRT),
                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK, Blocks.DIRT),
                        UniformInt.of(1, 3),
                        2));

        register(context, MUD_PATCH_KEY, Feature.DISK,
                new DiskConfiguration(

                        RuleBasedBlockStateProvider.simple(Blocks.MUD),
                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK, Blocks.DIRT),
                        UniformInt.of(1, 3),
                        2));

        register(context, FOREST_FLOOR_MIX_KEY, Feature.DISK,
                new DiskConfiguration(
                        forestFloorMixProvider(),   // ← THIS is what makes "no usages" go away
                        BlockPredicate.matchesBlocks(Blocks.GRASS_BLOCK, Blocks.DIRT),
                        UniformInt.of(5, 8),
                        2));
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
    private static RuleBasedBlockStateProvider forestFloorMixProvider() {
        NoiseThresholdProvider mossNoise = new NoiseThresholdProvider(
                2345L,
                new NormalNoise.NoiseParameters(-4, 1.0, 1.0),
                0.4F,
                0.55F,
                0.7F,
                Blocks.GRASS_BLOCK.defaultBlockState(),
                List.of(Blocks.COARSE_DIRT.defaultBlockState()),
                List.of(ModBlocks.FOREST_MOSS.get().defaultBlockState()));

        return new RuleBasedBlockStateProvider(mossNoise, List.of());
    }

    private static BlockPredicate mossPlacementPredicate() {
        return BlockPredicate.allOf(
                BlockPredicate.matchesBlocks(BlockPos.ZERO.above(), Blocks.AIR),
                BlockPredicate.matchesTag(BlockPos.ZERO.below(), BlockTags.DIRT));
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));

    }


    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
