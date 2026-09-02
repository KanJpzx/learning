# Project Architecture, Debugging, Study Drills, and Code-Reading Guide

This file ties the other study files together.

---

# 1. A scalable project layout

You do not have to refactor immediately, but this is a useful long-term mental target:

```text
src/main/java/com/kanjpz/meowski/
├── meowski.java
├── MeowskiClient.java
│
├── block/
│   ├── ModBlocks.java
│   ├── custom/
│   │   ├── LilyPadsBlock.java
│   │   └── ...
│   └── entity/
│       └── ...
│
├── item/
│   ├── ModItems.java
│   └── custom/
│
├── entity/
│   ├── ModEntities.java
│   ├── custom/
│   ├── ai/
│   │   ├── goal/
│   │   ├── task/
│   │   └── navigation/
│   └── client/
│
├── sound/
│   └── ModSounds.java
│
├── worldgen/
│   ├── ModConfiguredFeatures.java
│   ├── ModPlacedFeatures.java
│   ├── ModBiomeModifiers.java
│   ├── biome/
│   ├── feature/
│   ├── terrain/
│   ├── tree/
│   └── structure/
│
├── datagen/
├── network/
└── util/
```

Packages should communicate architecture.

Do not create folders only because they look organized.

Create them when a subsystem has enough code to deserve separation.

---

# 2. Resource layout

```text
src/main/resources/
├── assets/meowskis/
│   ├── blockstates/
│   ├── models/
│   │   ├── block/
│   │   └── item/
│   ├── textures/
│   │   ├── block/
│   │   ├── item/
│   │   └── entity/
│   ├── sounds/
│   └── sounds.json
│
└── data/meowskis/
    ├── tags/
    ├── loot_table/
    ├── recipe/
    └── worldgen/
```

NeoForge biome modifiers use a NeoForge registry path:

```text
data/meowskis/neoforge/biome_modifier/
```

when emitted as datapack JSON.

Generated resources belong in:

```text
src/generated/resources/
```

in your current Gradle setup.

---

# 3. Registration vs behavior

A registry class should not become a 2,000-line behavior class.

Example:

```java
public class ModBlocks {

    public static final DeferredBlock<Block> EXAMPLE =
            BLOCKS.register(
                    "example",
                    () -> new ExampleBlock(...)
            );
}
```

This tells Minecraft:

> create/register this block.

Behavior belongs in:

```java
public class ExampleBlock extends Block {
    ...
}
```

---

# 4. Bootstrap/datagen worldgen pattern

Dynamic registry bootstrap methods commonly look like:

```java
public static void bootstrap(
        BootstrapContext<ConfiguredFeature<?, ?>> context) {

    ...
}
```

This method is called by your registry-set/datagen setup.

Its job is to populate a dynamic registry.

Do not confuse:

```text
bootstrap registration time
```

with:

```text
feature placement time during world generation
```

The registration code creates recipes/data.

The generated feature later executes in worlds.

---

# 5. Read generic types slowly

Example:

```java
ResourceKey<ConfiguredFeature<?, ?>>
```

Read outside-in:

```text
ResourceKey
    of
ConfiguredFeature
    whose two generic types are wildcard/unknown here
```

Example:

```java
Feature<NoneFeatureConfiguration>
```

means:

```text
Feature
using
NoneFeatureConfiguration
```

Do not panic when brackets stack.

---

# 6. Why `static` appears everywhere in registries

Example:

```java
public static final ResourceKey<...> WILLOW_KEY
```

`static` means it belongs to the class itself, not to one instance.

Registry keys are shared global definitions, so static fields are natural.

---

# 7. Why `final`

```java
public static final int MAX_SIZE = 32;
```

`final` means the reference/value cannot be reassigned.

Constants should not suddenly become:

```java
MAX_SIZE = 80;
```

during runtime.

---

# 8. Public vs private

Use the smallest access that works.

```java
public
```

other classes can access.

```java
private
```

only this class.

Helpers that only exist to support the class are often private:

```java
private boolean canStandAt(...) {
}
```

Do not make everything `public` just to silence access errors.

Understand who should own access.

---

# 9. When to make a helper method

Bad:

```java
public void tick() {
    // 400 lines
}
```

Break by meaning:

```java
public void tick() {
    updateMode();
    updateNavigation();
    updateWork();
    updateAnimations();
}
```

Then each method can be understood independently.

---

# 10. Name methods by intent

Bad:

```java
doThing()
checkStuff()
process()
run2()
```

Better:

```java
chooseNextMineTarget()
findReachableWorkPosition()
shouldReturnToStorage()
updateMiningTimeline()
```

Good names act like comments.

---

# 11. Comments should explain why

Bad:

```java
// increment x
x++;
```

Better:

