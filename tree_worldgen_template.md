# Tree Worldgen Template — Trunk & Foliage Placers (NeoForge 1.21.1)

A tree in a `ConfiguredFeature` is built from **two independent pieces**:

```
TrunkPlacer   → shapes the trunk/branches
FoliagePlacer → shapes the leaf canopy
```

You mix and match ANY trunk with ANY foliage. Copy a block below, paste it into your
`TreeConfigurationBuilder`, and tweak the numbers. Every parameter has a comment
explaining what it visually does.

⚠️ Note: constructor parameter order can shift slightly between versions. If a copied
block gives a compile error, ctrl+click (or cmd+click) the class name in your IDE to
jump to the actual source and check the constructor — that's the fastest way to fix it
without needing to ask anyone.

---

## How a tree is assembled (reminder)

```java
new TreeConfiguration.TreeConfigurationBuilder(
        BlockStateProvider.simple(YOUR_LOG.get()),   // trunk block
        /* TRUNK PLACER GOES HERE */,
        BlockStateProvider.simple(YOUR_LEAVES.get()), // leaf block
        /* FOLIAGE PLACER GOES HERE */,
        new TwoLayersFeatureSize(0, 0, 0))             // space the tree needs, leave as-is for now
        .ignoreVines()
        .build();
```

---

## TRUNK PLACERS

### StraightTrunkPlacer — plain vertical pole
Good for: simple utility trees, palm-like trunks.
```java
new StraightTrunkPlacer(
        5,  // baseHeight   — minimum trunk height
        2,  // heightRandA  — adds 0-to-this extra height (roll #1)
        1); // heightRandB  — adds 0-to-this extra height (roll #2, stacked with A)
```

### ForkingTrunkPlacer — vanilla oak style
Trunk splits into 1-2 small branches near the top.
```java
new ForkingTrunkPlacer(
        4,  // baseHeight
        2,  // heightRandA
        0); // heightRandB
```

### FancyTrunkPlacer — vanilla "big/fancy oak" style
Branches spread out more randomly and widely — what you're already using for willow/oak.
```java
new FancyTrunkPlacer(
        9,  // baseHeight
        5,  // heightRandA
        2); // heightRandB
```

### GiantTrunkPlacer — thick 2x2 trunk (no branching)
Used for jungle giants / basic 2x2 trunks.
```java
new GiantTrunkPlacer(
        10, // baseHeight
        2,  // heightRandA
        2); // heightRandB
```

### MegaJungleTrunkPlacer — 2x2 trunk WITH branches
Same footprint as GiantTrunkPlacer but grows side branches with leaves.
```java
new MegaJungleTrunkPlacer(
        10, // baseHeight
        2,  // heightRandA
        19);// heightRandB
```

### DarkOakTrunkPlacer — vanilla dark oak style
2x2 trunk, very short, wide flat-topped canopy shape.
```java
new DarkOakTrunkPlacer(
        6,  // baseHeight
        2,  // heightRandA
        1); // heightRandB
```

### BendingTrunkPlacer — mangrove-style, bends sideways as it grows
```java
new BendingTrunkPlacer(
        4,                          // baseHeight
        2,                          // heightRandA
        0,                          // heightRandB
        3,                          // minHeightForLeaves — trunk must be at least this tall before leaves can start
        UniformInt.of(1, 3));       // bendLength — how many logs before each random bend
```

### CherryTrunkPlacer — vanilla cherry blossom style
Branches spread from partway up the trunk, offset from the top. More advanced —
double-check the constructor in your IDE since it has several `IntProvider` fields.
```java
new CherryTrunkPlacer(
        4,                      // baseHeight
        2,                      // heightRandA
        1,                      // heightRandB
        UniformInt.of(2, 4),    // branchStartOffsetFromTop
        UniformInt.of(1, 3),    // branchEndOffsetFromTop
        UniformInt.of(2, 4),    // branchLength
        UniformInt.of(1, 2));   // extra branch step range (check IDE — exact name varies)
```

---

## FOLIAGE PLACERS

### BlobFoliagePlacer — round canopy (vanilla oak/birch)
The classic ball-of-leaves look.
```java
new BlobFoliagePlacer(
        ConstantInt.of(2), // radius   — how wide the leaf ball is
        ConstantInt.of(0), // offset   — how far below the top the leaves start
        3);                // height   — how many layers tall the canopy is
```

