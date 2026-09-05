package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, meowski.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //tag(BlockTags.MINEABLE_WITH_PICKAXE)
                //.add(ModBlocks.RUBY_ORE.get());
        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.WILLOW_LOG.get())
                .add(ModBlocks.WILLOW_WOOD.get())
                .add(ModBlocks.STRIPPED_WILLOW_WOOD.get())
                .add(ModBlocks.STRIPPED_WILLOW_LOG.get());
        this.tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.FOREST_MOSS.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.WILLOW_PLANKS.get());

        this.tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.WILLOW_STAIRS.get());

        this.tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.WILLOW_SLAB.get());

        this.tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.WILLOW_FENCE.get());

        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.WILLOW_FENCE_GATE.get());

        this.tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.WILLOW_DOOR.get());

        this.tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.WILLOW_TRAPDOOR.get());

        this.tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.WILLOW_PRESSURE_PLATE.get());

        this.tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.WILLOW_BUTTON.get());

        this.tag(BlockTags.SAPLINGS)
                .add(ModBlocks.WILLOW_SAPLING.get());

        this.tag(BlockTags.LEAVES)
                .add(ModBlocks.WILLOW_LEAVES.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        ModBlocks.WILLOW_LOG.get(),
                        ModBlocks.WILLOW_WOOD.get(),
                        ModBlocks.STRIPPED_WILLOW_LOG.get(),
                        ModBlocks.STRIPPED_WILLOW_WOOD.get(),
                        ModBlocks.WILLOW_PLANKS.get(),
                        ModBlocks.WILLOW_STAIRS.get(),
                        ModBlocks.WILLOW_SLAB.get(),
                        ModBlocks.WILLOW_FENCE.get(),
                        ModBlocks.WILLOW_FENCE_GATE.get(),
                        ModBlocks.WILLOW_DOOR.get(),
                        ModBlocks.WILLOW_TRAPDOOR.get(),
                        ModBlocks.WILLOW_PRESSURE_PLATE.get(),
                        ModBlocks.WILLOW_BUTTON.get()
                );

    }
}
