package com.kanjpz.meowski.item;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, meowski.MOD_ID);

    public static final Supplier<CreativeModeTab> MEOWSKI_ITEMS_TAB = CREATIVE_MODE_TAB.register("meowski_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BLUE_BERRIES.get()))
                    .title(Component.translatable("creativetab.meowski.meowski_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.BLUE_BERRIES.get());

                        output.accept(ModBlocks.WILLOW_LOG.get());
                        output.accept(ModBlocks.WILLOW_WOOD.get());
                        output.accept(ModBlocks.STRIPPED_WILLOW_LOG.get());
                        output.accept(ModBlocks.STRIPPED_WILLOW_WOOD.get());
                        output.accept(ModBlocks.WILLOW_PLANKS.get());
                        output.accept(ModBlocks.WILLOW_SAPLING.get());
                        output.accept(ModBlocks.WILLOW_LEAVES.get());
                        output.accept(ModBlocks.FOREST_MOSS.get());
                        output.accept(ModBlocks.CATTAILS.get());
                    }).build());


    public static void register(IEventBus EventBus) {
        CREATIVE_MODE_TAB.register(EventBus);
    }
}
