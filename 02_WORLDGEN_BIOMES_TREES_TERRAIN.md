# Worldgen, Biomes, Trees, Climate, Terrain, and Features

This is the main world-generation study file.

Your current worldgen architecture already points toward the standard pipeline:

```text
CF → PF → BM
```

which means:

```text
ConfiguredFeature
      ↓
PlacedFeature
      ↓
BiomeModifier
```

We will build outward from that.

---

# 1. Worldgen is several systems, not one

Use this mental pipeline:

```text
SEED
  ↓
NOISE / DENSITY
  ↓
TERRAIN SHAPE
  ↓
BIOME / CLIMATE SELECTION
  ↓
SURFACE RULES
  ↓
FEATURES
  ↓
STRUCTURES
```

This distinction matters.

Example problems:

```text
"plains are too bumpy"
→ terrain density/noise

"forest grass looks boring"
→ surface rules / features

"too many trees"
→ placed feature

"tree crown shape is ugly"
→ foliage placer

"desert appears next to snowy biome too often"
→ climate/biome layout

"ocean is not deep enough"
→ density/noise/continental terrain

"swamp hut should be replaced"
→ structure system
```

---

# 2. Dynamic/worldgen registries

Many worldgen things are datapack registries.

Common concepts:

```java
ResourceLocation
ResourceKey<T>
Holder<T>
HolderSet<T>
HolderGetter<T>
BootstrapContext<T>
```

## ResourceLocation

An ID:

```text
meowskis:willow
minecraft:oak
```

Java:

```java
ResourceLocation id =
        ResourceLocation.fromNamespaceAndPath(
                meowski.MOD_ID,
                "willow"
        );
```

## ResourceKey

A typed registry address:

```java
public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY =
        ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(
                        meowski.MOD_ID,
                        "willow"
                )
        );
```

Important:

`ResourceKey<ConfiguredFeature<?, ?>>` is **not the tree itself**.

It is the address used to find it in the registry.

---

# 3. Configured Feature = WHAT

A `ConfiguredFeature` combines:

```text
Feature algorithm
+
configuration data
```

For trees:

```text
Feature.TREE
+
TreeConfiguration
```

Your willow uses exactly this architecture.

Conceptually:

```java
register(
    context,
    WILLOW_KEY,
    Feature.TREE,
    new TreeConfiguration.TreeConfigurationBuilder(
        ...
    ).build()
);
```

---

# 4. Placed Feature = WHERE / HOW OFTEN

A configured feature knows what to create.

A placed feature wraps it with placement modifiers:

```text
how many attempts?
where inside chunk?
what height?
what filters?
which biome?
```

Example concepts:

```java
CountPlacement.of(8)
InSquarePlacement.spread()
HeightmapPlacement.onHeightmap(...)
BiomeFilter.biome()
```

Think:

```text
ConfiguredFeature:
"Here is the recipe for an oak."

PlacedFeature:
"Try this recipe at these candidate positions."
```

---

# 5. Biome Modifier = WHICH BIOMES

NeoForge biome modifiers can add/remove placed features and make other biome changes.

Typical flow:

```text
custom_oak configured feature
    ↓
custom_oak placed feature
    ↓
add_custom_oak biome modifier
    ↓
minecraft:forest
```

NeoForge places biome modifier data at:

```text
data/<modid>/neoforge/biome_modifier/
```

when represented as JSON/datagen output.

---

# 6. Tree anatomy

Minecraft's standard procedural tree system is composed.

```text
TreeConfiguration
├── trunk block provider
├── trunk placer
├── foliage block provider
├── foliage placer
├── minimum size / feature size
├── root placer (optional depending configuration)
└── decorators
```

That means you can change one piece without rewriting all tree generation.

---

# 7. Trunk providers and foliage providers

Simple provider:

```java
BlockStateProvider.simple(Blocks.OAK_LOG)
```

or your block:

```java
BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get())
```

Provider means:

> when the tree asks for a trunk state, which state should be supplied?

More advanced providers can introduce variation.

---

# 8. Trunk placer

A trunk placer owns trunk geometry.

Examples include different vanilla placer classes.

A simple straight trunk pattern:

```java
new StraightTrunkPlacer(
        5,
        2,
        0
)
```

The constructor values participate in determining the generated trunk height.

🟨 Experiment:

