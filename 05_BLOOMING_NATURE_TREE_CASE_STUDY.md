# Case Study: How BloomingNature Generates Its Trees

**Inspected file:** `letsdo-bloomingnature-neoforge-1.1.10(1).7z`  
**Your target:** Minecraft 1.21.1 · NeoForge 21.1.248 · Java 21 · mod ID
`meowskis`.

This is a technical study of the supplied compiled mod and its data files. It
does not copy the mod's code/assets into Meowskis. The useful lesson is its
architecture and its remixing of vanilla tree components.

The archive declares Minecraft `[1.21,)`, NeoForge `[21.0,)`, and an
Architectury dependency. Therefore its JSON concepts are useful for your
1.21.1 project, but its compiled Java registration code must not be pasted into
your pure NeoForge project. Use your own 1.21.1 classes and mappings.

---

# 1. Main discovery

BloomingNature's normal trees are **procedural configured features**, not
structure-template trees.

It builds forests in five layers:

```text
individual tree ConfiguredFeature
→ survival-check PlacedFeature
→ random-selector ConfiguredFeature
→ density/position PlacedFeature
→ NeoForge biome modifiers remove vanilla trees and add replacement trees
```

Example for the vanilla forest biome:

```text
oak_tree_small / mid / tall / decorated + swamp oak + birch
→ each gets *_checked placed feature
→ forest_trees random selector
→ forest_trees_checked: 32 attempts, square spread, dry surface, heightmap
→ remove minecraft:trees_birch_and_oak
→ add bloomingnature:trees/forest_trees_checked
```

This layered design is the main technique you want for multiple oak shapes.

---

# 2. Exact files used by the forest replacement

Inside the supplied mod archive:

```text
data/bloomingnature/worldgen/configured_feature/trees/oak/oak_tree_small.json
data/bloomingnature/worldgen/configured_feature/trees/oak/oak_tree_mid.json
data/bloomingnature/worldgen/configured_feature/trees/oak/oak_tree_tall.json
data/bloomingnature/worldgen/configured_feature/trees/oak/oak_tree_tall_decorated.json

data/bloomingnature/worldgen/placed_feature/trees/oak/oak_tree_small_checked.json
data/bloomingnature/worldgen/placed_feature/trees/oak/oak_tree_mid_checked.json
data/bloomingnature/worldgen/placed_feature/trees/oak/oak_tree_tall_checked.json
data/bloomingnature/worldgen/placed_feature/trees/oak/oak_tree_tall_decorated_checked.json

data/bloomingnature/worldgen/configured_feature/trees/forest_trees.json
data/bloomingnature/worldgen/placed_feature/trees/forest_trees_checked.json

data/bloomingnature/neoforge/biome_modifier/removals_forest.json
data/bloomingnature/neoforge/biome_modifier/additions_forest.json
```

The word `checked` is this mod's naming convention, not a required Minecraft
suffix.

---

# 3. Every oak configuration

| Oak configuration | Trunk | Height inputs | Foliage | Special behavior |
|---|---|---:|---|---|
| `oak_bush` | straight | `1 + 0 + 0` | bush, radius 2, height 2 | default/filler shrub |
| `oak_tree_small` | fancy | `7 + 2 + 2` | cherry, radius 3, height 4 | fewer hanging leaves |
| `oak_tree_mid` | fancy | `10 + 1 + 0` | cherry, radius 4, height 4 | normal mid tree |
| `oak_tree_mid_beehive` | fancy | `11 + 1 + 0` | cherry, radius 4, height 4 | 5% beehive decorator |
| `oak_tree_tall` | fancy | `18 + 1 + 0` | cherry, radius 4, height 4 | tall tree |
| `oak_tree_tall_decorated` | fancy | `18 + 1 + 0` | cherry, radius 4, height 4 | ground decorator |
| `oak_tree_swamp` | fancy | `10 + 1 + 0` | cherry, radius 4, height 4 | leaf-vine decorator |
| `blooming_oak_tree_tall` | fancy | `18 + 1 + 0` | cherry, radius 4, height 4 | weighted blooming leaves + mangrove roots |

The three height numbers are the trunk placer's `base_height`,
`height_rand_a`, and `height_rand_b`. Minecraft samples the random additions;
they are not simply always added to create one fixed height.

## The clever part

The ordinary oak variants use only vanilla algorithms:

```text
minecraft:fancy_trunk_placer
+ minecraft:cherry_foliage_placer
+ minecraft:two_layers_feature_size
+ optional vanilla decorators
```

So the author gets oak trees that look dramatically different without writing
an `OakTrunkPlacer` or `OakFoliagePlacer` Java class.

