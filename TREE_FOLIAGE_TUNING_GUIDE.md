# Tree Foliage Tuning Guide

For Minecraft `1.21.1` / NeoForge tree generation.

Main project file:

```text
src/main/java/com/kanjpz/meowski/worldgen/ModConfiguredFeatures.java
```

This file explains **why tree shape changes work**, **why sometimes nothing changes**, and **how to tune every vanilla foliage placer**.

## 1. Why Your Changes Sometimes Do Nothing

Minecraft does not always read your Java file directly when you test the tree.

Your flow is:

```text
ModConfiguredFeatures.java
        |
        | runData
        v
src/generated/resources/data/meowskis/worldgen/configured_feature/willow.json
        |
        | processResources
        v
build/resources/main/data/meowskis/worldgen/configured_feature/willow.json
        |
        | run client
        v
Minecraft loads the tree config
```

So if you change Java but do not regenerate/copy the JSON, the game can still load old values.

After changing tree numbers, run:

```powershell
.\gradlew.bat runData processResources
```

Then restart the client and grow a **new** sapling.

Important:

| Case | Result |
| --- | --- |
| You change Java only | Game may still use old JSON |
| You run `runData` only | `src/generated` updates, but `build/resources/main` can still be old |
| You run `processResources` | Runtime resources get copied |
| You keep the client open | Old loaded registry data can stay in memory |
| You look at an old tree | Old placed blocks never update |
| You grow only one sapling | Random tree generation can hide the difference |

## 2. Why Shape Does Not Fully Change

Each foliage placer is a hard-coded algorithm.

That means:

```java
new CherryFoliagePlacer(...)
```

will always make a cherry-style canopy. You can make it wider, shorter, more holey, or more hanging, but it will still use the cherry algorithm.

Changing numbers is like changing settings on the same machine. It does not turn that machine into a different machine.

To get a totally different shape, choose a different placer or write a custom placer.

## 3. The Big Foliage Controls

Most foliage placers use some version of these controls:

| Control | Makes Wider | Makes Smaller | Makes Taller | Makes Lower | Makes More Leaves |
| --- | --- | --- | --- | --- | --- |
| `radius` | Increase | Decrease | No | No | Usually yes |
| `offset` | No | No | No | Increase/decrease vertical position | No |
| `height` | No | Decrease | Increase | Sometimes | Usually yes |
| `crownHeight` | No | Decrease | Increase | No | Usually yes |
| `foliageHeight` | No | Decrease | Increase random cloud | No | Usually yes |
| `leafPlacementAttempts` | No | Decrease | No | No | Yes |
| `cornerHoleChance` | No | Increase | No | No | Decrease leaves |
| `wideBottomLayerHoleChance` | No | Increase | No | No | Decrease leaves |
| `hangingLeavesChance` | No | Decrease | No | More hanging leaves | Increase leaves |
| `hangingLeavesExtensionChance` | No | Decrease | No | Longer hanging leaves | Increase leaves |

## 4. Current Willow Config

Your current willow foliage is:

```java
new CherryFoliagePlacer(
        ConstantInt.of(3),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.20F,
        0.45F,
        0.45F,
        0.35F)
```

This means:

| Value | Meaning |
| --- | --- |
| `ConstantInt.of(3)` | Leaf clump radius |
| `ConstantInt.of(0)` | No vertical offset |
| `ConstantInt.of(5)` | Leaf clump height |
| `0.20F` | Some holes on wide bottom edges |
| `0.45F` | Many corner holes |
| `0.45F` | Medium chance for hanging leaves |
| `0.35F` | Medium-low chance for extra hanging extension |

If leaves are still too much, use this smaller version:

```java
new CherryFoliagePlacer(
        ConstantInt.of(2),
        ConstantInt.of(0),
        ConstantInt.of(4),
        0.30F,
        0.55F,
        0.25F,
        0.15F)
```

## 5. Branches Affect Leaves Too

This is the part that feels weird:

The foliage placer does not decide how many separate leaf clumps exist.

The trunk placer creates **foliage attachment points**. Then the foliage placer puts leaves on each attachment.

More branch attachments means more leaf clumps.

Your current trunk is:

