# Minecraft 1.21.1 Tree Placer Guide

For your project file:

```text
src/main/java/com/kanjpz/meowski/worldgen/ModConfiguredFeatures.java
```

This guide is sorted as a reference. Use the tables to choose a placer, then copy the Java example from the matching section.

For tuning leaf amount, width, height, and hanging behavior, see:

```text
TREE_FOLIAGE_TUNING_GUIDE.md
```

## 1. Basic Tree Builder Shape

Every configured tree feature has this order:

```java
register(context, WILLOW_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
        BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
        TRUNK_PLACER_GOES_HERE,
        BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
        FOLIAGE_PLACER_GOES_HERE,
        FEATURE_SIZE_GOES_HERE
).build());
```

The parts mean:

| Part | Meaning |
| --- | --- |
| `BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get())` | Which block is used for logs |
| `TRUNK_PLACER_GOES_HERE` | The class that places logs |
| `BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get())` | Which block is used for leaves |
| `FOLIAGE_PLACER_GOES_HERE` | The class that places leaves |
| `FEATURE_SIZE_GOES_HERE` | Helps Minecraft check if the tree has room |

## 2. Parameter Types

The most common confusion is whether a number should be a raw `int`, an `IntProvider`, or a `float`.

| Type | How To Write It | Meaning |
| --- | --- | --- |
| `int` | `5` | A normal whole number |
| `float` | `0.5F` | A decimal chance, usually from `0.0F` to `1.0F` |
| `IntProvider` | `ConstantInt.of(5)` | Always gives the same number |
| `IntProvider` | `UniformInt.of(2, 4)` | Randomly gives `2`, `3`, or `4` |
| `HolderSet<Block>` | `blockGetter.getOrThrow(BlockTags.SOME_TAG)` | A block tag lookup |

Useful imports:

```java
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
```

Most trunk placers start with:

```java
baseHeight + random(0..heightRandA) + random(0..heightRandB)
```

Example:

```java
new StraightTrunkPlacer(5, 2, 1)
```

That can make a trunk height from `5` to `8`.

## 3. Trunk Placer Quick Table

| Trunk Placer | Best For | Constructor Shape |
| --- | --- | --- |
| `StraightTrunkPlacer` | Simple vertical trees | `(int, int, int)` |
| `ForkingTrunkPlacer` | Acacia-style forked trees | `(int, int, int)` |
| `GiantTrunkPlacer` | 2x2 giant trunks | `(int, int, int)` |
| `MegaJungleTrunkPlacer` | 2x2 jungle trunks with branches | `(int, int, int)` |
| `FancyTrunkPlacer` | Organic big-oak limbs | `(int, int, int)` |
| `DarkOakTrunkPlacer` | 2x2 dark-oak style trees | `(int, int, int)` |
| `BendingTrunkPlacer` | Curved/bent trees | `(int, int, int, int, IntProvider)` |
| `CherryTrunkPlacer` | Curved side branches | `(int, int, int, IntProvider, IntProvider, UniformInt, IntProvider)` |
| `UpwardsBranchingTrunkPlacer` | Mangrove-style upward branches | `(int, int, int, IntProvider, float, IntProvider, HolderSet<Block>)` |

## 4. Foliage Placer Quick Table

