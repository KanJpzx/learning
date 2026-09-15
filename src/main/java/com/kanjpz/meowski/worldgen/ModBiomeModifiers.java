package com.kanjpz.meowski.worldgen;

import com.kanjpz.meowski.meowski;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;


public class ModBiomeModifiers {

    // =========================================================
    // SWAMP COLORS
    // =========================================================

    // Our single swamp grass color.
    private static final int SWAMP_GRASS_COLOR = 0x469E56;
    private static final int SWAMP_FOLIAGE_COLOR = 0x769945;
    private static final int SWAMP_WATER_COLOR = 0x488385;
    private static final int SWAMP_WATER_FOG_COLOR = 0x294849;


    // =========================================================
    // CUSTOM BIOME MODIFIER CODEC
    // =========================================================

    private static final DeferredRegister<MapCodec<? extends BiomeModifier>>
            BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                    meowski.MOD_ID);

    private static final DeferredHolder<
            MapCodec<? extends BiomeModifier>,
            MapCodec<SwampColorsModifier>
            > SWAMP_COLORS_CODEC =
            BIOME_MODIFIER_SERIALIZERS.register(
                    "swamp_colors",
                    () -> RecordCodecBuilder.mapCodec(instance ->
                            instance.group(

                                    Biome.LIST_CODEC
                                            .fieldOf("biomes")
                                            .forGetter(SwampColorsModifier::biomes),

                                    Codec.INT
                                            .fieldOf("grass_color")
                                            .forGetter(SwampColorsModifier::grassColor),

                                    Codec.INT
                                            .fieldOf("foliage_color")
                                            .forGetter(SwampColorsModifier::foliageColor),

                                    Codec.INT
                                            .fieldOf("water_color")
                                            .forGetter(SwampColorsModifier::waterColor),

                                    Codec.INT
                                            .fieldOf("water_fog_color")
                                            .forGetter(SwampColorsModifier::waterFogColor)

                            ).apply(
                                    instance,
                                    SwampColorsModifier::new
                            )
                    )
            );



    // =========================================================
    // BIOME MODIFIER KEYS
    // =========================================================

    public static final ResourceKey<BiomeModifier> WILLOW_KEY =
            registerKey("willow");


    public static final ResourceKey<BiomeModifier> OAK_KEY =
            registerKey("oak");
    public static final ResourceKey<BiomeModifier> BIRCH_KEY =
            registerKey("birch");


    public static final ResourceKey<BiomeModifier> CATTAILS_KEY =
            registerKey("cattails");



    public static final ResourceKey<BiomeModifier> REMOVE_SWAMP_TREES =
            registerKey("remove_swamp_trees");
    public static final ResourceKey<BiomeModifier> REMOVE_OAK_TREES =
            registerKey("remove_oak_trees");




    public static final ResourceKey<BiomeModifier> SWAMP_COLORS_KEY =
            registerKey("swamp_color");


    // =========================================================
    // REGISTER THE CUSTOM CODEC
    // =========================================================

    public static void register(IEventBus modEventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }


    // =========================================================
    // DATAGEN BOOTSTRAP
    // =========================================================

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {

        // CF -> PF -> BM
        var placedFeatures =
                context.lookup(Registries.PLACED_FEATURE);

        var biomes =
                context.lookup(Registries.BIOME);


        // -----------------------------------------------------
        // REMOVE VANILLA SWAMP TREES
        // -----------------------------------------------------

        context.register(REMOVE_SWAMP_TREES,
                BiomeModifiers.RemoveFeaturesBiomeModifier.allSteps(
                        HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP)),
                        HolderSet.direct(placedFeatures.getOrThrow(VegetationPlacements.TREES_SWAMP))));

        context.register(REMOVE_OAK_TREES,
                BiomeModifiers.RemoveFeaturesBiomeModifier.allSteps(
                        HolderSet.direct(biomes.getOrThrow(Biomes.FOREST)),
                        HolderSet.direct(placedFeatures.getOrThrow(VegetationPlacements.TREES_BIRCH_AND_OAK))));




        // -----------------------------------------------------
        // ADD OUR WILLOW TREES
        // -----------------------------------------------------

        context.register(WILLOW_KEY,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP)),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILLOW_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(OAK_KEY,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(List.of(biomes.getOrThrow(Biomes.FOREST))),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OAK_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(BIRCH_KEY,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        HolderSet.direct(List.of(biomes.getOrThrow(Biomes.FOREST))),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BIRCH_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(CATTAILS_KEY,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        cattailBiomes(biomes),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CATTAILS_KEY)),
                        GenerationStep.Decoration.VEGETAL_DECORATION));



        // NOTE: forest_moss_patch / coarse_dirt_patch / rooted_dirt_patch used to be
        // registered here as scattered AddFeaturesBiomeModifier disk placements. That job
        // is now done continuously by the ModSurfaceRules "forest floor" blend wired into
        // minecraft:overworld's surface rule (see ModNoiseGeneratorSettings), so these are
        // removed to avoid double-placing the same blocks. The old ConfiguredFeature /
        // PlacedFeature entries (ModConfiguredFeatures / ModPlacedFeatures) are now unused
        // and safe to delete once you've confirmed the surface-rule version looks right.

        // -----------------------------------------------------
        // CHANGE SWAMP GRASS COLOR
        // -----------------------------------------------------

        context.register(
                SWAMP_COLORS_KEY,
                new SwampColorsModifier(
                        HolderSet.direct(
                                biomes.getOrThrow(Biomes.SWAMP)
                        ),
                        SWAMP_GRASS_COLOR,
                        SWAMP_FOLIAGE_COLOR,
                        SWAMP_WATER_COLOR,
                        SWAMP_WATER_FOG_COLOR));



    }

    private static HolderSet<Biome> cattailBiomes(HolderGetter<Biome> biomes) {
        return HolderSet.direct(List.of(
                biomes.getOrThrow(Biomes.PLAINS),
                biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS),
                biomes.getOrThrow(Biomes.SNOWY_PLAINS),
                biomes.getOrThrow(Biomes.DESERT),
                biomes.getOrThrow(Biomes.SWAMP),
                biomes.getOrThrow(Biomes.MANGROVE_SWAMP),
                biomes.getOrThrow(Biomes.FOREST),
                biomes.getOrThrow(Biomes.FLOWER_FOREST),
                biomes.getOrThrow(Biomes.BIRCH_FOREST),
                biomes.getOrThrow(Biomes.DARK_FOREST),
                biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST),
                biomes.getOrThrow(Biomes.OLD_GROWTH_PINE_TAIGA),
                biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                biomes.getOrThrow(Biomes.TAIGA),
                biomes.getOrThrow(Biomes.SNOWY_TAIGA),
                biomes.getOrThrow(Biomes.SAVANNA),
                biomes.getOrThrow(Biomes.SAVANNA_PLATEAU),
                biomes.getOrThrow(Biomes.JUNGLE),
                biomes.getOrThrow(Biomes.SPARSE_JUNGLE),
                biomes.getOrThrow(Biomes.BAMBOO_JUNGLE),
                biomes.getOrThrow(Biomes.BADLANDS),
                biomes.getOrThrow(Biomes.ERODED_BADLANDS),
                biomes.getOrThrow(Biomes.CHERRY_GROVE),
                biomes.getOrThrow(Biomes.GROVE),
                biomes.getOrThrow(Biomes.RIVER),
                biomes.getOrThrow(Biomes.SNOWY_BEACH),
                biomes.getOrThrow(Biomes.STONY_SHORE),
                biomes.getOrThrow(Biomes.MUSHROOM_FIELDS)
        ));
    }


    // =========================================================
    // CUSTOM SWAMP COLOR MODIFIER
    // =========================================================

    public record SwampColorsModifier(
            HolderSet<Biome> biomes,
            int grassColor,
            int foliageColor,
            int waterColor,
            int waterFogColor
    ) implements BiomeModifier {

        @Override
        public void modify(
                Holder<Biome> biome,
                Phase phase,
                ModifiableBiomeInfo.BiomeInfo.Builder builder
        ) {

            if (phase == Phase.MODIFY && biomes.contains(biome)) {

                var effects = builder.getSpecialEffects();

                effects.grassColorOverride(grassColor);

                effects.grassColorModifier(
                        BiomeSpecialEffects.GrassColorModifier.NONE
                );

                effects.foliageColorOverride(foliageColor);
                effects.waterColor(waterColor);
                effects.waterFogColor(waterFogColor);
            }
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return SWAMP_COLORS_CODEC.get();
        }
    }


    // =========================================================
    // RESOURCE KEY HELPER
    // =========================================================

    private static ResourceKey<BiomeModifier> registerKey(
            String name) {

        return ResourceKey.create(

                NeoForgeRegistries.Keys.BIOME_MODIFIERS,

                ResourceLocation.fromNamespaceAndPath(
                        meowski.MOD_ID, name));
    }
}