Use deliberately exaggerated heights in a disposable test tree.

Compare:

```java
new StraightTrunkPlacer(3, 0, 0)
```

to:

```java
new StraightTrunkPlacer(15, 5, 3)
```

Do not ask "which looks best?" yet.

First ask:

> Can I predict what changed?

---

# 9. Foliage placer

Foliage placers control leaf geometry around foliage attachment points.

Conceptual oak-like pattern:

```java
new BlobFoliagePlacer(
        ConstantInt.of(2),
        ConstantInt.of(0),
        3
)
```

Spruce uses foliage behavior suited to conical/layered foliage.

Your current willow experiment uses a cherry foliage placer combined with an upward-branching trunk placer.

That is a perfect example of **composition**:

```text
willow blocks
+
branching trunk algorithm
+
cherry-like foliage algorithm
```

You can mix vanilla pieces before writing your own algorithm.

---

# 10. IntProvider

Worldgen frequently uses:

```java
IntProvider
```

instead of a plain `int`.

Why?

Because the value may vary per generation.

Always two:

```java
ConstantInt.of(3)
```

means always `3`.

```java
UniformInt.of(3, 6)
```

means choose an integer in a range.

This pattern appears throughout worldgen.

---

# 11. Custom oak learning tree

📁 Usually inside your configured-feature bootstrap class or a dedicated tree configured-feature class.

```java
public static final ResourceKey<ConfiguredFeature<?, ?>> CUSTOM_OAK =
        registerKey("custom_oak");
```

Conceptual registration:

```java
register(
        context,
        CUSTOM_OAK,
        Feature.TREE,

        new TreeConfiguration.TreeConfigurationBuilder(

                BlockStateProvider.simple(Blocks.OAK_LOG),

                new StraightTrunkPlacer(
                        5,
                        2,
                        0
                ),

                BlockStateProvider.simple(Blocks.OAK_LEAVES),

                new BlobFoliagePlacer(
                        ConstantInt.of(2),
                        ConstantInt.of(0),
                        3
                ),

                new TwoLayersFeatureSize(
                        1,
                        0,
                        1
                )
        )
        .ignoreVines()
        .build()
);
```

The important lesson is not the exact prettiness of these numbers.

It is identifying:

```text
block
trunk algorithm
leaves
foliage algorithm
space rules
```

---

# 12. Tree families

Do not make every tree identical.

Instead:

```text
oak_small
oak_normal
oak_tall
oak_wide
oak_old
```

Then create weighted placement.

This lets one biome feel varied without adding 30 different tree species.

Example design:

```text
oak normal     55%
oak small      20%
oak wide       15%
oak old        10%
```

Same idea for:

```text
birch normal
birch tall
birch leaning

spruce small
spruce tall
spruce old

willow young
willow mature
willow ancient
```

---

# 13. Custom trunk placer — the point where you control geometry

When vanilla placers cannot make your desired willow, create your own.

📁 Example:

```text
src/main/java/com/kanjpz/meowski/worldgen/tree/WillowTrunkPlacer.java
```

Simplified architecture:

```java
public class WillowTrunkPlacer extends TrunkPlacer {

    public WillowTrunkPlacer(
            int baseHeight,
            int heightRandA,
            int heightRandB) {

        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            int freeTreeHeight,
            BlockPos startPos,
            TreeConfiguration config) {

        List<FoliagePlacer.FoliageAttachment> foliage =
                new ArrayList<>();

        for (int y = 0; y < freeTreeHeight; y++) {

            BlockPos logPos = startPos.above(y);

            this.placeLog(
                    level,
                    blockSetter,
                    random,
                    logPos,
                    config
            );
        }

        foliage.add(
                new FoliagePlacer.FoliageAttachment(
                        startPos.above(freeTreeHeight),
                        0,
                        false
                )
        );

        return foliage;
    }
}
```

> [!WARNING]
> A custom `TrunkPlacer` also needs its placer type/codec registration. The snippet above teaches the geometry method; it is not the entire registration file.

---

# 14. Procedural branch math

Start with a branch point:

```java
BlockPos branchStart =
        startPos.above(freeTreeHeight - 3);
```

Choose horizontal direction:

```java
Direction direction =
        Direction.Plane.HORIZONTAL.getRandomDirection(random);
```

Extend:

```java
for (int distance = 1; distance <= 5; distance++) {

    BlockPos branchPos =
            branchStart.relative(direction, distance);

    placeLog(...);
}
```

Now slope upward:

```java
BlockPos branchPos =
        branchStart
                .relative(direction, distance)
                .above(distance / 2);
```

This simple coordinate math is the basis of custom procedural trees.

---

# 15. Willow hanging leaves

One approach:

1. create foliage attachment around each large branch end
2. create a crown around attachment
3. choose some crown-edge positions
4. extend leaf columns downward
5. stop if blocked or maximum hanging length reached

Conceptual:

```java
for (int hang = 1; hang <= maxHang; hang++) {

    BlockPos leafPos = edgePos.below(hang);

    if (!canPlaceLeaf(level, leafPos)) {
        break;
    }

    placeLeaf(leafPos);
}
```

Then variation:

```java
int maxHang = 2 + random.nextInt(5);
```

Result:

```text
███████████
 █████████
  ███ ███
   █   █
   █   █
       █
```

---

# 16. Tree growth vs worldgen

Tree worldgen and sapling growth can point toward the same configured tree family.

But they enter through different systems.

A sapling uses a tree grower to select configured features when it grows.

Worldgen uses placed features.

Think:

```text
SAPLING
   ↓
TreeGrower
   ↓
ConfiguredFeature

WORLDGEN
   ↓
PlacedFeature
   ↓
ConfiguredFeature
```

One tree recipe can be reused by both.

---

# 17. Forest placement density

Simplified placement:

```java
Holder<ConfiguredFeature<?, ?>> oak =
        configuredFeatures.getOrThrow(
                ModConfiguredFeatures.CUSTOM_OAK
        );

register(
        context,
        CUSTOM_OAK_PLACED,
        oak,
        List.of(
                CountPlacement.of(8),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(
                        Heightmap.Types.OCEAN_FLOOR
                ),
                BiomeFilter.biome()
        )
);
```

`CountPlacement.of(8)` means placement attempts, not guaranteed eight successful trees.

🟨 Debug:

```java
CountPlacement.of(80)
```

If you still see no trees, density was not the real problem.

Check:

- biome attachment
- survival
- placement filters
- registry/datagen
- tree feature failure

---

# 18. Adding a placed feature to a biome

NeoForge pattern:

```java
context.register(
        ADD_CUSTOM_OAKS,
        new AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.FOREST)
                ),
                HolderSet.direct(
                        placedFeatures.getOrThrow(
                                ModPlacedFeatures.CUSTOM_OAK_PLACED
                        )
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION
        )
);
```

Trees and plants generally belong in the vegetal-decoration stage.

> [!WARNING]
> Be careful reusing vanilla placed features through modifiers because feature ordering can produce feature-cycle crashes. Owning copies under your own namespace can be safer when modifying ordering.

---

# 19. Replacing vanilla trees

Adding your trees does not remove vanilla trees.

To truly overhaul:

```text
REMOVE existing feature
+
ADD custom replacement feature
```

NeoForge supports feature removal biome modifiers.

This is the pattern for your swamp plan:

```text
minecraft:swamp

remove:
vanilla swamp tree placement

add:
meowskis willow placement
```

---

# 20. Lily-pad worldgen

Your block behavior and worldgen should remain separate.

```text
LilyPadsBlock
    stacking / slowdown / survival / state

ConfiguredFeature
    what natural lily-pad patch contains

PlacedFeature
    how often patches are attempted

BiomeModifier
    which swamp/wet biomes receive it
```

## Weighted natural state idea

Player placement:

```text
1 → 2 → 3 → 4 pads
```

Natural generation can use weighted initial states:

```text
pads=1 : common
pads=2 : less common
pads=3 : uncommon
pads=4 : rare
```

You can use a weighted block-state provider or a custom feature depending on how much spatial control you want.

---

# 21. Why independent randomness often looks ugly

Bad visual pattern:

```text
moss grass moss rooted grass moss grass rooted moss
```

when every individual block rolls independent random chance.

It looks like TV static.

Instead create **patch-scale coherence**.

```text
grass grass grass grass
grass moss moss moss
grass moss moss rooted
grass grass coarse grass
```

This is where noise masks become useful.

---

# 22. Custom feature pattern

📁 Example:

```text
src/main/java/com/kanjpz/meowski/worldgen/feature/ForestFloorFeature.java
```