| Foliage Placer | Best For | Constructor Shape |
| --- | --- | --- |
| `BlobFoliagePlacer` | Oak-like leaf ball | `(IntProvider, IntProvider, int)` |
| `BushFoliagePlacer` | Short bush leaves | `(IntProvider, IntProvider, int)` |
| `FancyFoliagePlacer` | Round natural clusters | `(IntProvider, IntProvider, int)` |
| `AcaciaFoliagePlacer` | Flat acacia canopy | `(IntProvider, IntProvider)` |
| `DarkOakFoliagePlacer` | Large dark-oak canopy | `(IntProvider, IntProvider)` |
| `MegaJungleFoliagePlacer` | Giant jungle leaf blobs | `(IntProvider, IntProvider, int)` |
| `MegaPineFoliagePlacer` | Tall conifer crown | `(IntProvider, IntProvider, IntProvider)` |
| `PineFoliagePlacer` | Small cone/pine crown | `(IntProvider, IntProvider, IntProvider)` |
| `SpruceFoliagePlacer` | Spruce cone crown | `(IntProvider, IntProvider, IntProvider)` |
| `RandomSpreadFoliagePlacer` | Loose random leaves | `(IntProvider, IntProvider, IntProvider, int)` |
| `CherryFoliagePlacer` | Hanging cherry/willow leaves | `(IntProvider, IntProvider, IntProvider, float, float, float, float)` |

## 5. Branching Code Guide

Branches mostly come from the **trunk placer**, not the foliage placer.

The trunk placer creates one or more `FoliageAttachment` points. Then the foliage placer puts a leaf clump on each attachment point.

```text
more branch endpoints = more leaf clumps
larger foliage radius = bigger leaf clumps
high branch chance + big leaf clumps = way too many leaves
```

### Branching Control Table

| Trunk Placer | Can Branch? | Can You Control Branch Amount? | Notes |
| --- | --- | --- | --- |
| `StraightTrunkPlacer` | No | No | One vertical trunk only |
| `ForkingTrunkPlacer` | Yes | Not directly | Random acacia-like fork near the top |
| `GiantTrunkPlacer` | No | No | 2x2 trunk only |
| `MegaJungleTrunkPlacer` | Yes | Not directly | Built-in side branches on big jungle trunks |
| `FancyTrunkPlacer` | Yes | Not directly | Organic oak limbs, internally random |
| `DarkOakTrunkPlacer` | Slightly | Not directly | 2x2 trunk, bend, and small top side pieces |
| `BendingTrunkPlacer` | Kind of | A little | Makes a bending trunk, not true many branches |
| `CherryTrunkPlacer` | Yes | Yes, 1 to 3 branches | Best simple controlled branch placer |
| `UpwardsBranchingTrunkPlacer` | Yes | Yes | Best vanilla placer for many branch endpoints |

### No-Branch Tree

Use when you want a clean beginner tree:

```java
new StraightTrunkPlacer(5, 2, 1)
new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3)
new TwoLayersFeatureSize(1, 0, 1)
```

### Slight Branching

Use `ForkingTrunkPlacer` when you want a simple acacia-like split:

```java
new ForkingTrunkPlacer(5, 2, 2)
new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0))
new TwoLayersFeatureSize(1, 0, 2)
```

This has branches, but you cannot write `branchCount` for it. Minecraft decides internally.

### Organic Oak Branching

Use `FancyTrunkPlacer` when you want old-oak style limbs:

```java
new FancyTrunkPlacer(5, 6, 0)
new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(3), 4)
new TwoLayersFeatureSize(0, 0, 0)
```

Good for round branch clumps. Not good for controlled willow branches.

### Controlled Cherry Branching

Use `CherryTrunkPlacer` when you want 1 to 3 visible side branches:

```java
new CherryTrunkPlacer(
        8,
        2,
        2,
        ConstantInt.of(3),
        UniformInt.of(3, 5),
        UniformInt.of(-5, -3),
        UniformInt.of(-1, 1)
)
new CherryFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(5),
        0.20F, 0.45F, 0.45F, 0.35F)
new TwoLayersFeatureSize(1, 0, 2)
```

The important branch values are:

| Value | Meaning |
| --- | --- |
| `ConstantInt.of(3)` | Try to make 3 branches |
| `UniformInt.of(3, 5)` | Branches go 3 to 5 blocks sideways |
| `UniformInt.of(-5, -3)` | Branches start 3 to 5 blocks below the top |
| `UniformInt.of(-1, 1)` | Branches end around the top |