```java
// Reverse every second row so excavation follows a serpentine path
boolean reverseRow =
        ((z - minZ) & 1) == 1;
```

Code says what.

Comment says why.

---

# 12. Compiler errors: read from first real error

One missing `}` can create 40 fake errors below it.

When compilation explodes:

```text
1. find first error
2. fix it
3. compile again
4. only then examine later errors
```

Do not attack all 40 simultaneously.

---

# 13. Braces debugging

Use indentation:

```java
public class Example {

    public void method() {

        if (true) {

        }
    }
}
```

IntelliJ can reformat code.

If indentation suddenly becomes weird, inspect braces.

---

# 14. Import errors

If:

```java
Cannot resolve symbol X
```

possible causes:

```text
wrong Minecraft version
wrong package
missing dependency
class renamed
mapping mismatch
typo
class only exists client-side
```

Do not randomly import the first autocomplete result with same short name.

Read full package.

---

# 15. Version mismatch warning

Minecraft modding changes APIs frequently.

A tutorial for:

```text
1.19
1.20.1
1.20.6
1.21.4
```

may be architecturally useful but syntactically wrong for your:

```text
Minecraft 1.21.1
NeoForge 21.1.x
Java 21
```

Use old tutorials to learn concepts.

Verify exact classes/signatures against your source/mappings.

---

# 16. Client-class crash

A classic dedicated-server issue happens if common/server code references a client-only class.

Avoid importing rendering/model classes into common gameplay classes.

Keep:

```text
renderer
model
client setup
```

on client side.

---

# 17. Datagen debugging

Your Gradle run has a data configuration targeting:

```text
src/generated/resources/
```

When datagen fails:

```text
read first registry/bootstrap error
check missing holder/key
check duplicate ID
check wrong registry
check circular dependency
```

Do not assume JSON is wrong if bootstrap never created it.

---

# 18. Registry debugging mental model

If:

```text
configured feature exists
placed feature lookup fails
```

ask:

```text
Was key registered?
Was registry builder given the bootstrap?
Did datagen run?
Is generated resource included?
Is name exactly the same?
```

Follow the pipeline.

---

# 19. Worldgen feature-cycle errors

Biome features have ordered generation steps.

Adding the same/reused placed features to different biomes/orders can create cycle violations.

When you see a feature-order cycle:

```text
do not just shuffle random priorities
```

Inspect:

```text
which placed features?
which generation step?
which biomes?
same feature used in multiple modifiers?
```

---

# 20. Fixed-seed testing

For worldgen:

```text
seed: fixed
coordinates: fixed
version: noted
```

Test one change at a time.

Example notebook:

```text
CHANGE:
oak count 8 → 20

SEED:
123456789

COORD:
x=500 z=-200

RESULT:
forest visibly denser

NEXT:
try 12
```

This is actual experimental development.

---

# 21. Minimal reproduction

If willow fails:

Do not debug:

```text
willow
+ custom biome
+ custom noise
+ surface rules
+ new structures
+ lily pads
```

all together.

Create a test path:

```text
one configured willow
→ one placed feature
→ add to vanilla plains
```

If that works, the tree is fine.

Then reintroduce your real biome setup.

---

# 22. Debug value exaggeration

Use extreme test values.

Examples:

```text
tree count 8 → 100
speed 0.8 → 4.0
sound volume 0.5 → 2.0
animation angle 10° → 90°
moss chance 0.1 → 1.0
```

Then restore reasonable values.

This is one of the best debugging habits for visual systems.

---

# 23. Binary search your code

If a 200-line method broke after edits:

```text
disable half
test
```

If error remains, bug is in active half.

Repeat.

This is conceptual binary search.

---

# 24. Log state transitions, not every tick

Bad:

```text
20 logs/sec/entity forever
```

Better:

```text
IDLE -> CHOOSE_BLOCK
CHOOSE_BLOCK -> NAVIGATING
NAVIGATING -> MINING
```

Log changes and unusual failures.

---

# 25. Assertions/invariants

Write down what must always be true.

Example excavation:

```text
targetBlock is inside WorkArea
workPosition is not solid
MINING implies targetBlock != null
RETURNING_STORAGE implies storage target exists
```

Then validate in development.

This prevents impossible states.

---

# 26. Null means something

Avoid blindly:

```java
if (thing != null) {
    ...
}
```

Ask:

> Why may this be null?

Example path:

```text
null path
→ navigator could not create path
```

That should become a failure reason, not silently disappear.

---

# 27. Use enums instead of booleans when states are exclusive

Bad:

```java
boolean mining;
boolean harvesting;
boolean resting;
boolean returning;
```

What happens if all four become `true`?

Better:

```java
WorkMode mode;
```

Now only one mode exists at once.

Use booleans for independent true/false facts.

---

# 28. Avoid premature custom engines

Before writing your own:

