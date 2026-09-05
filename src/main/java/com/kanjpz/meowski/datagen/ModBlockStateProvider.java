package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, meowski.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        berryBushBlock(ModBlocks.BLUE_BERRY_BUSH.get());
        catTailsBlock(ModBlocks.CATTAILS.get());

        logBlock(((RotatedPillarBlock) ModBlocks.WILLOW_LOG.get()));
        simpleBlockItem(ModBlocks.WILLOW_LOG.get(), models().getExistingFile(modLoc("block/willow_log")));

        axisBlock(((RotatedPillarBlock) ModBlocks.WILLOW_WOOD.get()), blockTexture(ModBlocks.WILLOW_LOG.get()), blockTexture(ModBlocks.WILLOW_LOG.get()));
        simpleBlockItem(ModBlocks.WILLOW_WOOD.get(), models().getExistingFile(modLoc("block/willow_wood")));

        logBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WILLOW_LOG.get()));
        simpleBlockItem(ModBlocks.STRIPPED_WILLOW_LOG.get(), models().getExistingFile(modLoc("block/stripped_willow_log")));

        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_WILLOW_WOOD.get()), blockTexture(ModBlocks.STRIPPED_WILLOW_LOG.get()), blockTexture(ModBlocks.STRIPPED_WILLOW_LOG.get()));
        simpleBlockItem(ModBlocks.STRIPPED_WILLOW_WOOD.get(), models().getExistingFile(modLoc("block/stripped_willow_wood")));

        simpleBlockWithItem(ModBlocks.WILLOW_PLANKS.get(), cubeAll(ModBlocks.WILLOW_PLANKS.get()));

        simpleBlockWithItem(
                ModBlocks.WILLOW_LEAVES.get(),
                models()
                        .withExistingParent("willow_leaves",
                                mcLoc("block/leaves"))
                        .texture("all",
                                blockTexture(ModBlocks.WILLOW_LEAVES.get()))
                        .renderType("cutout"));

        simpleBlockWithItem(ModBlocks.WILLOW_SAPLING.get(), models().cross("willow_sapling",
                blockTexture(ModBlocks.WILLOW_SAPLING.get())).renderType("cutout"));

        simpleBlockWithItem(
                ModBlocks.FOREST_MOSS.get(),
                cubeAll(ModBlocks.FOREST_MOSS.get()));
        simpleBlockItem(
                ModBlocks.CATTAILS.get(),
                models().getExistingFile(
                        modLoc("block/cattails")
                )
        );
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
    private void catTailsBlock(Block block) {

        getVariantBuilder(block)
                .forAllStates(state -> {

                    DoubleBlockHalf half =
                            state.getValue(DoublePlantBlock.HALF);

                    // UPPER HALF:
                    // It is invisible, so rotation doesn't matter.
                    if (half == DoubleBlockHalf.UPPER) {

                        return ConfiguredModel.builder()
                                .modelFile(
                                        models().getExistingFile(
                                                modLoc("block/cattails_empty")
                                        )
                                )
                                .build();
                    }

                    // LOWER HALF:
                    // Give Minecraft 4 possible rotations.
                    // It picks one, making cattails look randomly rotated.
                    return ConfiguredModel.builder()

                            .modelFile(
                                    models().getExistingFile(
                                            modLoc("block/cattails")
                                    )
                            )
                            .rotationY(0)
                            .nextModel()

                            .modelFile(
                                    models().getExistingFile(
                                            modLoc("block/cattails")
                                    )
                            )
                            .rotationY(90)
                            .nextModel()

                            .modelFile(
                                    models().getExistingFile(
                                            modLoc("block/cattails")
                                    )
                            )
                            .rotationY(180)
                            .nextModel()

                            .modelFile(
                                    models().getExistingFile(
                                            modLoc("block/cattails")
                                    )
                            )
                            .rotationY(270)

                            .build();
                });
    }
}