Do not use `ConstantInt.of(8)` for branch count. Cherry branch count is meant to be `1` to `3`.

### Many Willow/Mangrove-Style Branches

Use `UpwardsBranchingTrunkPlacer` when you want lots of branch endpoints:

```java
var blockGetter = context.lookup(Registries.BLOCK);

new UpwardsBranchingTrunkPlacer(
        8,
        2,
        2,
        UniformInt.of(2, 3),
        0.45F,
        UniformInt.of(1, 3),
        blockGetter.getOrThrow(BlockTags.LEAVES)
)
new CherryFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(5),
        0.20F, 0.45F, 0.45F, 0.35F)
new TwoLayersFeatureSize(1, 0, 2)
```

Branch controls:

| Value | Meaning | Bigger Value Does This |
| --- | --- | --- |
| `UniformInt.of(2, 3)` | Branch step count | Longer/more branch endpoints |
| `0.45F` | Chance to start a branch from each trunk log | More branches |
| `UniformInt.of(1, 3)` | Extra branch length | Longer branches |

Fewer leaves and fewer branches:

```java
new UpwardsBranchingTrunkPlacer(
        8,
        2,
        2,
        UniformInt.of(1, 2),
        0.25F,
        UniformInt.of(1, 2),
        blockGetter.getOrThrow(BlockTags.LEAVES)
)
new CherryFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), ConstantInt.of(4),
        0.30F, 0.55F, 0.25F, 0.15F)
```

More branches and more leaves:

```java
new UpwardsBranchingTrunkPlacer(
        8,
        2,
        2,
        UniformInt.of(3, 5),
        0.70F,
        UniformInt.of(2, 4),
        blockGetter.getOrThrow(BlockTags.LEAVES)
)
new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(6),
        0.15F, 0.35F, 0.75F, 0.65F)
```

### Big Jungle Branching

Use this for large jungle-style side branches:

```java
new MegaJungleTrunkPlacer(10, 2, 12)
new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2)
new TwoLayersFeatureSize(1, 1, 2)
```

You can make the tree taller with the first three numbers, but the side branch pattern is built into the placer.

### Branching Presets

Use these as the trunk placer, foliage placer, and feature size part inside `TreeConfigurationBuilder`.

Simple no-branch tree:

```java
new StraightTrunkPlacer(5, 2, 1),
new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
new TwoLayersFeatureSize(1, 0, 1)
```

Acacia fork:

```java
new ForkingTrunkPlacer(5, 2, 2),
new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
new TwoLayersFeatureSize(1, 0, 2)
```

Fancy oak branches:

```java
new FancyTrunkPlacer(5, 6, 0),
new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(3), 4),
new TwoLayersFeatureSize(0, 0, 0)
```

Cherry branches:

```java
new CherryTrunkPlacer(
        8,
        2,
        2,
        ConstantInt.of(3),
        UniformInt.of(3, 5),
        UniformInt.of(-5, -3),
        UniformInt.of(-1, 1)
),
new CherryFoliagePlacer(
        ConstantInt.of(3),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.20F,
        0.45F,
        0.45F,
        0.35F
),
new TwoLayersFeatureSize(1, 0, 2)
```

Willow branch endpoints:

```java
new UpwardsBranchingTrunkPlacer(
        8,
        2,
        2,
        UniformInt.of(2, 3),
        0.45F,
        UniformInt.of(1, 3),
        blockGetter.getOrThrow(BlockTags.LEAVES)
),
new CherryFoliagePlacer(
        ConstantInt.of(3),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.20F,
        0.45F,
        0.45F,
        0.35F
),
new TwoLayersFeatureSize(1, 0, 2)
```

Giant jungle branches:

```java
new MegaJungleTrunkPlacer(10, 2, 12),
new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
new TwoLayersFeatureSize(1, 1, 2)
```

## 6. Trunk Placers

### StraightTrunkPlacer

```java
new StraightTrunkPlacer(5, 2, 1)
```