This is the approach I recommend you learn first.

---

# 4. Anatomy of its mid oak

The supplied `oak_tree_mid.json` is effectively:

```json
{
  "type": "minecraft:tree",
  "config": {
    "dirt_provider": {
      "type": "minecraft:simple_state_provider",
      "state": { "Name": "minecraft:dirt" }
    },
    "trunk_provider": {
      "type": "minecraft:simple_state_provider",
      "state": {
        "Name": "minecraft:oak_log",
        "Properties": { "axis": "y" }
      }
    },
    "foliage_provider": {
      "type": "minecraft:simple_state_provider",
      "state": {
        "Name": "minecraft:oak_leaves",
        "Properties": {
          "persistent": "false",
          "distance": "7"
        }
      }
    },
    "trunk_placer": {
      "type": "minecraft:fancy_trunk_placer",
      "base_height": 10,
      "height_rand_a": 1,
      "height_rand_b": 0
    },
    "foliage_placer": {
      "type": "minecraft:cherry_foliage_placer",
      "corner_hole_chance": 1.0,
      "hanging_leaves_chance": 0.5,
      "hanging_leaves_extension_chance": 0.1,
      "height": 4,
      "offset": 3,
      "radius": 4,
      "wide_bottom_layer_hole_chance": 0.25
    },
    "ignore_vines": true,
    "force_dirt": false,
    "minimum_size": {
      "type": "minecraft:two_layers_feature_size",
      "min_clipped_height": 4,
      "limit": 0,
      "lower_size": 0,
      "upper_size": 0
    },
    "sapling_provider": {
      "type": "minecraft:simple_state_provider",
      "state": {
        "Name": "minecraft:oak_sapling",
        "Properties": { "stage": "0" }
      }
    },
    "decorators": []
  }
}
```

🟦 **What each piece owns**

- `trunk_provider`: which log block is placed.
- `foliage_provider`: which leaf state is placed.
- `fancy_trunk_placer`: trunk/branch attachment geometry.
- `cherry_foliage_placer`: leaf volumes around foliage attachments.
- `minimum_size`: free-space requirements by height layer.
- `sapling_provider`: sapling state associated with this configuration.
- `decorators`: optional work after trunk and foliage.

🟪 **VANILLA SOURCE:** `FancyTrunkPlacer`, `CherryFoliagePlacer`,
`TwoLayersFeatureSize`, and `TreeConfiguration`.

---

# 5. How the checked feature works

The individual configured tree is wrapped in a placed feature containing one
filter:

```json
{
  "feature": "bloomingnature:trees/oak/oak_tree_mid",
  "placement": [
    {
      "type": "minecraft:block_predicate_filter",
      "predicate": {
        "type": "minecraft:would_survive",
        "state": {
          "Name": "minecraft:oak_sapling",
          "Properties": { "stage": "0" }
        }
      }
    }
  ]
}
```

This does not control forest density. It asks whether an oak sapling would
survive at the candidate position before the selector chooses this tree.

Why separate it?

```text
individual wrapper = can this tree type start here?
top-level wrapper = how many candidates and where in the chunk?
```

That lets many biome selectors reuse the same checked oak variant.

🟪 **VANILLA SOURCE:** `BlockPredicateFilter`, `BlockPredicate.wouldSurvive`,
and `PlacedFeature`.

---

# 6. The random selector is sequential, not a weight list

Forest selector entries are declared in this order:

| Order | Declared chance | Feature |
|---:|---:|---|
| 1 | 0.08 | tall oak |
| 2 | 0.34 | mid oak |
| 3 | 0.31 | tall decorated oak |
| 4 | 0.27 | tall swamp oak |
| 5 | 0.12 | mid swamp oak |
| 6 | 0.26 | mid birch |
| default | remaining | oak bush |

`RandomFeatureConfiguration` tries entries in order. For each entry it rolls
that entry's chance; the first success wins. The chances are therefore not
ordinary weights and do not need to total 1.

Approximate effective forest probabilities are:

| Feature | Effective probability |
|---|---:|
| tall oak | 8.00% |
| mid oak | 31.28% |
| tall decorated oak | 18.82% |
| tall swamp oak | 11.31% |
| mid swamp oak | 3.67% |
| mid birch | 7.00% |
| default oak bush | 19.92% |

Example: mid oak only gets tested after tall oak failed, so its effective
chance is `0.92 × 0.34 = 0.3128`.

🟥 **Common mistake:** treating these chance fields as weights and expecting
34% of all trees to be mid oak.

🟪 **VANILLA SOURCE:** `RandomSelectorFeature` and
`RandomFeatureConfiguration`.

