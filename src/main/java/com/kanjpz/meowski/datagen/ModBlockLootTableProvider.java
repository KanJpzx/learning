package com.kanjpz.meowski.datagen;

import com.kanjpz.meowski.block.ModBlocks;
import com.kanjpz.meowski.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.stream.IntStream;

public class ModBlockLootTableProvider extends BlockLootSubProvider {


    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        add(ModBlocks.BLUE_BERRY_BUSH.get(), this::createBerryBushDrops);
       // add(ModBlocks.LILY_PADS.get(), this::createLilyPadsDrops);

        // add(ModBlocks.RUBY_ORE.get(),         **single ore drop**
               // block -> createOreDrop(ModBlocks.RUBY_ORE.get(), ModItems.RUBY.get()));

        // add(ModBlocks.RUBY_ORE.get(),         **multi ore drop**
                // block -> createMultipleOreDrops(ModBlocks.RUBY_ORE.get(), ModItems.RUBY.get(), 2,  5));


        this.dropSelf(ModBlocks.WILLOW_LOG.get());
        this.dropSelf(ModBlocks.WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_WILLOW_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_WILLOW_LOG.get());
        this.dropSelf(ModBlocks.WILLOW_PLANKS.get());
        this.dropSelf(ModBlocks.WILLOW_SAPLING.get());
        this.dropSelf(ModBlocks.WILLOW_STAIRS.get());
        this.dropSelf(ModBlocks.WILLOW_SLAB.get());

        this.dropSelf(ModBlocks.WILLOW_FENCE.get());
        this.dropSelf(ModBlocks.WILLOW_FENCE_GATE.get());

        this.dropSelf(ModBlocks.WILLOW_TRAPDOOR.get());

        this.dropSelf(ModBlocks.WILLOW_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.WILLOW_BUTTON.get());

        this.dropSelf(ModBlocks.FOREST_MOSS.get());

        this.add(ModBlocks.CATTAILS.get(),
                block -> createSinglePropConditionTable(block,
                        DoublePlantBlock.HALF,
                        DoubleBlockHalf.LOWER));

        this.add(ModBlocks.WILLOW_DOOR.get(),
                block -> createSinglePropConditionTable(block,
                        DoorBlock.HALF,
                        DoubleBlockHalf.LOWER));

        this.add(ModBlocks.WILLOW_LEAVES.get(), block ->
                createLeavesDrops(block, ModBlocks.WILLOW_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
    }


    private LootTable.Builder createBerryBushDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.applyExplosionDecay(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(SweetBerryBushBlock.AGE, 3)))
                        .add(LootItem.lootTableItem(ModItems.BLUE_BERRIES.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(SweetBerryBushBlock.AGE, 2)))
                        .add(LootItem.lootTableItem(ModItems.BLUE_BERRIES.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;

    }
}
