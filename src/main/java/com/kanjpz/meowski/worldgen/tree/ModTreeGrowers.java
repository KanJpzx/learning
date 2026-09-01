package com.kanjpz.meowski.worldgen.tree;

import com.kanjpz.meowski.meowski;
import com.kanjpz.meowski.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower WILLOW = new TreeGrower(meowski.MOD_ID + ":willow",
            Optional.empty(), Optional.of(ModConfiguredFeatures.WILLOW_KEY), Optional.empty());
}