---

# 7. Top-level forest placement

The top-level placed feature is:

```json
{
  "feature": "bloomingnature:trees/forest_trees",
  "placement": [
    {
      "type": "minecraft:count",
      "count": {
        "type": "minecraft:constant",
        "value": 32
      }
    },
    { "type": "minecraft:in_square" },
    {
      "type": "minecraft:surface_water_depth_filter",
      "max_water_depth": 0
    },
    {
      "type": "minecraft:heightmap",
      "heightmap": "OCEAN_FLOOR"
    },
    { "type": "minecraft:biome" }
  ]
}
```

This means 32 **attempts**, not a promise of 32 successfully placed trees.
Trees can fail due to survival, obstruction, space, or biome/position filters.

Placement order:

```text
make 32 candidate streams
→ spread x/z across the chunk
→ require zero surface-water depth
→ move to OCEAN_FLOOR heightmap position
→ verify current biome owns this placed feature
→ selector chooses an individual checked tree
```

Thirty-two attempts is deliberately dense. Do not copy the number before
deciding how dense your forest should feel and testing performance/clearance.

---

# 8. It replaces vanilla trees instead of only adding more

Removal:

```json
{
  "type": "neoforge:remove_features",
  "biomes": "minecraft:forest",
  "features": [
    "minecraft:forest_flowers",
    "minecraft:trees_birch_and_oak"
  ],
  "steps": "vegetal_decoration"
}
```

Addition includes:

```json
{
  "type": "neoforge:add_features",
  "biomes": "minecraft:forest",
  "features": [
    "bloomingnature:trees/forest_trees_checked"
  ],
  "step": "vegetal_decoration"
}
```

The actual addition file also includes grasses, flowers, fallen trees, rocks,
and stone features. Tree replacement works because removal and addition target
the same biome generation stage.

For Meowskis, keep your first test narrower: remove only the vanilla forest
tree feature and add only your oak selector. Add forest-floor systems later.

🟪 **NEOFORGE SOURCE:** add/remove biome modifiers and
`GenerationStep.Decoration.VEGETAL_DECORATION`.

---

# 9. Every inspected tree family

The archive contains 62 individual tree configured-feature files across these
families, plus biome-level selectors.

| Family | Variants inspected | Main trunk placers | Main foliage placers | Important lesson |
|---|---|---|---|---|
| Acacia | bush, small, mid | straight, forking | bush, acacia | vanilla algorithms + altered ground |
| Aspen | mid, tall | straight | **custom aspen** | custom narrow layered crown |
| Baobab | one giant form | **custom baobab** | blob | complex hollow trunk/roots/arms in Java |
| Birch | small, mid, tall, ultra-tall, pine, roots, decorated, beehive | fancy, forking, straight | cherry, taiga | many shapes from vanilla component remixing |
| Cherry | one decorated form | cherry | cherry | vanilla cherry algorithms with altered ground/bees |
| Chestnut | one form | fancy | cherry | custom blocks with vanilla algorithms |
| Cypress | normal, decorated | straight | **custom cypress** | fixed layered taper crown |
| Dark oak | mid, two mushroom giants, willow-like | dark-oak, fancy | fancy | decorators create identity after basic tree |
| Ebony | small/big, decorated | cherry, dark-oak | cherry, jungle | roots + vines + altered ground |
| Fan palm | one form | **custom crooked** | **custom palm** | Java only where vanilla cannot make the silhouette |
| Fir | small/mid and green variants | straight | mega-pine | block providers change color/species; algorithms reused |
| Jungle | bush, crooked, decorated crooked, mid, big, huge | straight, **custom crooked**, dark-oak | bush, jungle | scale, vines/cocoa, and roots create families |
| Larch | normal, green, orange | straight | mega-pine | same geometry family with different foliage providers |
| Oak | bush, small, mid, mid-beehive, tall, tall-decorated, swamp, blooming | fancy/straight | cherry/bush | best model for your first oak overhaul |
| Spruce | bush, small, mid, tall, pine-mid, pine-tall, mega-mid, mega-tall | straight, **custom tall-giant** | taiga, mega-pine | multiple conifer silhouettes and 2×2 giants |
| Swamp cypress | one form | fancy | jungle | leaf vines, narrow canopy |
| Swamp oak | mid, mid-beehive, swamp, tall | fancy | cherry | same method as oak with custom blocks/decorators |

## Custom Java algorithms found

Only three custom trunk placer types were found:

- `CrookedTrunkPlacer`
- `BaobabTrunkPlacer`
- `TallGiantTrunkPlacer`

