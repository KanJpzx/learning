package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.item.ModItems;
import com.kanjpz.meowski.meowski;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, meowski.MOD_ID, existingFileHelper);
    }
    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, "block/" + item.getId().getPath()));

    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.BLUE_BERRIES.get());
        basicItem(ModBlocks.WILLOW_DOOR.get().asItem());

        withExistingParent(ModBlocks.CATTAILS.getId().getPath(),
                ResourceLocation.parse("item/generated"))
                .texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID,
                                "item/cattails"));

        //withExistingParent(ModItems.LILY_PADS.getId().getPath(),
                //ResourceLocation.parse("item/generated"))
                //.texture("layer0", ResourceLocation.fromNamespaceAndPath(meowski.MOD_ID, "item/lily_pads_2d"));

        saplingItem(ModBlocks.WILLOW_SAPLING);
    }

}
