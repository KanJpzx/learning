package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, meowski.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.WILLOW_LOG.get().asItem())
                .add(ModBlocks.WILLOW_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_WILLOW_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_WILLOW_LOG.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.WILLOW_PLANKS.get().asItem());

        this.tag(ItemTags.FLOWERS)
                .add(ModBlocks.FOREST_MOSS.get().asItem());
        this.tag(ItemTags.WOODEN_STAIRS)
                .add(ModBlocks.WILLOW_STAIRS.get().asItem());

        this.tag(ItemTags.WOODEN_SLABS)
                .add(ModBlocks.WILLOW_SLAB.get().asItem());

        this.tag(ItemTags.WOODEN_FENCES)
                .add(ModBlocks.WILLOW_FENCE.get().asItem());

        this.tag(ItemTags.FENCE_GATES)
                .add(ModBlocks.WILLOW_FENCE_GATE.get().asItem());

        this.tag(ItemTags.WOODEN_DOORS)
                .add(ModBlocks.WILLOW_DOOR.get().asItem());

        this.tag(ItemTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.WILLOW_TRAPDOOR.get().asItem());

        this.tag(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.WILLOW_PRESSURE_PLATE.get().asItem());

        this.tag(ItemTags.WOODEN_BUTTONS)
                .add(ModBlocks.WILLOW_BUTTON.get().asItem());

        this.tag(ItemTags.SAPLINGS)
                .add(ModBlocks.WILLOW_SAPLING.get().asItem());

    }
}
