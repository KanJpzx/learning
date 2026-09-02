package com.kanjpz.meowski.worldgen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_MOSS_PATCH_BONEMEAL_KEY = registerKey("forest_moss_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY = registerKey("willow");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        var blockGetter = context.lookup(Registries.BLOCK);

        register(context, WILLOW_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                new UpwardsBranchingTrunkPlacer(
                        8,
                        5,
                        5,
                        UniformInt.of(4, 6),
                        0.55F,
                        UniformInt.of(4, 5),
                        blockGetter.getOrThrow(BlockTags.LEAVES)
                ),

                BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
                new CherryFoliagePlacer(
                        ConstantInt.of(3),
                        ConstantInt.of(2),
                        ConstantInt.of(4),
                        0.50F,
                        0.65F,
                        0.65F,
                        0.55F),

                    new TwoLayersFeatureSize(1, 0,2)).ignoreVines().build());


    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));

    }


    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
