package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries);
    }


    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        /*
         * =========================================================
         * WILLOW PLANKS
         * =========================================================
         *
         * Any willow log/wood variant:
         *
         * 1 log/wood -> 4 willow planks
         */

        ShapelessRecipeBuilder.shapeless(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_PLANKS.get(),
                        4
                )
                .requires(
                        Ingredient.of(
                                ModBlocks.WILLOW_LOG.get(),
                                ModBlocks.WILLOW_WOOD.get(),
                                ModBlocks.STRIPPED_WILLOW_LOG.get(),
                                ModBlocks.STRIPPED_WILLOW_WOOD.get()
                        )
                )
                .unlockedBy(
                        "has_willow_log",
                        has(ModBlocks.WILLOW_LOG.get())
                )
                .unlockedBy(
                        "has_willow_wood",
                        has(ModBlocks.WILLOW_WOOD.get())
                )
                .unlockedBy(
                        "has_stripped_willow_log",
                        has(ModBlocks.STRIPPED_WILLOW_LOG.get())
                )
                .unlockedBy(
                        "has_stripped_willow_wood",
                        has(ModBlocks.STRIPPED_WILLOW_WOOD.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW WOOD
         * =========================================================
         *
         * LL
         * LL
         *
         * 4 logs -> 3 wood
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_WOOD.get(),
                        3
                )
                .pattern("LL")
                .pattern("LL")
                .define('L', ModBlocks.WILLOW_LOG.get())
                .unlockedBy(
                        "has_willow_log",
                        has(ModBlocks.WILLOW_LOG.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * STRIPPED WILLOW WOOD
         * =========================================================
         *
         * LL
         * LL
         *
         * 4 stripped logs -> 3 stripped wood
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.STRIPPED_WILLOW_WOOD.get(),
                        3
                )
                .pattern("LL")
                .pattern("LL")
                .define('L', ModBlocks.STRIPPED_WILLOW_LOG.get())
                .unlockedBy(
                        "has_stripped_willow_log",
                        has(ModBlocks.STRIPPED_WILLOW_LOG.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW STAIRS
         * =========================================================
         *
         * P
         * PP
         * PPP
         *
         * 6 planks -> 4 stairs
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_STAIRS.get(),
                        4
                )
                .pattern("P  ")
                .pattern("PP ")
                .pattern("PPP")
                .define('P', ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW SLAB
         * =========================================================
         *
         * PPP
         *
         * 3 planks -> 6 slabs
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_SLAB.get(),
                        6
                )
                .pattern("PPP")
                .define('P', ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW FENCE
         * =========================================================
         *
         * P S P
         * P S P
         *
         * P = willow planks
         * S = stick
         *
         * -> 3 fences
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_FENCE.get(),
                        3
                )
                .pattern("PSP")
                .pattern("PSP")
                .define('P', ModBlocks.WILLOW_PLANKS.get())
                .define('S', Items.STICK)
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW FENCE GATE
         * =========================================================
         *
         * S P S
         * S P S
         *
         * -> 1 fence gate
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_FENCE_GATE.get()
                )
                .pattern("SPS")
                .pattern("SPS")
                .define('P', ModBlocks.WILLOW_PLANKS.get())
                .define('S', Items.STICK)
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW DOOR
         * =========================================================
         *
         * PP
         * PP
         * PP
         *
         * 6 planks -> 3 doors
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_DOOR.get(),
                        3
                )
                .pattern("PP")
                .pattern("PP")
                .pattern("PP")
                .define('P', ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW TRAPDOOR
         * =========================================================
         *
         * PPP
         * PPP
         *
         * 6 planks -> 2 trapdoors
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.WILLOW_TRAPDOOR.get(),
                        2
                )
                .pattern("PPP")
                .pattern("PPP")
                .define('P', ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW PRESSURE PLATE
         * =========================================================
         *
         * PP
         *
         * 2 planks -> 1 pressure plate
         */

        ShapedRecipeBuilder.shaped(
                        RecipeCategory.REDSTONE,
                        ModBlocks.WILLOW_PRESSURE_PLATE.get()
                )
                .pattern("PP")
                .define('P', ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);


        /*
         * =========================================================
         * WILLOW BUTTON
         * =========================================================
         *
         * 1 plank -> 1 button
         */

        ShapelessRecipeBuilder.shapeless(
                        RecipeCategory.REDSTONE,
                        ModBlocks.WILLOW_BUTTON.get()
                )
                .requires(ModBlocks.WILLOW_PLANKS.get())
                .unlockedBy(
                        "has_willow_planks",
                        has(ModBlocks.WILLOW_PLANKS.get())
                )
                .save(recipeOutput);
    }
}