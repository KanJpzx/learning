package com.kanjpz.meowski.worldgen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Overrides "minecraft:overworld" noise settings so that Forest biomes run our blended
 * forest-floor rule (ModSurfaceRules) BEFORE falling through to vanilla's own surface
 * rule for everything else. Because this is registered under vanilla's own resource key
 * (NoiseGeneratorSettings.OVERWORLD), the file our datagen writes to
 * data/minecraft/worldgen/noise_settings/overworld.json overrides the base game's copy
 * of that file when this mod's data loads - the same override-by-path mechanism NeoForge
 * documents for biome modifiers.
 *
 * IMPORTANT: only one mod/datapack can win this override. If you later add another mod
 * that also overrides minecraft:overworld's noise settings, whichever loads last wins
 * and the other's changes are silently dropped. If that ever happens, the fix is to
 * apply this same wrapping to whatever RuleSource that other mod/datapack produces,
 * rather than to vanilla's directly.
 */
public class ModNoiseGeneratorSettings {

    public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> context) {

        // Reconstructs vanilla's current overworld settings from the live game code
        // (not copied text) so this always matches whatever version you're building
        // against. false, false = not amplified, not large-biomes.
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
                vanillaOverworld.defaultBlock(),
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
