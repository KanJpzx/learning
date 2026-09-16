package com.kanjpz.meowski.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record BoulderConfiguration(
        BlockStateProvider stateProvider,
        IntProvider radius,
        IntProvider lumps
) implements FeatureConfiguration {
    public static final Codec<BoulderConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(BoulderConfiguration::stateProvider),
                    IntProvider.CODEC.fieldOf("radius").forGetter(BoulderConfiguration::radius),
                    IntProvider.CODEC.fieldOf("lumps").forGetter(BoulderConfiguration::lumps)
            ).apply(instance, BoulderConfiguration::new));
}
