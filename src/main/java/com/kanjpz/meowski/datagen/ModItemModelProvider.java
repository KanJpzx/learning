package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.item.ModItems;
import com.kanjpz.meowski.meowski;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, meowski.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SWAMP_SAPLING.get());
        basicItem(ModItems.WILLOW_SAPLING.get());
        basicItem(ModItems.BLUE_BERRIES.get());
    }
}