```java
new UpwardsBranchingTrunkPlacer(
        8,
        2,
        2,
        UniformInt.of(2, 3),
        0.45F,
        UniformInt.of(1, 3),
        blockGetter.getOrThrow(BlockTags.LEAVES)
)
```

The branch controls are:

| Value | Meaning |
| --- | --- |
| `UniformInt.of(2, 3)` | Branch step count |
| `0.45F` | Chance to start a branch from each trunk log |
| `UniformInt.of(1, 3)` | Branch length |

If you want fewer leaf clumps:

```java
UniformInt.of(1, 2),
0.25F,
UniformInt.of(1, 2)
```

If you want more branch clumps:

```java
UniformInt.of(3, 5),
0.70F,
UniformInt.of(2, 4)
```

## 6. IntProvider Cheat Sheet

Use `ConstantInt` when you want predictable testing:

```java
ConstantInt.of(3)
```

Use `UniformInt` when you want random variation:

```java
UniformInt.of(2, 5)
```

For testing, prefer `ConstantInt`. After you like the shape, switch some values to `UniformInt`.

Example:

```java
// Predictable while testing
ConstantInt.of(3)

// More natural after testing
UniformInt.of(2, 4)
```

## 7. Foliage Placer Tuning Table

| Foliage Placer | Wider | Smaller | Taller | Lower | More Open/Holey | More Hanging |
| --- | --- | --- | --- | --- | --- | --- |
| `BlobFoliagePlacer` | Increase `radius` | Decrease `radius` or `height` | Increase `height` | Change `offset` | Not much control | No |
| `BushFoliagePlacer` | Increase `radius` | Decrease `radius` or `height` | Increase `height` | Change `offset` | Not much control | No |
| `FancyFoliagePlacer` | Increase `radius` | Decrease `radius` or `height` | Increase `height` | Change `offset` | Not much control | No |
| `AcaciaFoliagePlacer` | Increase `radius` | Decrease `radius` | Not really | Change `offset` | Built-in flat gaps | No |
| `DarkOakFoliagePlacer` | Increase `radius` | Decrease `radius` | Mostly fixed | Change `offset` | Some built-in shape gaps | No |
| `MegaJungleFoliagePlacer` | Increase `radius` | Decrease `radius` or `height` | Increase `height` | Change `offset` | Not much control | No |
| `MegaPineFoliagePlacer` | Increase `radius` a little | Decrease `crownHeight` | Increase `crownHeight` | Change `offset` | Not much control | No |
| `PineFoliagePlacer` | Increase `radius` | Decrease `radius` or `height` | Increase `height` | Change `offset` | Not much control | No |
| `SpruceFoliagePlacer` | Increase `radius` | Increase third value or decrease `radius` | Decrease third value | Change `offset` | Not much control | No |
| `RandomSpreadFoliagePlacer` | Increase `radius` | Decrease attempts or radius | Increase `foliageHeight` | Change `offset` | Lower attempts | No |
| `CherryFoliagePlacer` | Increase `radius` | Increase hole chances or decrease size | Increase `height` | Change `offset` | Increase hole chances | Yes |

## 8. BlobFoliagePlacer

Code:

```java
new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3)
```

Parameter map:

| Parameter | What It Controls |
| --- | --- |
| `radius` | Sideways size |
| `offset` | Vertical shift |
| `height` | Number of leaf layers downward |

Tuning:

```java
// Wider and fatter
new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 4)

// Smaller
new BlobFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0), 2)

// Higher leaves
new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 3)

// Lower leaves
new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(-1), 3)
```

What it cannot do:

Blob foliage cannot make long hanging leaves. It is a round oak-like clump.

## 9. BushFoliagePlacer

Code:

```java
new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2)
```

Tuning:

```java
// Wider bush
new BushFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 2)

// Thicker/taller bush
new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 3)

// Smaller bush
new BushFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), 1)
```

What it cannot do:

It cannot make branches or hanging strands by itself.

## 10. FancyFoliagePlacer

Code:

```java
new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4)
```

Tuning:

```java
// Bigger fancy clumps
new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(4), 5)

// Smaller fancy clumps
new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(3), 3)

// Lower fancy clumps
new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2), 4)
```

Important:

Fancy foliage is most useful with `FancyTrunkPlacer`, because that trunk creates multiple limb endpoints.

## 11. AcaciaFoliagePlacer

Code:

```java
new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0))
```