Architecture:

```java
public class ForestFloorFeature
        extends Feature<NoneFeatureConfiguration> {

    public ForestFloorFeature(
            Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(
            FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int radius = 4 + random.nextInt(4);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                if (x * x + z * z > radius * radius) {
                    continue;
                }

                BlockPos column =
                        origin.offset(x, 0, z);

                BlockPos surface =
                        level.getHeightmapPos(
                                Heightmap.Types.WORLD_SURFACE_WG,
                                column
                        ).below();

                BlockState oldState =
                        level.getBlockState(surface);

                if (!oldState.is(Blocks.GRASS_BLOCK)) {
                    continue;
                }

                float roll = random.nextFloat();

                if (roll < 0.55F) {
                    level.setBlock(
                            surface,
                            Blocks.MOSS_BLOCK.defaultBlockState(),
                            2
                    );
                } else if (roll < 0.80F) {
                    level.setBlock(
                            surface,
                            Blocks.COARSE_DIRT.defaultBlockState(),
                            2
                    );
                } else {
                    level.setBlock(
                            surface,
                            Blocks.ROOTED_DIRT.defaultBlockState(),
                            2
                    );
                }
            }
        }

        return true;
    }
}
```

This is a **learning feature**, not your final forest-floor algorithm.

Why?

Because the material selection is still per-block random.

Later replace that with coherent noise.

---

# 23. Surface Rules vs Features

Use **surface rules** for broad terrain skin.

Use **features** for local objects/patches.

Examples:

```text
"this badlands plateau should broadly expose red sand"
→ surface rule

"small coarse-dirt patch around trees"
→ feature

"entire snowy high region has snow top layer"
→ surface rule

"occasional moss patch"
→ feature
```

---

# 24. Biome does NOT own terrain height

This is a critical fact.

Do not search for:

```java
plains.setHeight(72)
```

because modern vanilla biomes do not work that way.

A biome describes things such as:

- climate/environment values
- effects
- mobs
- generation features

Terrain shape comes primarily from the noise/density chunk generator.

Biome placement and terrain use related climate/noise dimensions, which makes them correlate, but:

```text
Biome ≠ terrain height function
```

---

# 25. Vanilla classes worth reading

Open these using IntelliJ `Ctrl+N`:

```text
net.minecraft.data.worldgen.biome.OverworldBiomes
OverworldBiomeBuilder
NoiseGeneratorSettings
NoiseRouterData
DensityFunctions
SurfaceRules
```

Study them in that order only after you understand CF/PF/BM.

---

# 26. Climate placement

Modern overworld biome placement works with several climate dimensions.

Conceptually think of:

```text
temperature
humidity
continentalness
erosion
weirdness / ridges
depth
```

A biome can occupy ranges of this multidimensional climate space.

Important:

```java
.temperature(0.8F)
```

inside a biome builder is **not the same thing** as saying:

> place this biome at climate-noise temperature coordinate 0.8.

Biome environmental temperature and biome-source climate placement are separate concepts.

---

# 27. Temperature + humidity macro-regions

Your goal:

```text
large hot region
├── desert
├── savanna
└── badlands
```

rather than:

```text
desert
snow
desert
forest
tiny savanna
snow
```

Conceptually use broad climate ranges:

```text
hot temperature band
        ↓
humidity/dryness separates biome families
        ↓
terrain dimensions influence subtypes
```

Example design:

```text
HOT + VERY DRY + LOW/FLAT
→ desert

HOT + MEDIUM DRY + ROLLING
→ savanna

HOT + DRY + ERODED/RIDGED/HIGH
→ badlands
```

This is design logic, not literal copy-paste code.

---

# 28. Elevation and biome choice

You want biome distribution to respect terrain.

Example conceptual rule:

```text
low coastal wet land
→ swamp / mangrove

moderate inland rolling terrain
→ plains / forest

high rugged terrain
→ mountain-family biomes
```

Do not implement this as:

```java
if (y > 130) biome = mountain;
```

for the entire overworld.

Biome source operates during generation with climate parameters, not as a simple post-process over final block Y.

Instead correlate biome parameter ranges with the same terrain-driving dimensions.

---

# 29. Terrain math — conceptual only

🟦 **CONCEPTUAL MATH — not direct paste code.**

Imagine:

