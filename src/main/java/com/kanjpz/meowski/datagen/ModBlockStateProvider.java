package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, meowski.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        logBlock(ModBlocks.WILLOW_LOG.get());
        itemModels().withExistingParent("willow_log", modLoc("block/willow_log"));

        berryBushBlock(ModBlocks.BLUE_BERRY_BUSH.get());
    }

    private void berryBushBlock(Block block) {
        getVariantBuilder(block).forAllStates(state -> {
            int age = state.getValue(SweetBerryBushBlock.AGE);

            return ConfiguredModel.builder()
                    .modelFile(models().cross("blue_berry_bush_stage" + age,
                            modLoc("block/blueberry_bush_stage" + age)).renderType("cutout"))
                    .build();
        });
    }
}