```text
pathfinder
noise generator
animation system
registry system
```

understand Minecraft's existing extension points.

Customize only the part that actually limits your design.

Your willow may need a custom trunk placer.

It probably does not need a completely new chunk format.

---

# 29. Read vanilla source as examples

Recommended classes by topic.

## Blocks

```text
SnowLayerBlock
SeaPickleBlock
CandleBlock
CakeBlock
DoorBlock
DoublePlantBlock
WaterlilyBlock
```

Look for multi-state/stacking/survival patterns.

## Trees

```text
TreeConfiguration
TrunkPlacer
StraightTrunkPlacer
UpwardsBranchingTrunkPlacer
FoliagePlacer
SpruceFoliagePlacer
CherryFoliagePlacer
```

## Biomes/worldgen

```text
OverworldBiomes
OverworldBiomeBuilder
NoiseGeneratorSettings
NoiseRouterData
DensityFunctions
SurfaceRules
```

## AI

```text
Goal
MoveToBlockGoal
RemoveBlockGoal
FollowOwnerGoal
RandomStrollGoal
PathNavigation
WalkNodeEvaluator
FlyNodeEvaluator
```

## Animation

```text
AnimationState
HierarchicalModel
FrogModel
CamelModel
WardenModel
```

---

# 30. How to compare another mod safely

Do not copy a 500-line file first.

Instead make a table:

```text
What problem is it solving?
Which Minecraft extension point?
Which data belongs to configuration?
Which algorithm is custom?
Which part is cosmetic?
Which part is performance-sensitive?
```

Then write your own small version.

This teaches you more and avoids importing architecture you do not understand.

---

# 31. Suggested weekly curriculum

You can study at any pace, but this sequence builds skills.

## Unit 1
Java braces, methods, fields, constructors.

Build:
simple custom block.

## Unit 2
BlockState properties.

Build:
1–4 variant block.

## Unit 3
Models/datagen.

Build:
four visible variants.

## Unit 4
Configured/placed features.

Build:
custom oak in vanilla forest.

## Unit 5
Custom trees.

Build:
branching test tree.

## Unit 6
Custom feature.

Build:
forest floor patch.

## Unit 7
Biome construction.

Rebuild a plains-like test biome.

## Unit 8
Climate.

Map hot/dry/wet/cold regions on paper/code.

## Unit 9
Density/noise.

Make controlled test terrain.

## Unit 10
Entity Goals.

Build:
walk-to-target NPC.

## Unit 11
State machine.

Build:
choose → walk → work → done.

## Unit 12
Mining.

Build:
mine one assigned block.

## Unit 13
WorkArea.

Build:
small 5×5×3 excavation.

## Unit 14
Smart target selection.

Add candidate scoring/recovery.

## Unit 15
Animation/sound.

Tie work timeline to model/sound.

---

# 32. "Explain this code" worksheet

For every snippet, answer:

```text
FILE:
PACKAGE:

CLASS:
extends?
implements?

FIELDS:
what do they store?

CONSTRUCTOR:
what does it initialize?

METHOD:
who calls it?
when?
server/client?
what parameters?
what return value?

SIDE EFFECT:
does it change world/entity/state?

DEPENDENCIES:
what other classes does it require?

TEST:
what value can I exaggerate?

FAILURE:
what would make it do nothing?
```

Use this worksheet when Codex generates code.

---

# 33. Example worksheet: lily pads

```text
CLASS:
LilyPadsBlock

EXTENDS:
Block

FIELD:
PADS IntegerProperty 1..4

DEFAULT:
pads=1

PLACEMENT:
same item increments state

MODEL:
blockstate JSON chooses model by pads value

COLLISION:
empty collision shape

SURVIVAL:
requires water below

ENTITY EFFECT:
slowdown based on PADS
```

Once you can produce summaries like this yourself, you are learning architecture.

---

# 34. Example worksheet: tree

```text
KEY:
meowskis:willow

FEATURE:
Feature.TREE

TRUNK PROVIDER:
willow log

TRUNK PLACER:
upward-branching

FOLIAGE PROVIDER:
willow leaves

FOLIAGE PLACER:
cherry-like

PLACED FEATURE:
controls placement

BIOME MODIFIER:
attaches to selected biome
```

---

# 35. Questions to ask yourself before asking AI

Not:

```text
"fix"
```

Ask:

```text
Which layer is wrong?
What did I expect?
What actually happened?
Does it compile?
Does registration exist?
Does an exaggerated test prove it runs?
Which state is the system in?
What is the first failure reason?
```

Then AI/Codex becomes a tool instead of a replacement for understanding.

---

# 36. References

NeoForge 1.21.1:
https://docs.neoforged.net/docs/1.21.1/

Minecraft 1.21.1 mappings:
https://mappings.dev/1.21.1/
