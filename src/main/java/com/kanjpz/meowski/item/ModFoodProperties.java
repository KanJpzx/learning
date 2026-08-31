package com.kanjpz.meowski.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties BLUE_BERRIES = (new FoodProperties.Builder().nutrition(2).saturationModifier(0.15f)
            .fast().alwaysEdible().build());

}
