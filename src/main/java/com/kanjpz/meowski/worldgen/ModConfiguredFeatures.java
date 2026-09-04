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
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY = registerKey("willow");

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

                                0.65F, // holes in wide bottom layer
                                0.50F, // corner hole chance
                                0.3F, // hanging leaves chance
                                0.6F), // chance hanging leaves extend farther
                        // HOW MUCH SPACE THE TREE NEEDS
                        new TwoLayersFeatureSize(0, 0, 0))
                            .ignoreVines()
                            .build());
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));

    }


    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