Makes one simple vertical trunk. Use this first when testing a custom tree.

### ForkingTrunkPlacer

```java
new ForkingTrunkPlacer(5, 2, 2)
```

Starts mostly vertical, then bends sideways near the top. Can make a second fork. Vanilla acacia uses this style.

### GiantTrunkPlacer

```java
new GiantTrunkPlacer(13, 2, 14)
```

Makes a 2x2 trunk. Good for huge trees.

### MegaJungleTrunkPlacer

```java
new MegaJungleTrunkPlacer(10, 2, 19)
```

Makes a 2x2 trunk and adds side branches from the upper half.

### FancyTrunkPlacer

```java
new FancyTrunkPlacer(3, 11, 0)
```

Creates an organic old-oak shape with angled limbs. Usually paired with `FancyFoliagePlacer`.

### DarkOakTrunkPlacer

```java
new DarkOakTrunkPlacer(6, 2, 1)
```

Makes a 2x2 dark-oak style trunk that may bend near the top. Best with `DarkOakFoliagePlacer`.

### BendingTrunkPlacer

```java
new BendingTrunkPlacer(4, 2, 0, 3, UniformInt.of(1, 2))
```

Parameters:

| Parameter | Meaning |
| --- | --- |
| `4, 2, 0` | Normal trunk height settings |
| `3` | Leaves can start after this many trunk blocks |
| `UniformInt.of(1, 2)` | How far the trunk bends sideways |

Good for azalea-like or willow-like curved trunks.

### CherryTrunkPlacer

```java
new CherryTrunkPlacer(
        7,
        1,
        0,
        ConstantInt.of(2),
        UniformInt.of(2, 4),
        UniformInt.of(-4, -3),
        UniformInt.of(-1, 0)
)
```

Parameters:

| Parameter | Meaning |
| --- | --- |
| `7, 1, 0` | Normal trunk height settings |
| `ConstantInt.of(2)` | Number of branches |
| `UniformInt.of(2, 4)` | How far branches go sideways |
| `UniformInt.of(-4, -3)` | Where branches start, measured from the top |
| `UniformInt.of(-1, 0)` | Where branches end, measured from the top |

Important: `branchStartOffsetFromTop` should use `UniformInt`, not `ConstantInt`, because cherry needs at least two possible start heights.

### UpwardsBranchingTrunkPlacer

This placer needs a block lookup before the tree builder:

```java
var blockGetter = context.lookup(Registries.BLOCK);
```

Then use:

```java
new UpwardsBranchingTrunkPlacer(
        2,
        6,
        4,
        ConstantInt.of(1),
        0.5F,
        ConstantInt.of(1),
        blockGetter.getOrThrow(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
)
```

Parameters:

| Parameter | Meaning |
| --- | --- |
| `2, 6, 4` | Normal trunk height settings |
| `ConstantInt.of(1)` | Extra branch steps |
| `0.5F` | 50 percent chance to create a branch from each log |
| `ConstantInt.of(1)` | Extra branch length |
| `blockGetter.getOrThrow(...)` | Blocks this trunk can grow through |

This is wrong:

```java
new UpwardsBranchingTrunkPlacer(2, 6, 4, 1, 0.5F, 1, 4)
```

Why it is wrong:

| Given | Problem |
| --- | --- |
| `1` | Should be `ConstantInt.of(1)` |
| second `1` | Should be `ConstantInt.of(1)` |
| `4` | Should be a `HolderSet<Block>` from a block tag |

## 7. Foliage Placers

### BlobFoliagePlacer

```java
new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3)
```

Makes a rounded oak-like blob.

| Parameter | Meaning |
| --- | --- |
| `ConstantInt.of(2)` | Leaf radius |
| `ConstantInt.of(0)` | Vertical offset |
| `3` | Leaf height |

### BushFoliagePlacer

```java
new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2)
```

Makes a low, full bush shape.

### FancyFoliagePlacer