Only four custom foliage placer types were found:

- `AspenFoliagePlacer`
- `CypressFoliagePlacer`
- `PalmFoliagePlacer`
- `TaigaFoliagePlacer`

One custom decorator was found:

- `MushroomDecorator`

That is the strongest lesson: a mod with many tree appearances does not need a
custom Java algorithm for every species.

---

# 10. What each custom placer does

## CrookedTrunkPlacer

- chooses one horizontal direction;
- adds two small offset base logs;
- grows upward;
- after the third level, each level has a 40% chance to step sideways in the
  chosen direction;
- returns one foliage attachment at the top.

Used for fan palm and crooked jungle trees.

## TallGiantTrunkPlacer

- prepares a 2×2 dirt footprint;
- builds a 2×2 trunk for the main sampled height;
- continues one corner upward by an `extra_height` codec parameter;
- returns one double-trunk foliage attachment above the extension.

Used for the custom mega spruce variants.

## BaobabTrunkPlacer

- builds a large hollow diamond-like trunk shell;
- extends root feet downward through air/water toward ground;
- creates randomized raised base/crown segments;
- grows four horizontal top arms;
- returns foliage attachments at arm ends;
- sometimes places water inside the hollow trunk.

This is far more specialized than you need for first oak variants.

## AspenFoliagePlacer

- places a controlled sequence of narrow leaf rows around the top attachment;
- changes radius by layer;
- adds cross-like wider layers at selected heights;
- calculates foliage height relative to a configured trunk-height provider.

## CypressFoliagePlacer

- uses a fixed 14-layer radius array;
- begins with several radius-zero tip layers;
- widens through radius-one and radius-two rings;
- randomly skips some edge cells for a less solid cone.

## PalmFoliagePlacer

- starts at the attachment;
- makes four directional quadrants;
- extends leaves outward and downward;
- creates the drooping palm silhouette directly.

## TaigaFoliagePlacer

- uses a longer fixed layer-radius pattern;
- creates irregular circular rings;
- skips selected cells randomly;
- adds lower rows based on computed foliage height.

🟥 Decompiled code is useful for understanding behavior but should not be
copied as source. Names/casts may be imperfect, and the original license still
applies.

---

# 11. Biome selector overview

| Biome-style selector | Main tree mix | Top-level density style |
|---|---|---|
| forest | oak, swamp oak, birch, bush | constant 32 attempts |
| flower forest | blooming/tall/mid-bee oak, rooted birch, bush | count + noise |
| plains | chestnut, oak, swamp oak, birch, bush | rare single attempts |
| river | bush, mid oak, tall swamp oak | rarity + noise |
| birch forest | several birch sizes/decorations | noise + always-pass rarity 1 |
| old-growth birch | ultra/tall/rooted/decorated birch | noise-based dense placement |
| dark forest | dark-oak and mushroom-decorated giants | constant 20 attempts |
| taiga | spruce mid/tall/small/bush | weighted count 10–11 |
| snowy taiga | fir small/mid | weighted count 10–11 |
| old-growth spruce | custom mega spruce + normal spruce | weighted count 10–11 |
| old-growth pine | vanilla mega spruce + custom pine forms | weighted count 10–11 |
| jungle | huge/mid jungle, ebony, bush | constant 38 attempts |
| sparse jungle | vanilla jungle, ebony, crooked jungle, bush | one attempt |
| savanna | acacia bush/small/mid | noise + rarity 2 |
| swamp | swamp oak, swamp cypress, oak, willow-like dark oak | weighted count 2–3, water depth ≤2 |
| cherry grove | custom/vanilla cherry mixes | constant 9 attempts |
| larch forest | larch color/height variants | weighted count 10–11 |
| cypress fields | cypress, decorated cypress, bush, mid oak | rare 0–1 attempt |
| cold grassland | spruce/fir variants/bush | very rare single attempt |
| baobab savanna | acacia bush/tree + baobab | rarity + noise |

This table exposes a useful separation:

```text
shape variety belongs in selector
density belongs in outer placed feature
biome membership belongs in biome modifier
```

---

# 12. How to build your Meowskis oaks this way

Do not start with a custom `OakTrunkPlacer`. Build this first:

```text
MEOWSKIS OAK FAMILY v1
├─ oak_bush
│  straight trunk + bush foliage
├─ oak_small
│  fancy trunk + cherry foliage
├─ oak_mid
│  fancy trunk + cherry foliage
├─ oak_tall
│  fancy trunk + cherry foliage
└─ oak_old
   forking/fancy trunk + cherry foliage + altered-ground decorator
```

Recommended project ownership:

```text
src/main/java/com/kanjpz/meowski/worldgen/ModConfiguredFeatures.java
  individual tree configurations + random-selector configured feature

src/main/java/com/kanjpz/meowski/worldgen/ModPlacedFeatures.java
  checked individual wrappers + biome-density outer wrapper

src/main/java/com/kanjpz/meowski/worldgen/ModBiomeModifiers.java
  remove vanilla tree feature + add your replacement

src/main/java/com/kanjpz/meowski/datagen/ModDatapackProvider.java
  already registers configured feature → placed feature → biome modifier
```

Generated output should mirror:

```text
src/generated/resources/data/meowskis/worldgen/configured_feature/trees/oak/*.json
src/generated/resources/data/meowskis/worldgen/placed_feature/trees/oak/*_checked.json
src/generated/resources/data/meowskis/worldgen/configured_feature/trees/forest_oaks.json
src/generated/resources/data/meowskis/worldgen/placed_feature/trees/forest_oaks.json
src/generated/resources/data/meowskis/neoforge/biome_modifier/replace_forest_trees.json
```

Your current `ModDatapackProvider` already has the registry order needed for
this architecture. `ModPlacedFeatures.bootstrap` and
`ModBiomeModifiers.bootstrap` were empty in the inspected repository snapshot,
so they are the next missing links—not a reason to rewrite the tree algorithm.

---

# 13. First implementation checkpoints

## Checkpoint 1 — one oak recipe

Create only `meowskis:oak_tree_mid` with vanilla oak log/leaves,
`FancyTrunkPlacer`, and `CherryFoliagePlacer`.

Test it directly before natural biome generation. Grow/place at least 20 and
record:

- minimum/maximum trunk height;
- canopy width;
- branch clearance;
- failure rate near terrain/other trees.

## Checkpoint 2 — one checked wrapper

Wrap `oak_tree_mid` with a `BlockPredicateFilter` using the oak sapling's
`wouldSurvive` predicate.

Test on grass, stone, water edge, blocked space, and steep terrain.

## Checkpoint 3 — three visible variants

Add small, mid, and tall. Use exaggerated heights so screenshots prove the
selector works.

## Checkpoint 4 — random selector

Combine the three checked variants and a default bush. Use sequential chance
math deliberately.

## Checkpoint 5 — low-density biome injection

Add the selector to one test biome at a very low count without removing vanilla
trees. This proves registration and placement.

## Checkpoint 6 — replacement

Only after addition works, remove that biome's vanilla tree feature. Generate a
fresh world and confirm the forest contains only the intended tree systems.

## Checkpoint 7 — tuning

Tune shape probabilities and density separately. Never change both during the
same comparison.

---

# 14. What not to copy from this mod

- Do not copy its compiled/decompiled classes into your source.
- Do not copy 32 attempts just because its forest uses 32.
- Do not copy custom blocks/textures or names.
- Do not copy Architectury registration into NeoForge's deferred registers.
- Do not remove every vanilla tree until your replacement is proven.
- Do not use the selector's declared chances as if they were weights.
- Do not write custom Java placers while vanilla combinations still express
  the shape you want.

What you should copy as a learning idea:

```text
small reusable tree recipes
→ checked reusable wrappers
→ biome-specific selectors
→ outer density placement
→ explicit vanilla removal/replacement
```

---

# 15. Best vanilla classes to inspect in IntelliJ

Open these in this order:

1. `TreeConfiguration`
2. `TrunkPlacer`
3. `FancyTrunkPlacer`
4. `FoliagePlacer`
5. `CherryFoliagePlacer`
6. `TreeDecorator`
7. `AlterGroundDecorator`
8. `PlacedFeature`
9. `BlockPredicateFilter`
10. `RandomSelectorFeature`
11. `RandomFeatureConfiguration`
12. `VegetationPlacements`
13. `TreeFeatures`

When you open a class, ask:

```text
What fields does it store?
What does its codec expose to data?
What does its constructor require?
What method actually places blocks?
What does it return to the next system?
```

---

# 16. What you should now be able to repeat without help

You should now be able to explain why BloomingNature can make dozens of tree
appearances with only a handful of custom Java algorithms, trace an oak from
individual configured feature through survival wrapper, selector, density
placement, and biome modifier, and plan your own small/mid/tall/old oak family
using vanilla 1.21.1 placers before writing custom code.

The next coding lesson should implement only **Checkpoint 1: one Meowskis mid
oak** against your newest local files. Before that lesson, confirm whether the
other Codex left any unpushed changes in `ModConfiguredFeatures`,
`ModPlacedFeatures`, or `ModBiomeModifiers`.