```java
double broadRoll =
        noise(x / 350.0, z / 350.0) * 12.0;

double localRoll =
        noise(x / 100.0, z / 100.0) * 4.0;

double height =
        72.0
        + broadRoll
        + localRoll;
```

Interpretation:

```text
x / 350
→ broad horizontal landform scale

* 12
→ vertical influence

x / 100
→ smaller detail

* 4
→ lower detail amplitude
```

Your desired plains should have:

```text
broad elevation
+
gentle local detail
```

not:

```text
flat plane
```

and not:

```text
constant one-block noise bumps
```

---

# 30. Scale/frequency intuition

Compare:

```java
noise(x / 50.0, z / 50.0)
```

and:

```java
noise(x / 500.0, z / 500.0)
```

The second changes more slowly across space.

That generally creates broader regions/features.

Compare amplitudes:

```java
noise * 3
noise * 30
```

Second has much larger vertical effect when used as height influence.

---

# 31. Density function mental model

A density function roughly answers:

```text
for coordinate x,y,z → produce a number
```

Very simplified intuition:

```text
positive-ish density
→ solid terrain

negative-ish density
→ air
```

Actual Minecraft terrain generation has more layers, including aquifers and material rules, but this mental model helps.

---

# 32. Compose functions

A core idea:

```text
simple functions
+
simple functions
→ complex terrain
```

Conceptual:

```java
DensityFunction terrain =
        DensityFunctions.add(
                base,
                rollingNoise
        );
```

Or:

```java
DensityFunction ridges =
        DensityFunctions.mul(
                ridgeNoise,
                DensityFunctions.constant(2.5)
        );
```

You will see nested function trees in vanilla code/data.

Read them from inside outward.

---

# 33. Clamp

```java
Mth.clamp(value, min, max)
```

means:

```text
if too low → min
if too high → max
otherwise unchanged
```

Useful for preventing terrain signals from exploding.

---

# 34. Lerp

Linear interpolation:

```java
Mth.lerp(progress, start, end)
```

Concept:

```text
progress 0.0 → start
progress 0.5 → halfway
progress 1.0 → end
```

Useful for blending terrain personalities.

Hard transition:

```text
PLAIN | MOUNTAIN
```

Blended transition:

```text
plain → foothill → hill → mountain
```

---

# 35. Ridges

Conceptual ridge transform:

```java
double ridge =
        1.0 - Math.abs(noiseValue);
```

This transforms values so regions near noise zero become ridge-like maxima.

Real vanilla ridge systems are more involved, but the transformation teaches why math can reshape noise.

---

# 36. Continentalness

Useful conceptual interpretation:

```text
strong negative
→ deep ocean

negative
→ ocean

near boundary
→ coast

positive
→ inland

strong positive
→ deep inland
```

Do not memorize arbitrary numbers from tutorials unless they match your exact generator/version.

Focus on the dimension's role.

---

# 37. Designing deeper oceans

Your desired ocean concept:

```text
coast
  ↓ gentle shelf
ocean
  ↓
deep basin
  ↓
rare trench
```

Instead of:

```text
random seabed noise everywhere
```

Use separate scales:

```text
very broad basin signal
+
smaller seabed detail
+
rare trench mask
```

Conceptual:

```java
depth =
        broadOceanBasin
        + seabedDetail
        + trenchMask * largeDepth;
```

---

# 38. Swamp terrain concept

Your swamp goals fit this architecture:

```text
continental/coastal signal
    ↓
favor low terrain near ocean/coasts

broad wetland basin
    ↓
mostly low relief

island noise
    ↓
patches of raised ground

surface rules
    ↓
mud / dark soil / grass

features
    ↓
willow / cattail / lily pads
```

Do not make the islands by placing dirt blobs as decorations if you want them to feel like true terrain.

Terrain islands should come from terrain shaping.

---

# 39. Plains concept

Your target:

```text
wide buildable surfaces
+
rolling elevation
+
occasional broad hills
+
low micro-noise
```

Design layers:

```text
continent/base
+
broad roll
+
low amplitude detail
+
rare hill mask
```

This can remain interesting without being flat.

---

# 40. Forest concept

Forest can use similar macro terrain as plains but somewhat stronger relief:

```text
broad valleys
+
hills
+
tree groups
+
floor patches
+
clearings
```

The important insight:

Biome identity is not only terrain.

