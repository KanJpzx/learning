package com.kanjpz.meowski.block;

import com.kanjpz.meowski.block.custom.*;
import com.kanjpz.meowski.item.ModItems;
import com.kanjpz.meowski.meowski;
import com.kanjpz.meowski.worldgen.tree.ModTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;

import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(meowski.MOD_ID);

    public static final BlockSetType WILLOW_BLOCK_SET_TYPE =
            BlockSetType.register(
                    new BlockSetType(meowski.MOD_ID + ":willow")
            );

    public static final WoodType WILLOW_WOOD_TYPE =
            WoodType.register(
                    new WoodType(
                            meowski.MOD_ID + ":willow",
                            WILLOW_BLOCK_SET_TYPE
                    )
            );

    public static final DeferredBlock<Block> BLUE_BERRY_BUSH = BLOCKS.register("blue_berry_bush",
        () -> new BlueBerryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)));

    //public static final DeferredBlock<LilyPadsBlock> LILY_PADS =
            //BLOCKS.register("lily_pads",
                   // () -> new LilyPadsBlock(
                            //BlockBehaviour.Properties
                                 //   .ofFullCopy(Blocks.LILY_PAD)
                                 //   .noCollission()
                   // ));
    public static final DeferredBlock<ForestMossBlock> FOREST_MOSS =
            registerBlock(
                    "forest_moss",
                    () -> new ForestMossBlock(
                            BlockBehaviour.Properties
                                    .ofFullCopy(Blocks.MOSS_BLOCK)
                                    .sound(SoundType.MOSS)
                                    .speedFactor(0.65F)
                    )
            );
    public static final DeferredBlock<CatTailsBlock> CATTAILS =
            registerBlock(
                    "cattails",
                    () -> new CatTailsBlock(
                            BlockBehaviour.Properties
                                    .ofFullCopy(Blocks.TALL_SEAGRASS)
                                    .noCollission()
                    )
            );
    public static final DeferredBlock<Block> WILLOW_LOG = registerBlock("willow_log",
        () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));
    public static final DeferredBlock<Block> WILLOW_WOOD = registerBlock("willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)));
    public static final DeferredBlock<Block> STRIPPED_WILLOW_LOG = registerBlock("stripped_willow_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)));
    public static final DeferredBlock<Block> STRIPPED_WILLOW_WOOD = registerBlock("stripped_willow_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)));



    public static final DeferredBlock<SlabBlock> WILLOW_SLAB =
            registerBlock(
                    "willow_slab",
                    () -> new SlabBlock(
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_SLAB
                            )
                    )
            );


    public static final DeferredBlock<FenceBlock> WILLOW_FENCE =
            registerBlock(
                    "willow_fence",
                    () -> new FenceBlock(
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_FENCE
                            )
                    )
            );


    public static final DeferredBlock<FenceGateBlock> WILLOW_FENCE_GATE =
            registerBlock(
                    "willow_fence_gate",
                    () -> new FenceGateBlock(
                            WILLOW_WOOD_TYPE,
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_FENCE_GATE
                            )
                    )
            );


    public static final DeferredBlock<DoorBlock> WILLOW_DOOR =
            registerBlock(
                    "willow_door",
                    () -> new DoorBlock(
                            WILLOW_BLOCK_SET_TYPE,
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_DOOR
                            )
                    )
            );


    public static final DeferredBlock<TrapDoorBlock> WILLOW_TRAPDOOR =
            registerBlock(
                    "willow_trapdoor",
                    () -> new TrapDoorBlock(
                            WILLOW_BLOCK_SET_TYPE,
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_TRAPDOOR
                            )
                    )
            );


    public static final DeferredBlock<PressurePlateBlock> WILLOW_PRESSURE_PLATE =
            registerBlock(
                    "willow_pressure_plate",
                    () -> new PressurePlateBlock(
                            WILLOW_BLOCK_SET_TYPE,
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_PRESSURE_PLATE
                            )
                    )
            );


    public static final DeferredBlock<ButtonBlock> WILLOW_BUTTON =
            registerBlock(
                    "willow_button",
                    () -> new ButtonBlock(
                            WILLOW_BLOCK_SET_TYPE,
                            30,
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_BUTTON
                            )
                    )
            );

    public static final DeferredBlock<Block> WILLOW_PLANKS =
            registerBlock(
                    "willow_planks",
                    () -> new Block(
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_PLANKS
                            )
                    ) {
                        @Override
                        public boolean isFlammable(
                                BlockState state,
                                BlockGetter level,
                                BlockPos pos,
                                Direction direction
                        ) {
                            return true;
                        }

                        @Override
                        public int getFlammability(
                                BlockState state,
                                BlockGetter level,
                                BlockPos pos,
                                Direction direction
                        ) {
                            return 20;
                        }

                        @Override
                        public int getFireSpreadSpeed(
                                BlockState state,
                                BlockGetter level,
                                BlockPos pos,
                                Direction direction
                        ) {
                            return 5;
                        }
                    }
            );

    public static final DeferredBlock<StairBlock> WILLOW_STAIRS =
            registerBlock(
                    "willow_stairs",
                    () -> new StairBlock(
                            WILLOW_PLANKS.get().defaultBlockState(),
                            BlockBehaviour.Properties.ofFullCopy(
                                    Blocks.OAK_STAIRS
                            )
                    ) {}
            );

    public static final DeferredBlock<Block> WILLOW_LEAVES = registerBlock("willow_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> WILLOW_SAPLING = registerBlock("willow_sapling",
            () -> new SaplingBlock(ModTreeGrowers.WILLOW, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));






    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlocksItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlocksItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
