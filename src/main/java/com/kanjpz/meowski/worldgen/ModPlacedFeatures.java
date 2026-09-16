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
    public static final ResourceKey<PlacedFeature> BUSH_KEY = registerKey("bush");
    public static final ResourceKey<PlacedFeature> CATTAILS_KEY = registerKey("cattails");
    public static final ResourceKey<PlacedFeature> FOREST_FLOOR_MIX_KEY = registerKey("forest_floor_mix");
    public static final ResourceKey<PlacedFeature> FOREST_MOSS_PATCH_KEY = registerKey("forest_moss_patch");
    public static final ResourceKey<PlacedFeature> PODZOL_PATCH_KEY = registerKey("podzol_patch");
    public static final ResourceKey<PlacedFeature> FOREST_MOSS_CARPET_KEY = registerKey("forest_moss_carpet_patch");
    public static final ResourceKey<PlacedFeature> BLUE_BERRY_BUSH_KEY = registerKey("blue_berry_bush_patch");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

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
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 1),
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
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}