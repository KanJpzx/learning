package com.kanjpz.meowski.worldgen;

import com.kanjpz.meowski.meowski;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * Custom noise fields used to blend forest-floor surface blocks (see ModSurfaceRules).
 * Each field runs at a different scale/seed so the patches it drives don't line up with
 * each other - that's what keeps the result looking scattered instead of banded/ringed.
 */
public class ModNoises {

    // Big, sparse blobs - forest moss "islands".
    public static final ResourceKey<NormalNoise.NoiseParameters> FOREST_MOSS_PATCHES =
            registerKey("forest_moss_patches");

    // Medium-scale patches - coarse dirt clearings.
    public static final ResourceKey<NormalNoise.NoiseParameters> COARSE_DIRT_PATCHES =
            registerKey("coarse_dirt_patches");

    // Fine, high-frequency speckle - rooted dirt under root systems.
    public static final ResourceKey<NormalNoise.NoiseParameters> ROOTED_DIRT_SPECKLE =
            registerKey("rooted_dirt_speckle");

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {
        // NormalNoise.NoiseParameters(firstOctave, amplitudes...)
        // More negative firstOctave + a single amplitude = smoother, bigger features.
        // Less negative firstOctave = tighter, higher-frequency noise.
        context.register(FOREST_MOSS_PATCHES, new NormalNoise.NoiseParameters(-5, 1.0, 1.0));
        context.register(COARSE_DIRT_PATCHES, new NormalNoise.NoiseParameters(-4, 1.0, 1.0));
        context.register(ROOTED_DIRT_SPECKLE, new NormalNoise.NoiseParameters(-3, 1.0));
    }

    private static ResourceKey<NormalNoise.NoiseParameters> registerKey(String name) {
        return ResourceKey.create(Registries.NOISE,
                ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, name));
    }
}