```java
new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4)
```

Makes rounder natural leaf clusters. Usually used with `FancyTrunkPlacer`.

### AcaciaFoliagePlacer

```java
new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0))
```

Makes a flat-ish acacia canopy.

### DarkOakFoliagePlacer

```java
new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0))
```

Makes a large canopy for a 2x2 dark-oak trunk.

### MegaJungleFoliagePlacer

```java
new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2)
```

Makes leaf blobs for giant jungle trunks and branches.

### MegaPineFoliagePlacer

```java
new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17))
```

Makes a tall conifer crown that widens lower down.

### PineFoliagePlacer

```java
new PineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(3, 4))
```

Makes a smaller pine cone shape.

### SpruceFoliagePlacer

```java
new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2))
```

The third value is named `trunkHeight`, but it really controls how much trunk is kept clear before the leaf crown starts.

### RandomSpreadFoliagePlacer

```java
new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70)
```

Parameters:

| Parameter | Meaning |
| --- | --- |
| `ConstantInt.of(3)` | Maximum sideways spread |
| `ConstantInt.of(0)` | Vertical offset |
| `ConstantInt.of(2)` | Maximum vertical spread |
| `70` | How many leaf placement attempts |

Makes a loose random cloud of leaves. Vanilla azalea and mangrove use this.

### CherryFoliagePlacer

```java
new CherryFoliagePlacer(
        ConstantInt.of(4),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.25F,
        0.5F,
        0.16666667F,
        0.33333334F
)
```

Parameters:

| Parameter | Meaning |
| --- | --- |
| `ConstantInt.of(4)` | Canopy radius |
| `ConstantInt.of(0)` | Vertical offset |
| `ConstantInt.of(5)` | Canopy height |
| `0.25F` | Chance to skip wide bottom edge leaves |
| `0.5F` | Chance to skip corner leaves |
| `0.16666667F` | Chance to place hanging leaves |
| `0.33333334F` | Chance hanging leaves extend downward |

For willow-style leaves, increase the last two values:

```java
new CherryFoliagePlacer(
        ConstantInt.of(3),
        ConstantInt.of(0),
        ConstantInt.of(6),
        0.15F,
        0.35F,
        0.55F,
        0.45F
)
```

## 8. Paste-Ready Examples

### Simple Beginner Tree

```java
new StraightTrunkPlacer(5, 2, 1)
new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3)
new TwoLayersFeatureSize(1, 0, 1)
```

### Acacia-Like Tree

```java
new ForkingTrunkPlacer(5, 2, 2)
new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0))
new TwoLayersFeatureSize(1, 0, 2)
```

### Cherry-Like Tree

```java
new CherryTrunkPlacer(
        7,
        1,
        0,
        ConstantInt.of(2),
        UniformInt.of(2, 4),
        UniformInt.of(-4, -3),
        UniformInt.of(-1, 0)
)
new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5),
        0.25F, 0.5F, 0.16666667F, 0.33333334F)
new TwoLayersFeatureSize(1, 0, 2)
```

### Willow-Like Beginner Tree

```java
new BendingTrunkPlacer(5, 2, 1, 3, UniformInt.of(1, 2))
new CherryFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(6),
        0.15F, 0.35F, 0.55F, 0.45F)
new TwoLayersFeatureSize(1, 0, 2)
```

## 9. Value Tuning

| Problem | Change |
| --- | --- |
| Tree is too short | Increase `baseHeight` |
| Tree is too predictable | Increase `heightRandA` or `heightRandB` |
| Leaves are too small | Increase foliage `radius` |
| Leaves are too high or low | Change foliage `offset` |
| Cherry/willow leaves look square | Increase `cornerHoleChance` |
| Willow leaves do not hang enough | Increase `hangingLeavesChance` and `hangingLeavesExtensionChance` |
| Tree often fails to generate | Use a simpler trunk first, or adjust `FeatureSize` |