```text
terrain shape
+
surface
+
vegetation
+
tree architecture
+
density
+
clearings
```

all contribute.

---

# 41. Mountain concept

Mountains should not simply be:

```text
plains noise * 10
```

A richer system can use:

```text
mountain region mask
×
ridge signal
+
erosion shaping
+
height variation
```

This lets mountains form coherent ranges rather than random spikes.

---

# 42. Biome size / micro-biome problem

If climate noise varies too rapidly or ranges fragment excessively, you can get tiny biome fragments.

To favor larger coherent regions:

- use broad climate-scale signals
- avoid excessive narrow parameter slices
- group related biomes in neighboring parameter ranges
- introduce sub-biomes as controlled variation, not random confetti

---

# 43. World preset

A custom world preset becomes useful when your overhaul changes the generator architecture itself.

Think:

```text
WORLD PRESET
    ↓
DIMENSION
    ↓
CHUNK GENERATOR
    ↓
BIOME SOURCE
    +
NOISE GENERATOR SETTINGS
```

This is a later-stage task.

Do not begin here before you can confidently work with features and registries.

---

# 44. Structures are separate

Trees/plants:

```text
Feature
```

large structured content:

```text
Structure
StructureSet
Template/Jigsaw systems
```

Your swamp-hut replacement belongs here.

High-level plan:

```text
1. understand existing vanilla hut eligibility/placement
2. stop/replace vanilla hut generation
3. create your custom structure/template
4. create structure set/placement
5. assign biome eligibility
6. test spacing and terrain adaptation
```

Do not delete Minecraft Java source.

Use data/registries/mod hooks.

---

# 45. Cattails

Two-block cattail architecture:

```text
UPPER
LOWER
WATER
GROUND
```

Use a half property:

```java
EnumProperty<DoubleBlockHalf> HALF
```

Placement checks:

```text
lower target has water/valid placement
upper target is free
ground below valid
```

Survival:

```text
upper requires matching lower
lower requires valid wet ground/water condition
```

Breaking one half should cleanly remove/invalidate the other.

---

# 46. Testing worldgen

Create a permanent test world:

```text
WORLDGEN_TEST
seed: 123456789
```

After major noise/density/registry changes, regenerate the world using the same seed.

Compare fixed coordinates.

Create a notebook:

```text
seed
coordinates
setting
old result
new result
```

This converts visual tweaking into an experiment.

---

# 47. Debug by exaggeration

Examples:

```text
tree attempts:
8 → 80

moss chance:
0.1 → 1.0

terrain amplitude:
5 → 50
```

If exaggerating the setting does nothing, the setting is probably:

- not loaded
- not attached
- filtered out
- not the system you thought controlled the result

---

# 48. Performance mindset

Worldgen code may run enormous numbers of times.

Be careful with:

- scanning huge cubes repeatedly
- allocating giant lists per block
- searching far distances for every placement
- expensive path-like logic during chunk generation
- unnecessary repeated registry lookups inside inner loops

Prefer:

```text
broad cheap signal
→ mask candidate region
→ expensive detail only where needed
```

---

# 49. Study exercises

## Tree exercise

Create two oak configurations that differ only in trunk placer numbers.

Predict which is taller before launching the game.

## Placement exercise

Use same configured tree with:

```text
PlacedFeature A: CountPlacement 3
PlacedFeature B: CountPlacement 30
```

Explain why tree shape does not change.

## Surface exercise

Write on paper which system should own each:

```text
snow across entire mountaintop
moss around occasional tree
mud across swamp basin
single boulder
```

## Climate exercise

Design a hot macro-region table:

| Temperature | Humidity | Terrain | Biome |
|---|---|---|---|
| hot | very dry | flat | desert |
| hot | medium dry | rolling | savanna |
| hot | dry | ridged | badlands |

The goal is understanding dimensions, not exact parameter numbers yet.

---

# 50. References

NeoForge biome modifiers:
https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/

NeoForge registries:
https://docs.neoforged.net/docs/1.21.1/concepts/registries/

Minecraft mappings:
https://mappings.dev/1.21.1/

Classes to search:
- `OverworldBiomes`
- `OverworldBiomeBuilder`
- `NoiseGeneratorSettings`
- `NoiseRouterData`
- `DensityFunctions`
- `SurfaceRules`
- `TrunkPlacer`
- `FoliagePlacer`
