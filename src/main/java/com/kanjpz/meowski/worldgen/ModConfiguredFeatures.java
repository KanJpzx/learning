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

    public static final ResourceKey<ConfiguredFeature<?, ?>> SANITY_TEST_KEY = registerKey("sanity_test");

    public static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_OAK_OVERRIDE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.withDefaultNamespace("oak"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        var blockGetter = context.lookup(Registries.BLOCK);

        register(context, WILLOW_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                        new FancyTrunkPlacer(9, 5, 2),
                        BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
                        new CherryFoliagePlacer(
                                ConstantInt.of(4), ConstantInt.of(1), ConstantInt.of(4),
                                0.75F, 0.65F, 0.3F, 0.6F),
                        new TwoLayersFeatureSize(0, 0, 0))
                        .ignoreVines()
                        .build());

        register(context, OAK_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG.defaultBlockState()),
                        new FancyTrunkPlacer(10, 5, 3),
                        BlockStateProvider.simple(Blocks.OAK_LEAVES.defaultBlockState()),
                        new CherryFoliagePlacer(
                                ConstantInt.of(4), ConstantInt.of(2), ConstantInt.of(4),
                                0.45F, 0.65F, 0.3F, 0.45F),
                        new TwoLayersFeatureSize(0, 0, 0))
                        .ignoreVines()
                        .build());

        register(context, BIRCH_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.BIRCH_LOG.defaultBlockState()),
                        new FancyTrunkPlacer(9, 3, 1),
                        BlockStateProvider.simple(Blocks.BIRCH_LEAVES.defaultBlockState()),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2)),
                        new TwoLayersFeatureSize(0, 0, 0))
                        .ignoreVines()
                        .build());

        register(context, CATTAILS_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        10, 6, 0,
                        PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        BlockStateProvider.simple(ModBlocks.CATTAILS.get())),
                                BlockPredicateFilter.forPredicate(cattailsPlacementPredicate()))));

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