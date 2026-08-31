package com.kanjpz.meowski.item;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.meowski;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(meowski.MOD_ID);

    public static final DeferredItem<Item> WILLOW_SAPLING = ITEMS.register("willow_sapling",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SWAMP_SAPLING = ITEMS.register("swamp_sapling",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLUE_BERRIES = ITEMS.register("blue_berries",
            () -> new ItemNameBlockItem(ModBlocks.BLUE_BERRY_BUSH.get(), new Item.Properties().food(ModFoodProperties.BLUE_BERRIES)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
