package com.kanjpz.meowski.worldgen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModNoiseGeneratorSettings {

    public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> context) {

        // Reconstructs vanilla's current overworld settings from the live game code.
        NoiseGeneratorSettings vanillaOverworld =
                NoiseGeneratorSettings.overworld(context, false, false);

        SurfaceRules.RuleSource combinedSurfaceRule = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(Biomes.FOREST),
                        ModSurfaceRules.forestFloor()),
                vanillaOverworld.surfaceRule()
        );

        NoiseGeneratorSettings overridden = new NoiseGeneratorSettings(
                vanillaOverworld.noiseSettings(),
                Blocks.DIAMOND_BLOCK.defaultBlockState(), // <-- SANITY CHECK, see below
                vanillaOverworld.defaultFluid(),
                vanillaOverworld.noiseRouter(),
                combinedSurfaceRule,
                vanillaOverworld.spawnTarget(),
                vanillaOverworld.seaLevel(),
                vanillaOverworld.disableMobGeneration(),
                vanillaOverworld.aquifersEnabled(),
                vanillaOverworld.oreVeinsEnabled(),
                vanillaOverworld.useLegacyRandomSource()
        );

        context.register(NoiseGeneratorSettings.OVERWORLD, overridden);
    }
}