Tuning:

```java
// Wider flat canopy
new AcaciaFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0))

// Smaller flat canopy
new AcaciaFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0))

// Raise canopy
new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1))
```

What it does:

This placer is flat and sparse by design. It is not good for willow hanging leaves.

## 12. DarkOakFoliagePlacer

Code:

```java
new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0))
```

Tuning:

```java
// Bigger canopy
new DarkOakFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0))

// Shift canopy up
new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(1))

// Shift canopy down
new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(-1))
```

Important:

This looks best with `DarkOakTrunkPlacer`, because that trunk marks itself as a double trunk.

## 13. MegaJungleFoliagePlacer

Code:

```java
new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2)
```

Tuning:

```java
// Larger branch leaf blobs
new MegaJungleFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3)

// Smaller branch leaf blobs
new MegaJungleFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0), 1)
```

Good for:

Big jungle branch endpoints, not willow curtains.

## 14. MegaPineFoliagePlacer

Code:

```java
new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17))
```

Parameter map:

| Parameter | What It Controls |
| --- | --- |
| `radius` | Extra sideways size |
| `offset` | Vertical shift |
| `crownHeight` | Height of the pine crown |

Tuning:

```java
// Taller pine crown
new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(16, 20))

// Shorter pine crown
new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(6, 10))

// Slightly wider crown
new MegaPineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0), UniformInt.of(13, 17))
```

Good for:

Tall spruce or pine trees.

## 15. PineFoliagePlacer

Code:

```java
new PineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(3, 4))
```

Tuning:

```java
// Wider cone
new PineFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), UniformInt.of(3, 4))

// Taller cone
new PineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(5, 7))

// Smaller cone
new PineFoliagePlacer(ConstantInt.of(1), ConstantInt.of(0), UniformInt.of(2, 3))
```

Good for:

Small pine/spruce-like trees.

## 16. SpruceFoliagePlacer

Code:

```java
new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2))
```

Important:

The third parameter is named `trunkHeight`. Higher values usually mean the leaf crown starts later and becomes smaller/shorter.

Tuning:

```java
// Wider spruce
new SpruceFoliagePlacer(UniformInt.of(3, 4), UniformInt.of(0, 2), UniformInt.of(1, 2))

// Smaller/tighter spruce
new SpruceFoliagePlacer(UniformInt.of(1, 2), UniformInt.of(0, 1), UniformInt.of(3, 4))

// More leafy spruce
new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), ConstantInt.of(1))
```

Good for:

Cone-shaped evergreen trees.

## 17. RandomSpreadFoliagePlacer

Code:

```java
new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70)
```

Parameter map:

| Parameter | What It Controls |
| --- | --- |
| `radius` | Random sideways spread |
| `offset` | Vertical shift |
| `foliageHeight` | Random vertical spread |
| `leafPlacementAttempts` | How many leaves it tries to place |

Tuning:

```java
// More leaves
new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 90)

// Much fewer leaves
new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 30)

// Wider random cloud
new RandomSpreadFoliagePlacer(ConstantInt.of(5), ConstantInt.of(0), ConstantInt.of(2), 70)

// Taller random cloud
new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(5), 70)
```

Important:

This placer is great when you want loose random leaves, but it does not make long hanging strands.

## 18. CherryFoliagePlacer

Code:

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

Parameter map:

| Parameter | What It Controls |
| --- | --- |
| `radius` | Sideways canopy size |
| `offset` | Vertical shift from branch endpoint |
| `height` | Canopy height |
| `wideBottomLayerHoleChance` | Removes some bottom edge leaves |
| `cornerHoleChance` | Removes corner leaves |
| `hangingLeavesChance` | Adds hanging leaves below edges |
| `hangingLeavesExtensionChance` | Adds a second lower hanging leaf |

Make it wider:

```java
new CherryFoliagePlacer(
        ConstantInt.of(5),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.20F,
        0.40F,
        0.45F,
        0.35F
)
```

Make it smaller:

```java
new CherryFoliagePlacer(
        ConstantInt.of(2),
        ConstantInt.of(0),
        ConstantInt.of(4),
        0.30F,
        0.55F,
        0.25F,
        0.15F
)
```

Make it more hanging:

```java
new CherryFoliagePlacer(
        ConstantInt.of(3),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.15F,
        0.35F,
        0.75F,
        0.65F
)
```

Make it less blocky:

```java
new CherryFoliagePlacer(
        ConstantInt.of(3),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.35F,
        0.65F,
        0.45F,
        0.35F
)
```

Important limit:

`CherryFoliagePlacer` can only hang leaves a short distance below the leaf clump. It cannot make huge willow curtains down to the ground by itself.

For true long willow curtains, you need one of these later:

| Option | What It Would Do |
| --- | --- |
| Custom foliage placer | Full control over leaf shape |
| Custom tree decorator | Add extra hanging leaves after the tree generates |
| Manual JSON experiments | Faster tuning, still limited to vanilla algorithms |

## 19. Why Leaf Amount Gets Too Much

Leaf amount is not controlled by one number.

It is roughly:

```text
leaf amount = leaf clump size * number of foliage attachment points
```

So leaves get huge when both are high:

```java
// Big clumps
ConstantInt.of(5) // radius
ConstantInt.of(8) // height

// Many branch endpoints
0.75F // branch chance
UniformInt.of(3, 5) // branch steps
```

To reduce leaves, reduce one or both sides:

```java
// Smaller clumps
ConstantInt.of(2) // radius
ConstantInt.of(4) // height

// Fewer branch endpoints
0.25F // branch chance
UniformInt.of(1, 2) // branch steps
```

## 20. Willow Presets

### Small Willow

Trunk:

```java
new UpwardsBranchingTrunkPlacer(
        7,
        1,
        2,
        UniformInt.of(1, 2),
        0.30F,
        UniformInt.of(1, 2),
        blockGetter.getOrThrow(BlockTags.LEAVES)
)
```

Leaves:

```java
new CherryFoliagePlacer(
        ConstantInt.of(2),
        ConstantInt.of(0),
        ConstantInt.of(4),
        0.30F,
        0.55F,
        0.35F,
        0.25F
)
```

### Medium Willow

Trunk:

```java
new UpwardsBranchingTrunkPlacer(
        8,
        2,
        2,
        UniformInt.of(2, 3),
        0.45F,
        UniformInt.of(1, 3),
        blockGetter.getOrThrow(BlockTags.LEAVES)
)
```

Leaves:

```java
new CherryFoliagePlacer(
        ConstantInt.of(3),
        ConstantInt.of(0),
        ConstantInt.of(5),
        0.20F,
        0.45F,
        0.45F,
        0.35F
)
```

### Big Hanging Willow

Trunk:

```java
new UpwardsBranchingTrunkPlacer(
        8,
        2,
        2,
        UniformInt.of(3, 4),
        0.60F,
        UniformInt.of(2, 4),
        blockGetter.getOrThrow(BlockTags.LEAVES)
)
```

Leaves:

```java
new CherryFoliagePlacer(
        ConstantInt.of(4),
        ConstantInt.of(0),
        ConstantInt.of(6),
        0.15F,
        0.35F,
        0.75F,
        0.65F
)
```

Warning: this one can create a lot of leaves.

## 21. Fast Testing Checklist

Use this every time you change tree numbers:

```powershell
.\gradlew.bat runData processResources
```

Then:

1. Restart the Minecraft client.
2. Grow at least 5 new saplings.
3. Do not judge by old trees.
4. Check this file if it still looks unchanged:

```text
build/resources/main/data/meowskis/worldgen/configured_feature/willow.json
```

The values in that JSON are the values the game is likely using.

## 22. What To Change For Common Goals

| Goal | Change |
| --- | --- |
| Wider leaves | Increase foliage `radius` by 1 |
| Fewer leaves | Decrease `radius`, decrease `height`, or increase hole chances |
| More hanging | Increase `hangingLeavesChance` |
| Longer hanging | Increase `hangingLeavesExtensionChance` |
| Less hanging | Lower both hanging values |
| More branch leaf clumps | Increase trunk branch chance or steps |
| Fewer branch leaf clumps | Decrease trunk branch chance or steps |
| Taller tree | Increase trunk `baseHeight` |
| More random height | Increase `heightRandA` or `heightRandB` |
| More open cherry/willow canopy | Increase `cornerHoleChance` and `wideBottomLayerHoleChance` |
| Completely different shape | Use another foliage placer or custom code |

