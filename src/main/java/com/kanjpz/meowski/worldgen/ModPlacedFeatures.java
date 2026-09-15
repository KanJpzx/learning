package com.kanjpz.meowski.worldgen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> WILLOW_KEY = registerKey("willow");
    public static final ResourceKey<PlacedFeature> OAK_KEY = registerKey("oak");
    public static final ResourceKey<PlacedFeature> BIRCH_KEY = registerKey("birch");
    public static final ResourceKey<PlacedFeature> CATTAILS_KEY = registerKey("cattails");
    public static final ResourceKey<PlacedFeature> FOREST_MOSS_KEY = registerKey("forest_moss_patch");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, WILLOW_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILLOW_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2,0.2f, 1),
                        ModBlocks.WILLOW_SAPLING.get()));

        register(context, OAK_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2),
                        Blocks.OAK_SAPLING));

        register(context, BIRCH_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BIRCH_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 1),
                        Blocks.BIRCH_SAPLING));

        register(context, CATTAILS_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CATTAILS_KEY),
                List.of(CountPlacement.of(2),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(62),
                                VerticalAnchor.absolute(62)),
                        BiomeFilter.biome()));

        register(context, FOREST_MOSS_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.FOREST_MOSS_KEY),
                List.of(
                        CountPlacement.of(3), // ← patches attempted per chunk — THIS is your "chaotic" knob, turn it down
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), // finds the actual surface, not a random spot
                        BiomeFilter.biome()
                ));

    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