### SpruceFoliagePlacer — conical canopy (vanilla spruce)
```java
new SpruceFoliagePlacer(
        ConstantInt.of(2),         // radius
        ConstantInt.of(0),         // offset
        UniformInt.of(4, 7));      // trunkHeight — taller = longer cone
```

### PineFoliagePlacer — layered rings with visible gaps (vanilla pine-style spruce variant)
```java
new PineFoliagePlacer(
        ConstantInt.of(2),         // radius
        ConstantInt.of(1),         // offset
        UniformInt.of(3, 7));      // height
```

### AcaciaFoliagePlacer — flat wide canopy (vanilla acacia)
Note: only takes 2 params — it's a single flat layer by design.
```java
new AcaciaFoliagePlacer(
        ConstantInt.of(3), // radius — bigger = wider flat top
        ConstantInt.of(0));// offset
```

### BushFoliagePlacer — small dense round canopy (vanilla azalea tree)
Good starting point for shrub-sized trees.
```java
new BushFoliagePlacer(
        ConstantInt.of(2), // radius
        ConstantInt.of(0), // offset
        2);                // height
```

### DarkOakFoliagePlacer — big blocky canopy (vanilla dark oak)
```java
new DarkOakFoliagePlacer(
        ConstantInt.of(0), // radius (dark oak's shape is mostly hardcoded, radius has small effect)
        ConstantInt.of(0));// offset
```

### JungleFoliagePlacer — large wide canopy (vanilla jungle tree)
```java
new JungleFoliagePlacer(
        ConstantInt.of(2), // radius
        ConstantInt.of(0), // offset
        3);                // height
```

### RandomSpreadFoliagePlacer — scattered/wild leaves (vanilla azalea/mangrove roots style)
Leaves aren't placed in a neat shape — good for wild/overgrown looks.
```java
new RandomSpreadFoliagePlacer(
        ConstantInt.of(3),  // radius
        ConstantInt.of(0),  // offset
        ConstantInt.of(4),  // foliageHeight
        50);                // leafPlacementAttempts — higher = denser leaf scatter
```

### CherryFoliagePlacer — layered canopy with hanging leaves (what you're already using)
```java
new CherryFoliagePlacer(
        ConstantInt.of(4), // radius
        ConstantInt.of(1), // offset
        ConstantInt.of(4), // foliage height (layers)

        0.75F, // holes in the wide bottom layer — HIGHER = more gaps/see-through
        0.65F, // corner hole chance — HIGHER = rounder-looking canopy
        0.3F,  // hanging leaves chance — HIGHER = more single dangling leaves
        0.6F); // chance a hanging leaf extends an extra block down
```

---

## Quick pairing cheat-sheet

| Look you want              | Trunk                  | Foliage                  |
|-----------------------------|-------------------------|----------------------------|
| Classic round tree           | ForkingTrunkPlacer      | BlobFoliagePlacer          |
| Big wild fantasy tree        | FancyTrunkPlacer        | CherryFoliagePlacer        |
| Conifer / evergreen          | StraightTrunkPlacer     | SpruceFoliagePlacer        |
| Thick jungle giant           | MegaJungleTrunkPlacer   | JungleFoliagePlacer        |
| Flat-top savanna tree        | ForkingTrunkPlacer      | AcaciaFoliagePlacer        |
| Overgrown/swampy willow-ish  | BendingTrunkPlacer      | RandomSpreadFoliagePlacer  |
| Small bush/shrub             | StraightTrunkPlacer     | BushFoliagePlacer          |

---

## Workflow for making a new tree

1. Pick a trunk from above, paste it into your `TreeConfigurationBuilder`.
2. Pick a foliage from above, paste it in too.
3. Run the game, place a sapling (or use `/place feature <yourfeature>` in creative to test instantly without waiting for growth).
4. Tweak ONE number at a time — height, radius, chance values — and re-test. This is
   the fastest way to learn what each parameter actually does, faster than reading docs.
5. Once happy, that's your new `ModConfiguredFeatures` entry — same as your existing willow/oak/birch pattern.
