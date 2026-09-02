# Meowskis NeoForge 1.21.1 — MEGA GUIDE

This is the single-file edition of the full study pack.

## Sections

1. Start Here
2. Java, Blocks, Blockstates, Models, and Custom Systems
3. Worldgen, Biomes, Trees, Climate, and Terrain
4. Entities, NPC AI, Mining, Flying, and Smart Pathfinding
5. Models, Animations, and Timed Sounds
6. Architecture, Debugging, and Exercises
7. Fast Cheat Sheet
8. Your Mod Ideas → Exact System Map

> Use `Ctrl+F` to search terms such as `TrunkPlacer`, `BlockState`, `DensityFunction`, `Goal`, `PathNavigation`, `AnimationState`, `lily pads`, `swamp`, or `WorkArea`.

---



---

<!-- SOURCE SECTION: 00_START_HERE.md -->

# Meowskis NeoForge 1.21.1 — Start Here

> A study pack for learning Java + Minecraft mod architecture by working on the systems you actually want to build.

**Target:** Minecraft 1.21.1 • NeoForge 21.1.x • Java 21  
**Project snapshot used while writing:** `KanJpzx/learning`  
**Mod id:** `meowskis`  
**Java package:** `com.kanjpz.meowski`

> [!IMPORTANT]
> Codex may change your repository while you are reading this. Treat file paths and your existing classes as the *project snapshot* used to teach the concepts. If Codex renames a class, the concept is still the same.

---

## How to read this pack

Every file uses the same markers:

- 🟦 **CONCEPT** — understand this before copying code.
- 🟩 **PATTERN** — code pattern you can adapt.
- 🟨 **EXPERIMENT** — intentionally change a value and observe what happens.
- 🟥 **COMMON BUG** — a mistake beginners commonly make.
- 🟪 **ADVANCED** — useful later; you do not need to master it immediately.
- 📁 **WHERE IT GOES** — exact kind of file/package where the code belongs.
- 🔗 **PIPELINE** — how one system connects to another.

Java and JSON are placed in fenced code blocks so editors such as IntelliJ, GitHub, VS Code, Obsidian, etc. can syntax-highlight them.

---

# The most important mental model

A Minecraft mod is not "one code file".

Think of it as connected systems:

```text
REGISTRATION
    ↓
GAME OBJECT
    ↓
BEHAVIOR
    ↓
DATA / RESOURCES
    ↓
WORLD / ENTITY / CLIENT SYSTEM
```

Examples:

```text
CUSTOM BLOCK
ModBlocks registers it
    ↓
LilyPadsBlock defines behavior
    ↓
blockstate JSON chooses model
    ↓
model JSON defines shape
    ↓
texture PNG colors it
```

```text
TREE
ConfiguredFeature defines tree recipe
    ↓
PlacedFeature defines placement attempts
    ↓
BiomeModifier inserts it into biomes
    ↓
world generator places it
```

```text
CUSTOM MOB
EntityType registers it
    ↓
entity class stores server-side behavior
    ↓
Goal / Brain code decides behavior
    ↓
Navigation calculates paths
    ↓
client model renders it
    ↓
animation state moves model
    ↓
sounds/particles communicate actions
```

When something breaks, ask **which layer owns the problem**.

---

# Your recommended study order

Do not try to memorize every file immediately.

## Phase 1 — Java through Minecraft

Study:

1. classes
2. fields
3. methods
4. parameters
5. return values
6. `if`
7. loops
8. constructors
9. inheritance
10. `@Override`
11. lists/maps/sets
12. enums
13. records
14. generics
15. lambdas
16. codecs

Use `01_JAVA_BLOCKS_AND_CUSTOM_SYSTEMS.md`.

## Phase 2 — Worldgen

Study:

1. registry keys
2. configured features
3. placed features
4. biome modifiers
5. trees
6. custom features
7. biomes
8. surface rules
9. climate
10. density functions
11. noise router
12. world presets

Use `02_WORLDGEN_BIOMES_TREES_TERRAIN.md`.

## Phase 3 — NPCs and AI

Study:

1. entity state
2. Goals
3. navigation
4. movement control
5. task state machines
6. work targets
7. mining
8. safety costs
9. recovery when stuck
10. flying

Use `03_ENTITIES_AI_PATHFINDING.md`.

## Phase 4 — Models, animation, sound

Study:

1. block models
2. blockstate models
3. entity model parts
4. animation state
5. timing
6. sound events
7. particles

Use `04_MODELS_ANIMATIONS_SOUNDS.md`.

## Phase 5 — project architecture/debugging

Use `05_ARCHITECTURE_DEBUGGING_AND_EXERCISES.md`.

---

# Your project snapshot

At the time this pack was generated, your connected repository included:

```text
src/main/java/com/kanjpz/meowski/
├── Config.java
├── MeowskiClient.java
├── meowski.java
├── block/
├── datagen/
├── item/
└── worldgen/
    ├── ModBiomeModifiers.java
    ├── ModConfiguredFeatures.java
    ├── ModPlacedFeatures.java
    └── tree/
```

Your `build.gradle` targets Java 21 and includes `src/generated/resources` as a resource directory. It currently has no third-party animation library dependency.

That means this pack teaches **vanilla/NeoForge-native animation first**. Optional animation libraries are discussed separately instead of assuming one is installed.

---

# The "where may I put this?" rule

This is one of your biggest learning goals, so remember this template:

```java
public class ExampleClass {

    // ✅ CLASS BODY:
    // fields belong here

    private int value = 5;

    // constructors belong here
    public ExampleClass() {
    }

    // methods belong here
    public void doSomething() {

        // ✅ METHOD BODY:
        // statements and local variables belong here

        int localValue = 10;

        if (localValue > 5) {
            System.out.println("Large!");
        }
    }

    // another method is a SIBLING, not inside doSomething()
    public int getValue() {
        return value;
    }
}
```

🟥 Wrong:

```java
public void firstMethod() {

    public void secondMethod() {
    }
}
```

Java does not allow a named method declaration inside another method.

---

# How to study an unfamiliar class

When you open Minecraft source or another mod, do not read top-to-bottom like a book.

Use this order:

```text
1. What class does it extend?
2. What interfaces does it implement?
3. What fields does it store?
4. What does the constructor require?
5. What methods are overridden?
6. What other classes are called?
7. Which code is registration?
8. Which code is actual behavior?
9. Which code only runs client-side?
10. Which code changes world/server state?
```

Example:

```java
public class LilyPadsBlock extends Block {
```

Immediately ask:

> This is a specialized `Block`. Which vanilla `Block` methods does it override?

That question leads you to the behavior.

---

# Copying code vs understanding code

Use this four-step process whenever Codex gives you code:

```text
1. COMPILE
   Does it compile?

2. PROVE
   Exaggerate a value so you can prove the code executes.

3. EXPLAIN
   Explain every field/method in your own words.

4. MODIFY
   Change one behavior without asking Codex to rewrite it.
```

Example:

If a tree uses:

```java
CountPlacement.of(6)
```

test:

```java
CountPlacement.of(50)
```

If the world becomes absurdly forested, you have proven that this setting controls placement attempts.

Then tune it down.

---

# Important distinction: exact code vs teaching math

A block like this:

```java
double terrainHeight =
        72
        + broadNoise * 15
        + localNoise * 4;
```

can teach terrain concepts.

It is **not automatically code you paste into vanilla `NoiseRouterData`**.

This pack labels such sections:

> 🟦 CONCEPTUAL MATH — not direct paste code.

Meanwhile a class signature such as:

```java
public class MyGoal extends Goal
```

is an actual Java/Minecraft architectural pattern.

Always read the label above an example.

---

# Useful source references

Official/reference material used while building this pack:

- NeoForge 1.21.1 docs: https://docs.neoforged.net/docs/1.21.1/
- NeoForge biome modifiers: https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/
- NeoForge registries: https://docs.neoforged.net/docs/1.21.1/concepts/registries/
- NeoForge models: https://docs.neoforged.net/docs/1.21.1/resources/client/models/
- Minecraft 1.21.1 mappings browser: https://mappings.dev/1.21.1/

In IntelliJ, Minecraft source itself is also one of your best textbooks.

---

# The final rule

Do not measure progress by:

> "Can I write a world generator from memory?"

Measure it by:

> "Can I open unfamiliar code, identify its layers, modify a value, predict what changes, and debug the result?"

That is real programming progress.


---

<!-- SOURCE SECTION: 01_JAVA_BLOCKS_AND_CUSTOM_SYSTEMS.md -->

# Java, Blocks, Blockstates, Models, and Custom Systems

This file teaches the code structure you need to create **custom blocks, combine multiple visual/behavioral variations into one registered block, build multiblock systems, and design reusable custom systems**.

---

# 1. Java structure in Minecraft code

## 🟦 Class

A class defines a type.

```java
public class LilyPadsBlock extends Block {
}
```

This says:

- create a type named `LilyPadsBlock`
- it inherits behavior from Minecraft's `Block`

`extends Block` is **inheritance**.

You do not need to rewrite everything a block can do. You override only the behaviors you want to change.

---

## 🟦 Field

```java
private int energy;
```

A field belongs to an object/class and lasts longer than one method call.

Common Minecraft fields:

```java
public static final IntegerProperty PADS = IntegerProperty.create("pads", 1, 4);
private BlockPos workTarget;
private int workTicks;
private boolean working;
```

---

## 🟦 Local variable

```java
public void doWork() {
    int blocksMinedThisTick = 1;
}
```

`blocksMinedThisTick` exists only while `doWork()` is executing.

---

## 🟦 Method

```java
public int getEnergy() {
    return energy;
}
```

Break it apart:

```text
public      access
int         return type
getEnergy   method name
()          parameters
```

---

## 🟦 Constructor

```java
public LilyPadsBlock(BlockBehaviour.Properties properties) {
    super(properties);
}
```

A constructor initializes a newly created object.

`super(properties)` calls the parent `Block` constructor.

---

## 🟦 Override

```java
@Override
public boolean canSurvive(...) {
    ...
}
```

This means:

> `Block` already defines this behavior. Use my implementation for this subclass.

---

# 2. One registered block can represent many variants

This is what you mean when you talk about "combining multiple blocks into one".

There are **different kinds of combining**, and they should not be confused.

## Type A — multiple states of one block

Examples:

```text
lily pads:
pads=1
pads=2
pads=3
pads=4
```

```text
machine:
facing=north/south/east/west
active=true/false
```

```text
plant:
age=0..7
```

This uses **BlockState properties**.

## Type B — multipart visual model

One blockstate can render multiple model parts together.

Useful for:

- connected pipes
- cables
- fences
- machine attachments
- stems/branches
- panels that appear only in certain states

This uses a **multipart blockstate JSON**.

## Type C — one logical structure made from multiple world blocks

Example:

```text
[controller][body][body]
            [body]
```

This is a **multiblock structure**.

It is not one BlockState. It is multiple positions coordinated by code.

---

# 3. BlockState properties

Common property classes include:

```java
BooleanProperty
IntegerProperty
EnumProperty
DirectionProperty
```

Example:

```java
public static final BooleanProperty ACTIVE =
        BooleanProperty.create("active");

public static final IntegerProperty PADS =
        IntegerProperty.create("pads", 1, 4);
```

Then register them:

```java
@Override
protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder) {

    builder.add(ACTIVE, PADS);
}
```

And define defaults:

```java
this.registerDefaultState(
        this.stateDefinition.any()
                .setValue(ACTIVE, false)
                .setValue(PADS, 1)
);
```

---

# 4. Read and change state

Read:

```java
int count = state.getValue(PADS);
boolean active = state.getValue(ACTIVE);
```

Change:

```java
BlockState newState = state.setValue(PADS, 3);
```

Important:

`BlockState` behaves like an immutable value.

You usually do not mutate the existing object. `setValue` returns another state.

To put it into the world:

```java
level.setBlock(pos, newState, 3);
```

---

# 5. Your stacked lily-pad pattern

A clean stacking pattern:

```java
@Override
public boolean canBeReplaced(
        BlockState state,
        BlockPlaceContext context) {

    boolean stackingSameItem =
            !context.isSecondaryUseActive()
            && context.getItemInHand().is(this.asItem())
            && state.getValue(PADS) < 4;

    return stackingSameItem
            || super.canBeReplaced(state, context);
}
```

Why create `stackingSameItem`?

Because:

```java
A && B && C || D
```

is harder to read correctly than:

```java
boolean stackingSameItem = A && B && C;
return stackingSameItem || D;
```

Readable code is easier to debug.

---

## Placement state

```java
@Override
public BlockState getStateForPlacement(BlockPlaceContext context) {

    BlockState existing =
            context.getLevel()
                    .getBlockState(context.getClickedPos());

    if (existing.is(this)) {
        int current = existing.getValue(PADS);

        return existing.setValue(
                PADS,
                Math.min(4, current + 1)
        );
    }

    return this.defaultBlockState();
}
```

Flow:

```text
player places item
     ↓
what block is already there?
     ↓
same lily-pad block?
     ↓ yes
read PADS
     ↓
+1, capped at 4
     ↓
return new BlockState
```

---

# 6. Different model for each state

📁 Typical location:

```text
src/main/resources/assets/meowskis/blockstates/lily_pads.json
```

```json
{
  "variants": {
    "pads=1": { "model": "meowskis:block/lily_pads_1" },
    "pads=2": { "model": "meowskis:block/lily_pads_2" },
    "pads=3": { "model": "meowskis:block/lily_pads_3" },
    "pads=4": { "model": "meowskis:block/lily_pads_4" }
  }
}
```

Models:

```text
assets/meowskis/models/block/lily_pads_1.json
assets/meowskis/models/block/lily_pads_2.json
assets/meowskis/models/block/lily_pads_3.json
assets/meowskis/models/block/lily_pads_4.json
```

Textures:

```text
assets/meowskis/textures/block/lily_pad.png
```

One Java block.

Four states.

Four models.

Possibly one shared texture.

---

# 7. What a block model actually contains

Minecraft block models are JSON cuboids.

Simplified:

```json
{
  "textures": {
    "pad": "meowskis:block/lily_pad"
  },
  "elements": [
    {
      "from": [0, 0, 0],
      "to": [16, 1, 16],
      "faces": {
        "up": {
          "texture": "#pad"
        }
      }
    }
  ]
}
```

Blockbench helps author this JSON visually.

The Java class does **not** normally contain the cuboid model geometry.

Remember:

```text
Java              behavior
BlockState JSON    choose model
Model JSON         geometry
PNG                texture
```

---

# 8. Multipart models

Suppose a fictional pipe block has:

```text
north=true
south=false
east=true
west=false
```

You could render a center model plus conditional connector pieces.

Conceptual blockstate JSON:

```json
{
  "multipart": [
    {
      "apply": {
        "model": "meowskis:block/pipe_center"
      }
    },
    {
      "when": {
        "north": "true"
      },
      "apply": {
        "model": "meowskis:block/pipe_side"
      }
    }
  ]
}
```

NeoForge's model/datagen system also provides multipart builders.

This is ideal when a single registered block visually combines pieces depending on state.

---

# 9. When BlockState is NOT enough

Do not try to store huge dynamic information in blockstate properties.

Bad idea:

```text
inventory contents
owner UUID
10,000 energy values
custom selected work zone
a long list of coordinates
```

Blockstates are intended for a relatively small finite set of states.

For large dynamic state, use a **BlockEntity**.

---

# 10. BlockEntity mental model

```text
Block
    behavior / shape / placement rules

BlockState
    compact world-visible state

BlockEntity
    complex data tied to one block position
```

Example machine:

```java
public class WorkerControllerBlockEntity extends BlockEntity {

    private int energy;
    private BlockPos target;
    private final List<BlockPos> queuedBlocks = new ArrayList<>();

    ...
}
```

Use block entity data for things such as:

- inventory
- energy
- timers
- owner
- configuration
- work queue
- selected area

---

# 11. Multiblock structures

Suppose you want a 3×2 machine made from world blocks.

Do not register one absurd 48×32×16 model and assume Minecraft treats it like one cell.

A reliable pattern:

```text
CONTROLLER BLOCK
stores:
- structure origin
- orientation
- machine state

PART BLOCKS
store:
- controller position
- role/type
```

When formed:

```java
for (BlockPos partPos : expectedPositions) {
    if (!isCorrectPart(level, partPos)) {
        return false;
    }
}

formMachine(level, controllerPos);
```

When broken:

```java
invalidateMachine(level, controllerPos);
```

---

# 12. Build reusable custom systems instead of giant classes

🟥 Beginner trap:

```java
public class WallEEntity {
    // inventory
    // pathfinder
    // excavation
    // harvesting
    // animation
    // sounds
    // storage
    // owner commands
    // 4,000 lines
}
```

Better:

```text
WallEEntity
├── WorkMode
├── ExcavationController
├── HarvestController
├── WorkArea
├── StorageTarget
├── NavigationPolicy
└── animation/sound state
```

The entity coordinates the systems.

Each subsystem does one job.

---

# 13. Example: custom WorkArea object

📁 A reasonable package:

```text
src/main/java/com/kanjpz/meowski/util/WorkArea.java
```

```java
public record WorkArea(BlockPos min, BlockPos max) {

    public int width() {
        return max.getX() - min.getX() + 1;
    }

    public int height() {
        return max.getY() - min.getY() + 1;
    }

    public int depth() {
        return max.getZ() - min.getZ() + 1;
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= min.getX()
                && pos.getX() <= max.getX()
                && pos.getY() >= min.getY()
                && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ()
                && pos.getZ() <= max.getZ();
    }
}
```

Why a `record`?

This object is mostly data:

```text
min position
max position
```

and some calculations based on that data.

Records automatically provide useful data-object behavior.

---

# 14. Combine custom systems through composition

Suppose:

```java
public class ExcavationController {

    private WorkArea area;

    public void tick(WorkerEntity worker) {
        ...
    }
}
```

Entity:

```java
public class WorkerEntity extends PathfinderMob {

    private final ExcavationController excavation =
            new ExcavationController();

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            excavation.tick(this);
        }
    }
}
```

This is **composition**:

> WorkerEntity HAS an ExcavationController.

Contrast inheritance:

```java
public class WorkerEntity extends PathfinderMob
```

> WorkerEntity IS a PathfinderMob.

Learn this difference.

---

# 15. Interfaces for interchangeable behavior

You may eventually have:

```java
public interface WorkTask {

    boolean canStart(WorkerEntity worker);

    void start(WorkerEntity worker);

    void tick(WorkerEntity worker);

    boolean isFinished(WorkerEntity worker);

    void stop(WorkerEntity worker);
}
```

Then:

```java
public class MineTask implements WorkTask { ... }
public class HarvestTask implements WorkTask { ... }
public class ReturnStorageTask implements WorkTask { ... }
```

Now your worker can run different task types through the same interface.

That is one way to build your own AI/task framework above Minecraft Goals.

---

# 16. Enum for modes

```java
public enum WorkMode {
    RESTING,
    ASSISTING,
    EXCAVATING,
    HARVESTING,
    RETURNING_TO_STORAGE
}
```

This is far better than:

```java
int mode = 3;
```

because:

```java
WorkMode.EXCAVATING
```

explains itself.

---

# 17. State machine

A useful custom-system pattern:

```java
switch (workMode) {

    case RESTING -> tickResting();

    case ASSISTING -> tickAssisting();

    case EXCAVATING -> tickExcavating();

    case HARVESTING -> tickHarvesting();

    case RETURNING_TO_STORAGE -> tickReturning();
}
```

This is a **state machine**.

Each state owns its behavior.

Transitions are explicit:

```java
if (inventoryIsFull()) {
    workMode = WorkMode.RETURNING_TO_STORAGE;
}
```

---

# 18. Client vs server

One of the most important Minecraft rules.

## Server should own

- blocks breaking
- inventories
- AI decisions
- pathfinding targets
- damage
- actual entity position authority
- worldgen
- saved data

## Client should own/display

- rendering
- model animation
- HUD
- visual interpolation
- local particles in some cases

🟥 Do not let client-only rendering code decide whether a block is actually mined.

---

# 19. Ticks

Minecraft runs simulation in ticks, usually 20 ticks per second.

So:

```java
private int workTicks;

public void tickWork() {

    workTicks++;

    if (workTicks >= 20) {
        // roughly one second of ticks
        workTicks = 0;
    }
}
```

But do not scatter magic numbers everywhere.

Better:

```java
private static final int TICKS_PER_SECOND = 20;
private static final int MINE_SOUND_DELAY = 8;
```

---

# 20. Timers as reusable system

```java
public class TickTimer {

    private int ticks;

    public void reset() {
        ticks = 0;
    }

    public void tick() {
        ticks++;
    }

    public boolean reached(int target) {
        return ticks >= target;
    }
}
```

You can reuse it for:

- mining
- animations
- sounds
- cooldowns
- AI waits

---

# 21. Tags instead of hard-coded block lists

Bad:

```java
if (state.is(Blocks.OAK_LOG)
        || state.is(Blocks.BIRCH_LOG)
        || state.is(Blocks.SPRUCE_LOG)) {
```

Better:

```java
if (state.is(BlockTags.LOGS)) {
```

For your own category:

```text
data/meowskis/tags/block/worker_mineable.json
```

Then code can ask:

```java
state.is(ModTags.Blocks.WORKER_MINEABLE)
```

Data controls membership.

Code controls behavior.

---

# 22. A decision tree for "how should I implement this?"

```text
Does it only change appearance?
    → model / texture / blockstate JSON

Does it have a small finite state?
    → BlockState property

Does it store lots of dynamic data?
    → BlockEntity or Entity saved data

Is it a reusable operation?
    → helper class / method

Are there several interchangeable implementations?
    → interface + classes

Is it one object with several modes?
    → enum + state machine

Is it world placement?
    → feature / structure / worldgen registry

Is it creature behavior?
    → Goal / Brain / custom task controller

Is it path calculation?
    → Navigation / PathFinder / path costs
```

---

# 23. Exercises

## Exercise A

Add:

```java
BooleanProperty FLOWERING
```

to a test plant and make:

```text
flowering=false
flowering=true
```

choose different models.

## Exercise B

Change lily pads from 1–4 to 1–6.

Before coding, list every affected layer:

```text
Java IntegerProperty
placement cap
blockstate JSON
models
loot behavior
worldgen weights
```

## Exercise C

Create:

```java
public enum MachineMode {
    IDLE,
    WORKING,
    JAMMED
}
```

Then create a switch that prints a different message for each state.

## Exercise D

Write a `WorkArea.volume()` method.

Formula:

```text
width × height × depth
```

---

# 24. References

NeoForge blocks:
https://docs.neoforged.net/docs/1.21.1/blocks/

NeoForge models:
https://docs.neoforged.net/docs/1.21.1/resources/client/models/

NeoForge model datagen:
https://docs.neoforged.net/docs/1.21.1/resources/client/models/datagen/

NeoForge registries:
https://docs.neoforged.net/docs/1.21.1/concepts/registries/


---

<!-- SOURCE SECTION: 02_WORLDGEN_BIOMES_TREES_TERRAIN.md -->

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


---

<!-- SOURCE SECTION: 03_ENTITIES_AI_PATHFINDING.md -->

# Entities, NPC AI, Mining, Flying, and Smart Pathfinding

This file focuses on the kind of worker NPC you have described before: a mob that can wander, follow commands, excavate areas, mine blocks, return to storage, recover when stuck, and potentially fly/float.

The key lesson is that **AI is several layers**.

```text
DECISION
"What should I do?"
        ↓
TASK / GOAL
"What action am I currently performing?"
        ↓
TARGET SELECTION
"Which block/position/entity matters?"
        ↓
PATH PLANNING
"How can I reach it?"
        ↓
MOVEMENT CONTROL
"How do I physically move each tick?"
        ↓
ANIMATION / SOUND
"How do I communicate this action visually?"
```

Do not put all of those into one `tick()` method.

---

# 1. Vanilla Mob architecture

A `Mob`/`PathfinderMob` already owns useful systems such as:

```text
GoalSelector
TargetSelector
PathNavigation
MoveControl
LookControl
JumpControl
```

Minecraft 1.21.1 `Goal` exposes the important lifecycle methods:

```java
canUse()
canContinueToUse()
start()
stop()
tick()
```

That means a goal is basically a small stateful behavior.

---

# 2. Goal mental model

A custom goal:

```java
public class GoToWorkGoal extends Goal {

    private final WorkerEntity worker;

    public GoToWorkGoal(WorkerEntity worker) {
        this.worker = worker;
    }

    @Override
    public boolean canUse() {
        return worker.hasWorkTarget();
    }

    @Override
    public void start() {
        BlockPos target = worker.getWorkTarget();

        worker.getNavigation().moveTo(
                target.getX() + 0.5,
                target.getY(),
                target.getZ() + 0.5,
                1.0
        );
    }

    @Override
    public boolean canContinueToUse() {
        return !worker.getNavigation().isDone();
    }

    @Override
    public void tick() {
        // optional ongoing logic
    }

    @Override
    public void stop() {
        worker.getNavigation().stop();
    }
}
```

The exact method overloads you use can vary depending on target/path type, but the lifecycle is the important part.

---

# 3. Registering goals

Inside the entity class, a typical pattern is:

```java
@Override
protected void registerGoals() {

    this.goalSelector.addGoal(
            0,
            new FloatGoal(this)
    );

    this.goalSelector.addGoal(
            2,
            new GoToWorkGoal(this)
    );

    this.goalSelector.addGoal(
            8,
            new RandomStrollGoal(this, 0.8D)
    );

    this.goalSelector.addGoal(
            9,
            new RandomLookAroundGoal(this)
    );
}
```

Smaller priority number = generally more important.

So:

```text
0 emergency float
2 work
8 wander
9 look around
```

A work goal can interrupt wandering.

---

# 4. Goals need flags

Goals can declare what controls they use.

Example:

```java
public GoToWorkGoal(WorkerEntity worker) {
    this.worker = worker;

    this.setFlags(
            EnumSet.of(
                    Goal.Flag.MOVE,
                    Goal.Flag.LOOK
            )
    );
}
```

Why?

Two goals that both need movement should not fight each other every tick.

Without thinking about flags, you can accidentally create:

```text
FollowOwnerGoal says MOVE EAST
RandomStrollGoal says MOVE WEST
MineGoal says STOP
```

Goal controls help the selector coordinate this.

---

# 5. High-level mode vs low-level goal

For your worker, I recommend a high-level mode:

```java
public enum WorkMode {
    RESTING,
    ASSISTING,
    EXCAVATING,
    HARVESTING,
    RETURNING_TO_STORAGE
}
```

and smaller behaviors/goals underneath.

Example:

```text
EXCAVATING
├── choose next work block
├── navigate to work position
├── face target
├── mine
├── collect item
└── choose next block
```

The mode is not the path.

The mode says what the NPC is trying to accomplish.

---

# 6. Task state machine

For excavation, a second enum is useful:

```java
public enum ExcavationState {
    IDLE,
    CHOOSE_BLOCK,
    CHOOSE_WORK_POSITION,
    NAVIGATING,
    MINING,
    RECOVERING,
    RETURNING_STORAGE
}
```

Then:

```java
switch (excavationState) {

    case CHOOSE_BLOCK ->
            chooseNextBlock();

    case CHOOSE_WORK_POSITION ->
            chooseWorkPosition();

    case NAVIGATING ->
            tickNavigation();

    case MINING ->
            tickMining();

    case RECOVERING ->
            recoverFromFailure();

    case RETURNING_STORAGE ->
            returnToStorage();

    default -> {
    }
}
```

This is much easier to debug than one huge nest of `if` statements.

---

# 7. Store the current work data

Useful fields:

```java
private BlockPos targetBlock;
private BlockPos workPosition;
private int mineTicks;
private int stuckTicks;
private int failedPathAttempts;
```

Then your debug log can say:

```text
state=NAVIGATING
targetBlock=(100,64,200)
workPosition=(99,64,200)
failedPathAttempts=2
```

That is far better than:

```text
"AI failed"
```

---

# 8. Excavation area

Use a dedicated object:

```java
public record WorkArea(
        BlockPos min,
        BlockPos max) {
}
```

For a maximum 32×32×32 area:

```java
public boolean isWithinLimit() {
    return width() <= 32
            && height() <= 32
            && depth() <= 32;
}
```

Do not scatter `32` everywhere.

```java
public static final int MAX_WORK_SIZE = 32;
```

---

# 9. Top-down layer order

Your desired behavior is top-down.

So first identify:

```text
max Y
```

and work downward.

A simple ordering comparator:

```java
Comparator<BlockPos> topDown =
        Comparator
                .comparingInt(BlockPos::getY)
                .reversed()
                .thenComparingInt(BlockPos::getZ)
                .thenComparingInt(BlockPos::getX);
```

This is a first version.

Later use a serpentine/zigzag ordering so it does not constantly cross the entire layer.

---

# 10. Zigzag/serpentine layer traversal

Suppose a layer is:

```text
→ → → → →
        ↓
← ← ← ← ←
↓
→ → → → →
```

For each Z row:

```java
boolean reverse =
        ((z - minZ) & 1) == 1;
```

If not reversed:

```text
x = minX → maxX
```

If reversed:

```text
x = maxX → minX
```

This reduces unnecessary travel.

---

# 11. Do not pre-store every block if you don't need to

32×32×32 = 32,768 positions.

That is not enormous by itself, but avoid creating expensive giant path data for everything at once.

Better:

```text
current layer
    ↓
current row
    ↓
next candidate
```

or use a lazy iterator/state machine.

Store only what you need.

---

# 12. Choosing a mine target is different from choosing a work position

Target block:

```text
the block that should be broken
```

Work position:

```text
a reachable adjacent place the mob can stand while breaking it
```

For a target:

```text
[T]
```

candidate work positions might be:

```text
 . . .
 . T .
 . . .
```

where each `.` is evaluated.

This distinction directly prevents many "unreachable target" bugs.

---

# 13. Candidate work positions

Example:

```java
private static final Direction[] HORIZONTAL = {
        Direction.NORTH,
        Direction.SOUTH,
        Direction.EAST,
        Direction.WEST
};
```

Then:

```java
List<BlockPos> candidates =
        new ArrayList<>();

for (Direction direction : HORIZONTAL) {
    BlockPos candidate =
            target.relative(direction);

    if (canStandAt(candidate)) {
        candidates.add(candidate);
    }
}
```

You can also evaluate positions above/below depending on mining reach.

---

# 14. `canStandAt` should be explicit

Conceptual:

```java
private boolean canStandAt(BlockPos pos) {

    BlockState feet =
            level().getBlockState(pos);

    BlockState head =
            level().getBlockState(pos.above());

    BlockState floor =
            level().getBlockState(pos.below());

    return feet.getCollisionShape(
                    level(),
                    pos
            ).isEmpty()
            && head.getCollisionShape(
                    level(),
                    pos.above()
            ).isEmpty()
            && !floor.getCollisionShape(
                    level(),
                    pos.below()
            ).isEmpty();
}
```

This is simplified.

Real entity dimensions, fluids, partial blocks, hazards and path node types matter.

But write your assumptions down instead of saying only:

```java
state.isAir()
```

---

# 15. Ask navigation whether the candidate is reachable

Do not assume a geometrically free spot is path-reachable.

Concept:

```java
Path path =
        worker.getNavigation()
                .createPath(candidate, 0);

if (path != null && path.canReach()) {
    ...
}
```

Method names/overloads should be checked against your exact mappings when implementing.

The architecture is:

```text
candidate position
    ↓
basic safety check
    ↓
ask navigation for path
    ↓
score reachable path
```

---

# 16. Score candidates

Instead of "first valid position wins", assign a score.

Example:

```java
double score = 0.0;

score += distanceToCandidate * 1.0;

if (candidateIsInWater) {
    score += 10.0;
}

if (candidateIsNearDrop) {
    score += 30.0;
}

if (candidateRequiresLongDetour) {
    score += 20.0;
}
```

Lower score wins.

This is how you start making AI **prefer** good paths instead of only distinguishing valid/invalid.

---

# 17. Separate work-target cost from pathfinding cost

This is crucial.

You can improve intelligence without rewriting A*.

Layer 1:

```text
Which work position should I attempt?
```

Layer 2:

```text
How does vanilla navigation reach that position?
```

If your NPC constantly chooses terrible targets, fix target selection first.

Do not immediately rewrite Minecraft's `PathFinder`.

---

# 18. Pathfinding basics

Minecraft pathfinding is graph search.

Very simplified:

```text
node = possible position
edge = possible movement between positions
cost = how expensive movement is
heuristic = estimate of distance to target
```

A* roughly favors:

```text
cost_so_far + estimated_remaining_cost
```

This lets it find efficient paths without exploring the entire world.

---

# 19. Smart path costs

For your worker, path preferences can include:

```text
normal floor          low cost
water                 medium/high cost
danger block          very high cost
large drop            invalid
lava                  invalid
cactus/fire           high/invalid
own staircase exit    low cost
known dead end        high cost
```

Do not hard-code everything in one `if`.

Create a policy:

```java
public class NavigationPolicy {

    public float costFor(
            WorkerEntity worker,
            BlockPos pos) {

        ...
    }
}
```

Now the rule is reusable/testable.

---

# 20. Path node malus

Minecraft mobs already have pathfinding malus/cost concepts for node types.

Before writing a custom A* implementation, inspect:

```text
PathType
PathfindingContext
NodeEvaluator
WalkNodeEvaluator
FlyNodeEvaluator
PathNavigation
```

You may be able to teach the existing evaluator that a node is undesirable.

---

# 21. Stuck detection

Minecraft's `PathNavigation` already contains stuck-detection concepts, but your high-level task still needs recovery logic.

Track progress:

```java
private Vec3 lastProgressPosition;
private int noProgressTicks;
```

Every interval:

```java
double moved =
        worker.position()
                .distanceTo(lastProgressPosition);

if (moved < 0.25D) {
    noProgressTicks += CHECK_INTERVAL;
} else {
    noProgressTicks = 0;
    lastProgressPosition =
            worker.position();
}
```

Then:

```java
if (noProgressTicks > 100) {
    enterRecovery();
}
```

---

# 22. Recovery stages

Do not instantly teleport.

Use escalation:

```text
1. recalculate same path
2. choose different adjacent work position
3. choose different work block
4. back away / clear local obstruction if allowed
5. construct a path/staircase if excavation rules allow
6. controlled teleport only as last resort, if your design permits
```

Track failure reasons.

---

# 23. Failure reason enum

```java
public enum PathFailureReason {
    NONE,
    NO_CANDIDATE_POSITION,
    NO_PATH,
    PATH_BECAME_BLOCKED,
    STUCK,
    DANGEROUS_ROUTE,
    TARGET_CHANGED
}
```

Logs become:

```text
RECOVERY: reason=NO_PATH target=(...)
```

instead of:

```text
unreachable
```

This is extremely useful.

---

# 24. Mining is a timed action

Do not break the block immediately when the mob reaches it if you want understandable gameplay.

State:

```text
NAVIGATING
→ MINING
```

Mining:

```java
mineTicks++;

if (mineTicks == 1) {
    startMiningAnimation();
}

if (mineTicks == 6) {
    playMiningSound();
}

if (mineTicks >= requiredMineTicks) {
    finishBreakingBlock();
}
```

---

# 25. Determine mining duration

You can base it on:

```text
block hardness
worker tool
worker upgrade
special blacklist/whitelist
```

Conceptual:

```java
float destroySpeed =
        state.getDestroySpeed(
                level(),
                targetBlock
        );

int requiredTicks =
        Math.max(
                4,
                Math.round(
                        destroySpeed * 10.0F
                )
        );
```

This is a custom gameplay formula, not vanilla player mining math.

The point is: centralize the formula.

---

# 26. Server-authoritative block breaking

Only actually alter the world on server.

Pattern:

```java
if (!level().isClientSide) {

    level().destroyBlock(
            targetBlock,
            true,
            worker
    );
}
```

Exact loot/ownership behavior can be customized.

Do not let rendering code delete blocks.

---

# 27. Respect block restrictions

Before mining:

```text
is target inside selected WorkArea?
is block allowed?
is block unbreakable?
is it protected by your own rule?
will breaking it strand the mob?
```

Use tags:

```text
#meowskis:worker_mineable
#meowskis:worker_never_mine
```

A rule system is better than a massive Java list.

---

# 28. Safety before breaking

This is where your AI can be smarter than a naive mining bot.

Before breaking target, inspect consequences:

```text
lava behind block?
water flood?
unsupported falling block?
drop under worker?
would this destroy its only exit?
```

Example:

```java
BlockPos behind =
        targetBlock.relative(miningDirection);

if (level().getFluidState(behind)
        .is(FluidTags.LAVA)) {

    rejectTarget();
}
```

---

# 29. Maintaining an escape route

For excavation:

```text
work volume
+
access route/staircase
```

should be separate concepts.

Store reserved positions:

```java
Set<BlockPos> reservedAccessPath;
```

Mining target selection skips those unless deliberately rebuilding/removing the route later.

This helps prevent the worker from mining away its own staircase.

---

# 30. Staircase generation concept

If worker begins in a cave below the top layer:

```text
determine nearest accessible edge/corner
    ↓
choose staircase direction
    ↓
reserve staircase cells
    ↓
mine upward one level every N horizontal steps
    ↓
reach top work layer
```

For a 1-block-per-step staircase:

```text
horizontal step +1
vertical step +1
```

But entity height means you must reserve headroom as well.

---

# 31. Work in planning windows

You previously liked the idea of planning in smaller windows instead of solving an entire giant job repeatedly.

Example:

```text
selected area: 32×32×32

planner:
only reason deeply about nearby ~16-block window
```

Benefits:

- less expensive re-planning
- dynamic obstacle response
- easier recovery
- smaller data structures

The full WorkArea remains known, but the active planner focuses locally.

---

# 32. Owner radius

If the worker should stay within 16 blocks of owner when idle/assisting:

```java
private static final double OWNER_RADIUS = 16.0D;
```

Logic:

```java
if (distanceToOwner > OWNER_RADIUS) {
    returnToOwner();
}
```

Do not constantly call navigation every tick.

Use cooldown:

```java
if (--repathCooldown <= 0) {
    repathCooldown = 20;
    updateOwnerPath();
}
```

---

# 33. Flying mobs

Ground navigation and flying navigation are different problems.

A flying mob may override/create:

```text
FlyingPathNavigation
FlyingMoveControl
```

Aerial movement has 3D candidate positions.

Instead of:

```text
north/south/east/west on floor
```

it reasons about:

```text
x
y
z
```

---

# 34. Floating/hover movement

For a hovering machine, you may not want gravity-driven walking.

A move controller can gradually steer velocity/rotation toward a target.

Conceptual:

```java
Vec3 direction =
        target.subtract(worker.position());

Vec3 desiredVelocity =
        direction.normalize()
                .scale(speed);

worker.setDeltaMovement(
        worker.getDeltaMovement()
                .lerp(
                        desiredVelocity,
                        0.15D
                )
);
```

This is movement-control math, not path search.

Path search decides target nodes.

Move control physically steers toward the next node.

---

# 35. Smooth turning

Do not snap rotation instantly.

Concept:

```java
float desiredYaw =
        (float)(
                Mth.atan2(
                        direction.z,
                        direction.x
                ) * Mth.RAD_TO_DEG
        ) - 90.0F;
```

Then rotate toward it gradually.

This is why move control and animation matter even if pathfinding is perfect.

---

# 36. Flying safety

Air pathfinding still needs costs.

Avoid:

```text
lava
cactus
tight gaps
solid collision
dangerous projectiles if you model them
too-close-to-ground if desired
```

For large models, account for bounding box dimensions.

A node that fits a bat may not fit your machine.

---

# 37. Looking at work targets

Separate body movement from look control.

```java
worker.getLookControl()
        .setLookAt(
                targetX,
                targetY,
                targetZ,
                yawSpeed,
                pitchSpeed
        );
```

This lets the NPC face a block while mining.

---

# 38. Animation state should follow server/gameplay state

Do not let animation decide gameplay.

Correct:

```text
gameplay says MINING
    ↓
entity syncs mining state
    ↓
client starts mining animation
```

Wrong:

```text
animation reached frame 14
    ↓
client decides block should break
```

Server should decide actual action completion.

---

# 39. Synchronizing states

You can synchronize compact state such as:

```text
working?
work mode?
animation id?
target direction?
```

through entity data/network state.

Then the client can render correctly.

Do not network an entire 32×32×32 work plan every tick.

---

# 40. Timed sound during mining

Server/gameplay counter:

```java
if (mineTicks == 6) {
    worker.playSound(
            ModSounds.WORKER_HIT.get(),
            0.8F,
            1.0F
    );
}
```

For repeating hits:

```java
if (mineTicks % 8 == 0) {
    playHitSound();
}
```

This guarantees sound timing is connected to the actual action.

---

# 41. Event/timeline approach

For complex actions, create a timeline:

```text
tick 0   animation start
tick 5   servo sound
tick 8   first mining impact
tick 16  second impact
tick 24  block breaks
tick 25  pickup sound
```

Represent timing constants:

```java
private static final int SERVO_TICK = 5;
private static final int IMPACT_A_TICK = 8;
private static final int IMPACT_B_TICK = 16;
private static final int BREAK_TICK = 24;
```

Avoid unexplained magic numbers.

---

# 42. Inventory full behavior

State transition:

```text
MINING
    ↓ inventory full
RETURNING_TO_STORAGE
    ↓ unloaded
CHOOSE_BLOCK
```

Do not continue choosing mining targets while storage return is active.

That is why state machines matter.

---

# 43. Storage target

Store:

```java
private BlockPos selectedStorage;
```

Before pathing:

```text
is chunk loaded?
does block still exist?
is it still a valid container?
can worker interact with it?
```

If not:

```text
clear target
→ fallback behavior
```

AI should expect world conditions to change.

---

# 44. Replanning

Never assume a path remains valid forever.

Replan when:

```text
target changed
path blocked
worker stuck
door/world changed
target removed
mode changed
owner moved far enough
```

But do not recompute expensive paths every tick.

Use cooldowns/events.

---

# 45. Goal vs Brain AI

Minecraft has both Goal-based mob AI and Brain/Behavior-based AI.

For your first custom worker:

```text
Goal + your own task state machine
```

is easier to reason about.

Brain AI becomes useful for richer memory/sensor-driven behavior.

Do not adopt it just because it sounds "smarter".

Smartness comes from architecture and decision quality, not class name.

---

# 46. Sensors/memory concept

A more advanced architecture:

```text
SENSORS
what do I know?

MEMORY
what did I remember?

DECISION
what should I do?

BEHAVIOR
perform it
```

Possible worker memories:

```text
owner
storage
work area
current target
last failed target
danger positions
last progress position
```

---

# 47. Cache failed targets

If a block was proven unreachable:

```java
Map<BlockPos, Long> temporarilyUnreachable;
```

Do not retry the exact same block 20 times per second.

Expire entries later:

```text
failed at tick 1000
retry after tick 1200
```

because the world may change.

---

# 48. Debug visualization

For development, visualize:

```text
selected work area
current target block
candidate stand positions
chosen work position
current path
reserved staircase
danger nodes
```

Use particles or temporary debug rendering.

Example color scheme:

```text
blue   area boundary
green  chosen path
yellow candidate
red    rejected/danger
purple reserved access route
```

Debug visuals make AI bugs understandable.

---

# 49. Logging format

Use structured logs:

```text
[WORKER]
mode=EXCAVATING
state=CHOOSE_WORK_POSITION
target=(10,70,-4)
candidates=4
reachable=2
chosen=(9,70,-4)
```

Failure:

```text
[WORKER]
state=RECOVERING
reason=NO_PATH
target=(10,70,-4)
attempt=3
```

---

# 50. Do not run expensive scans every tick

Bad:

```java
for every tick:
    scan 32×32×32
    calculate paths to 30,000 blocks
```

Better:

```text
when job assigned:
    validate area

when target needed:
    inspect next small candidate set

when route invalid:
    replan

otherwise:
    continue current task
```

AI performance mostly comes from not repeating unnecessary work.

---

# 51. Exercises

## Exercise A — Goal lifecycle

Write on paper what each does:

```text
canUse
start
tick
canContinueToUse
stop
```

Then implement a goal that walks to one fixed position.

## Exercise B — State machine

Create:

```java
enum TestState {
    CHOOSE,
    WALK,
    WORK,
    DONE
}
```

Transition through it with debug messages.

## Exercise C — Candidate scoring

Given:

```text
A distance=2 water=false danger=false
B distance=1 water=true danger=false
C distance=3 water=false danger=true
```

Choose your own cost numbers and determine winner.

## Exercise D — Stuck recovery

Write a five-stage recovery policy without code.

Only then code it.

---

# 52. Vanilla classes to study

Search in IntelliJ/mappings:

```text
net.minecraft.world.entity.ai.goal.Goal
GoalSelector
MoveToBlockGoal
RandomStrollGoal
FloatGoal
FollowOwnerGoal

net.minecraft.world.entity.ai.navigation.PathNavigation
GroundPathNavigation
FlyingPathNavigation

net.minecraft.world.entity.ai.control.MoveControl
FlyingMoveControl

net.minecraft.world.level.pathfinder.Path
Node
NodeEvaluator
WalkNodeEvaluator
FlyNodeEvaluator
PathType
```

Reference:
https://mappings.dev/1.21.1/net/minecraft/world/entity/ai/goal/
https://mappings.dev/1.21.1/net/minecraft/world/entity/ai/navigation/PathNavigation.html


---

<!-- SOURCE SECTION: 04_MODELS_ANIMATIONS_SOUNDS.md -->

# Block Models, Entity Models, Animation, and Timed Sounds

This file separates four things that beginners often accidentally mix:

```text
MODEL
what shape exists

TEXTURE
what pixels appear on the shape

ANIMATION
how model parts change over time

GAMEPLAY STATE
what the server says the object is doing
```

They are connected, but they are not the same system.

---

# 1. Block model pipeline

```text
registered Block
    ↓
BlockState
    ↓
blockstates/<name>.json
    ↓
models/block/<model>.json
    ↓
textures/block/<texture>.png
```

NeoForge/Minecraft block models are JSON.

Entity models are usually client Java model classes (or library-specific model formats if you add a library).

---

# 2. One block, several visual models

For your lily pads:

```text
PADS=1
PADS=2
PADS=3
PADS=4
```

Blockstate JSON:

```json
{
  "variants": {
    "pads=1": {
      "model": "meowskis:block/lily_pads_1"
    },
    "pads=2": {
      "model": "meowskis:block/lily_pads_2"
    },
    "pads=3": {
      "model": "meowskis:block/lily_pads_3"
    },
    "pads=4": {
      "model": "meowskis:block/lily_pads_4"
    }
  }
}
```

This is the normal answer when you want several versions but do **not** want several registered block IDs.

---

# 3. Facing models

Suppose:

```java
public static final DirectionProperty FACING =
        BlockStateProperties.HORIZONTAL_FACING;
```

You can use the same model rotated based on state.

Conceptually:

```text
north → rotation 0
east  → rotation 90
south → rotation 180
west  → rotation 270
```

Datagen can generate this automatically.

Do not create four separate textures just because a block faces four directions.

---

# 4. Blockbench and Minecraft coordinates

Minecraft block model space is generally based around a 16-unit block cube:

```text
0 ... 16
```

Example full cube:

```json
"from": [0, 0, 0],
"to": [16, 16, 16]
```

Half-height slab-like cuboid:

```json
"from": [0, 0, 0],
"to": [16, 8, 16]
```

Your model may contain many elements.

---

# 5. Model parents

Simple cube model:

```json
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "meowskis:block/example"
  }
}
```

Parent models let you reuse geometry/display settings.

Do not duplicate a giant JSON if a parent already represents the shape.

---

# 6. Item model vs block model

Block:

```text
assets/meowskis/models/block/example.json
```

Item:

```text
assets/meowskis/models/item/example.json
```

An item model can point to the block model:

```json
{
  "parent": "meowskis:block/example"
}
```

---

# 7. Transparent/cutout plant models

Plants/leaves often need an appropriate render type/material behavior.

The geometry may use crossed planes rather than a cube.

Example parent patterns in vanilla resources are worth studying.

For custom leaves, remember that:

```text
block behavior
render layer
model
texture alpha
```

all influence appearance.

---

# 8. Entity model architecture

A vanilla-style entity renderer generally has:

```text
Entity
server/game state

EntityModel / HierarchicalModel
client model parts

EntityRenderer
draws entity with model + texture
```

Model parts form a hierarchy:

```text
root
├── body
│   ├── left_arm
│   └── right_arm
├── head
└── tracks
```

When the body rotates, child parts inherit that transform.

---

# 9. Why model hierarchy matters

Bad hierarchy:

```text
root
├── body
├── hand
└── finger
```

If hand should move with arm, but isn't a child of arm, animations become painful.

Better:

```text
root
└── body
    └── arm
        └── hand
            └── finger
```

Design animation hierarchy before exporting.

---

# 10. ModelPart

Vanilla model classes use:

```java
ModelPart
```

Typical fields:

```java
private final ModelPart root;
private final ModelPart head;
private final ModelPart leftArm;
private final ModelPart rightArm;
```

Constructor:

```java
public WorkerModel(ModelPart root) {
    this.root = root;
    this.head = root.getChild("head");
    this.leftArm = root.getChild("left_arm");
    this.rightArm = root.getChild("right_arm");
}
```

Names must match generated/exported model part names.

---

# 11. `HierarchicalModel`

For hierarchical animated entities, a model often extends:

```java
HierarchicalModel<WorkerEntity>
```

and returns:

```java
@Override
public ModelPart root() {
    return root;
}
```

This gives Minecraft animation helpers a root model tree to operate on.

---

# 12. `setupAnim`

Model animation is applied in:

```java
@Override
public void setupAnim(
        WorkerEntity entity,
        float limbSwing,
        float limbSwingAmount,
        float ageInTicks,
        float netHeadYaw,
        float headPitch) {

    ...
}
```

This method runs on the client rendering side.

It should read gameplay/animation state and pose the model.

---

# 13. Reset poses before applying animation

If you continuously add rotations without resetting, animation transforms may accumulate incorrectly.

A common pattern is to reset model parts to default pose before animations.

Conceptually:

```java
this.root()
        .getAllParts()
        .forEach(ModelPart::resetPose);
```

Then apply walk/work/idle animations.

---

# 14. AnimationState

Minecraft 1.21.1 has:

```java
net.minecraft.world.entity.AnimationState
```

Useful methods include ideas such as:

```text
start
startIfStopped
animateWhen
stop
isStarted
```

Entity:

```java
public final AnimationState idleAnimationState =
        new AnimationState();

public final AnimationState miningAnimationState =
        new AnimationState();
```

Then update them based on state.

---

# 15. Start/stop animation state

Conceptual entity logic:

```java
private void updateAnimationStates() {

    boolean mining =
            this.getWorkMode()
                    == WorkMode.EXCAVATING
            && this.isMining();

    this.miningAnimationState.animateWhen(
            mining,
            this.tickCount
    );

    this.idleAnimationState.animateWhen(
            !mining,
            this.tickCount
    );
}
```

Gameplay decides state.

Animation follows.

---

# 16. AnimationDefinition

Vanilla keyframe animations use:

```java
AnimationDefinition
AnimationChannel
Keyframe
```

An animation definition describes:

```text
duration
looping?
bone channels
keyframe times
transform values
interpolation
```

Example conceptual structure:

```java
public static final AnimationDefinition MINING =
        AnimationDefinition.Builder
                .withLength(1.2F)
                .looping()
                .addAnimation(
                        "right_arm",
                        new AnimationChannel(
                                AnimationChannel.Targets.ROTATION,
                                new Keyframe(
                                        0.0F,
                                        KeyframeAnimations.degreeVec(
                                                0,
                                                0,
                                                0
                                        ),
                                        AnimationChannel.Interpolations.LINEAR
                                ),
                                new Keyframe(
                                        0.3F,
                                        KeyframeAnimations.degreeVec(
                                                -60,
                                                0,
                                                10
                                        ),
                                        AnimationChannel.Interpolations.LINEAR
                                )
                        )
                )
                .build();
```

> [!WARNING]
> Exact generated animation code depends on your exporter/version. Use the concept to read generated code; do not hand-copy random animation class snippets from another Minecraft version.

---

# 17. Apply animation in model

A `HierarchicalModel` provides helpers for `AnimationState` + `AnimationDefinition`.

Conceptually:

```java
this.animate(
        entity.miningAnimationState,
        WorkerAnimations.MINING,
        ageInTicks
);
```

Walking can be a separate animation:

```java
this.animateWalk(
        WorkerAnimations.WALK,
        limbSwing,
        limbSwingAmount,
        2.0F,
        2.5F
);
```

---

# 18. Multiple animations at once

Yes, multiple animations can be applied in the same render update.

Example:

```text
base walking
+
arm work animation
+
head look
```

But be careful if two animations modify the **same bone**.

Example conflict:

```text
walk animation rotates arm
mining animation rotates same arm
```

Last-applied transforms/composition may create unexpected results.

Design animation ownership.

---

# 19. Animation layers by body region

A useful mental design:

```text
LOCOMOTION
tracks / legs / body bob

ACTION
arms / tool / crane

LOOK
head / camera / eyes

IDLE DETAIL
antenna / small bounce
```

Keep actions from fighting locomotion when possible.

---

# 20. Tread animation

There are two main visual approaches.

## Geometry animation

Move/rotate individual tread segments.

Pros:

- physically expressive

Cons:

- many bones/parts
- expensive and tedious
- turning requires more animation work

## Texture/UV animation

Keep belt geometry mostly fixed and animate visual tread movement.

Pros:

- much simpler
- cheaper model hierarchy

Cons:

- does not physically articulate each tread

For a Minecraft-styled robot, texture movement can be the better compromise.

---

# 21. Texture animation

Minecraft resource textures can have `.mcmeta` animation metadata for frame animation.

Example texture:

```text
worker_tread.png
```

with vertically stacked frames.

Metadata:

```text
worker_tread.png.mcmeta
```

Example:

```json
{
  "animation": {
    "frametime": 2,
    "interpolate": false
  }
}
```

This is simple frame animation.

It does not automatically understand entity movement direction.

If direction-specific belt motion matters, you need a more custom rendering/state solution.

---

# 22. Sound registration

Custom sound architecture:

```text
SoundEvent registered in Java
        ↓
sounds.json maps event to OGG file
        ↓
code calls playSound
```

Typical resources:

```text
assets/meowskis/sounds.json
assets/meowskis/sounds/entity/worker_mine.ogg
```

---

# 23. `sounds.json`

Example:

```json
{
  "worker_mine": {
    "sounds": [
      "meowskis:entity/worker_mine"
    ]
  }
}
```

Then your Java `SoundEvent` ID should correspond to:

```text
meowskis:worker_mine
```

---

# 24. Play a sound from entity

Entity classes can use sound methods.

Pattern:

```java
this.playSound(
        ModSounds.WORKER_MINE.get(),
        0.8F,
        1.0F
);
```

Parameters:

```text
sound
volume
pitch
```

Randomized pitch can prevent repetition:

```java
float pitch =
        0.95F
        + this.random.nextFloat() * 0.1F;
```

---

# 25. Specific sound timing

You asked specifically about playing sounds at exact moments in animation/actions.

Use gameplay timeline ticks:

```java
private int miningTicks;

private void tickMining() {

    miningTicks++;

    if (miningTicks == 5) {
        playServoSound();
    }

    if (miningTicks == 10) {
        playImpactSound();
    }

    if (miningTicks == 20) {
        finishBlockBreak();
    }
}
```

This is robust because the action state owns the timeline.

---

# 26. Why not use client animation time to break blocks?

Because:

- rendering may not tick identically
- client can lag
- dedicated server has no renderer
- server must own actual world state

Use:

```text
server action timeline
→ synchronized animation state
→ client visual animation
```

---

# 27. Sound sync to a looping animation

If mining repeats every 12 ticks:

```java
if (miningTicks % 12 == 6) {
    playImpactSound();
}
```

Animation can loop with a matching approximate period.

If exact audiovisual synchronization becomes important, define both from the same named timeline constants.

---

# 28. Timeline object idea

```java
public final class MiningTimeline {

    public static final int LENGTH = 24;
    public static final int SERVO = 4;
    public static final int IMPACT_1 = 8;
    public static final int IMPACT_2 = 16;
    public static final int COMPLETE = 24;

    private MiningTimeline() {
    }
}
```

Now:

```text
gameplay
sound
particles
```

can share the same timing constants.

---

# 29. Particles on impact

Server/client rules depend on particle method/type, but conceptually:

```text
tick 8
→ sound
→ block particle burst
→ arm reaches impact pose
```

Particles should be visual feedback, not authority.

---

# 30. Procedural animation

Not every motion needs keyframes.

Head look:

```java
head.yRot =
        netHeadYaw * Mth.DEG_TO_RAD;

head.xRot =
        headPitch * Mth.DEG_TO_RAD;
```

Wheel rotation:

```java
wheel.xRot += movementAmount;
```

Oscillation:

```java
float bob =
        Mth.sin(ageInTicks * 0.1F)
        * 0.05F;
```

Keyframes are great for authored actions.

Math is great for continuous reactive motion.

---

# 31. Smooth interpolation

Client rendering often benefits from interpolating values.

Concept:

```java
float visualArmAngle =
        Mth.lerp(
                partialProgress,
                previousAngle,
                currentAngle
        );
```

This avoids visually snapping between simulation states.

---

# 32. Model bone naming

Use predictable names:

```text
root
body
head
left_arm
left_hand
right_arm
right_hand
left_track
right_track
tool
```

Avoid:

```text
cube17
group4
thing2
```

Code/animations refer to names.

Good naming saves hours.

---

# 33. Export strategy

Keep source `.bbmodel` files for editing, but the game's runtime needs generated/exported assets/code in the correct place.

Your Gradle snapshot explicitly excludes `.bbmodel` files from finalized resources.

That is fine:

```text
.bbmodel = authoring source
runtime model/code/json = shipped output
```

---

# 34. Block model collision ≠ visual model

A custom Blockbench model does **not automatically define collision**.

Visual:

```text
model JSON
```

Collision:

```java
VoxelShape
```

Example:

```java
private static final VoxelShape SHAPE =
        Shapes.or(
                Block.box(0, 0, 0, 16, 4, 16),
                Block.box(4, 4, 4, 12, 12, 12)
        );
```

You can combine multiple boxes into one collision shape.

---

# 35. `Shapes.or` = combine collision boxes

This is another meaning of "combine multiple blocks/shapes into one".

```java
VoxelShape BASE =
        Block.box(
                0, 0, 0,
                16, 4, 16
        );

VoxelShape TOP =
        Block.box(
                4, 4, 4,
                12, 12, 12
        );

VoxelShape SHAPE =
        Shapes.or(
                BASE,
                TOP
        );
```

One block position.

One block.

Collision composed of multiple cuboids.

---

# 36. Direction-dependent collision

If a block rotates, its collision may need rotation too.

Strategy:

```text
define NORTH shape
rotate coordinates for EAST/SOUTH/WEST
cache each result
```

Do not rebuild complicated voxel shapes every collision query if you can cache static variants.

---

# 37. Optional animation libraries

Your current `build.gradle` snapshot does **not** include an animation library.

So do not paste GeckoLib classes into the project and wonder why imports fail.

If you later choose a library:

```text
1. add version-compatible dependency
2. understand its entity/model/animation registration
3. decide whether it replaces or complements vanilla model pipeline
4. keep gameplay state server-authoritative
```

A library can simplify animation authoring.

It does not replace good AI/gameplay architecture.

---

# 38. Exercises

## Exercise A — four-state block

Create one test block with:

```text
variant=1..4
```

Make each state select a different model.

## Exercise B — collision composition

Make one block collision from three boxes.

Predict which empty gaps players can move through.

## Exercise C — animation state

Create:

```text
IDLE
WORKING
```

and make only the correct animation state run.

## Exercise D — timed sound

Make an action lasting 40 ticks.

Play sounds exactly at:

```text
10
20
30
```

Then convert to modulo-based repeating logic.

---

# 39. References

NeoForge models:
https://docs.neoforged.net/docs/1.21.1/resources/client/models/

NeoForge model datagen:
https://docs.neoforged.net/docs/1.21.1/resources/client/models/datagen/

Minecraft mappings:
https://mappings.dev/1.21.1/

Useful classes:
- `ModelPart`
- `HierarchicalModel`
- `AnimationState`
- `AnimationDefinition`
- `AnimationChannel`
- `Keyframe`
- `KeyframeAnimations`
- `SoundEvent`


---

<!-- SOURCE SECTION: 05_ARCHITECTURE_DEBUGGING_AND_EXERCISES.md -->

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


---

<!-- SOURCE SECTION: 06_FAST_CHEAT_SHEET.md -->

# NeoForge / Java Fast Cheat Sheet

Use this when you remember the concept but forget the syntax.

---

# Java

## Field

```java
private int value;
```

## Constant

```java
private static final int MAX_SIZE = 32;
```

## Constructor

```java
public Example(int value) {
    this.value = value;
}
```

## Void method

```java
public void reset() {
    value = 0;
}
```

## Returning method

```java
public int getValue() {
    return value;
}
```

## Parameter

```java
public void setValue(int newValue) {
    value = newValue;
}
```

## If

```java
if (value > 10) {
    ...
}
```

## If/else

```java
if (working) {
    ...
} else {
    ...
}
```

## For loop

```java
for (int i = 0; i < 10; i++) {
    ...
}
```

## Enhanced for

```java
for (BlockPos pos : positions) {
    ...
}
```

## While

```java
while (condition) {
    ...
}
```

## Switch

```java
switch (mode) {
    case IDLE -> tickIdle();
    case WORKING -> tickWorking();
}
```

## List

```java
List<BlockPos> positions =
        new ArrayList<>();
```

Add:

```java
positions.add(pos);
```

## Set

```java
Set<BlockPos> visited =
        new HashSet<>();
```

Useful when each value should be unique.

## Map

```java
Map<BlockPos, Integer> failures =
        new HashMap<>();
```

Key → value.

## Enum

```java
public enum WorkMode {
    IDLE,
    MINING,
    RETURNING
}
```

## Record

```java
public record WorkArea(
        BlockPos min,
        BlockPos max) {
}
```

## Inheritance

```java
public class WorkerEntity
        extends PathfinderMob {
}
```

## Interface

```java
public interface WorkTask {
    void tick();
}
```

## Override

```java
@Override
public void tick() {
    super.tick();
}
```

---

# Coordinates

Up:

```java
pos.above()
pos.above(3)
```

Down:

```java
pos.below()
```

Direction:

```java
pos.relative(Direction.NORTH)
```

Offset:

```java
pos.offset(x, y, z)
```

Distance:

```java
entity.position()
        .distanceTo(otherPosition);
```

---

# BlockState

Read:

```java
int pads =
        state.getValue(PADS);
```

Change:

```java
state.setValue(PADS, 2);
```

Set world block:

```java
level.setBlock(
        pos,
        newState,
        3
);
```

Get:

```java
BlockState state =
        level.getBlockState(pos);
```

---

# Properties

Boolean:

```java
public static final BooleanProperty ACTIVE =
        BooleanProperty.create("active");
```

Integer:

```java
public static final IntegerProperty COUNT =
        IntegerProperty.create("count", 1, 4);
```

Horizontal facing:

```java
public static final DirectionProperty FACING =
        BlockStateProperties.HORIZONTAL_FACING;
```

Register:

```java
@Override
protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder) {

    builder.add(ACTIVE, COUNT, FACING);
}
```

---

# Voxel shapes

Box:

```java
Block.box(
        0, 0, 0,
        16, 8, 16
);
```

Combine:

```java
Shapes.or(
        shapeA,
        shapeB,
        shapeC
);
```

Empty:

```java
Shapes.empty();
```

---

# Resource IDs

```java
ResourceLocation.fromNamespaceAndPath(
        meowski.MOD_ID,
        "willow"
);
```

Result:

```text
meowskis:willow
```

---

# Registry key

```java
ResourceKey.create(
        Registries.CONFIGURED_FEATURE,
        id
);
```

---

# Tree providers

Always one state:

```java
BlockStateProvider.simple(
        Blocks.OAK_LOG
);
```

Always integer:

```java
ConstantInt.of(3)
```

Random integer range:

```java
UniformInt.of(3, 6)
```

---

# Tree mental shortcut

```text
TRUNK BLOCK
+
TRUNK PLACER
+
LEAF BLOCK
+
FOLIAGE PLACER
+
FEATURE SIZE
=
TreeConfiguration
```

---

# Worldgen shortcut

```text
CF
WHAT

PF
WHERE / HOW OFTEN

BM
WHICH BIOME
```

---

# Placed-feature concepts

```java
CountPlacement.of(8)
```

attempt count.

```java
InSquarePlacement.spread()
```

spread X/Z.

```java
BiomeFilter.biome()
```

respect current biome context.

```java
HeightmapPlacement.onHeightmap(...)
```

map candidate to terrain height.

---

# Goal

```java
public class ExampleGoal extends Goal {

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void stop() {
    }
}
```

---

# Navigation

Move:

```java
mob.getNavigation()
        .moveTo(
                x,
                y,
                z,
                speed
        );
```

Stop:

```java
mob.getNavigation()
        .stop();
```

Done:

```java
mob.getNavigation()
        .isDone();
```

Path creation APIs vary by target/overload; inspect exact 1.21.1 source before paste.

---

# Entity state machine

```java
switch (state) {

    case CHOOSE ->
            chooseTarget();

    case WALK ->
            navigate();

    case WORK ->
            work();

    case RECOVER ->
            recover();
}
```

---

# Server check

```java
if (!level().isClientSide) {
    // actual world/gameplay change
}
```

---

# Timer

```java
ticks++;

if (ticks >= 20) {
    ticks = 0;
}
```

Modulo:

```java
if (ticks % 8 == 0) {
    playSound();
}
```

---

# Sound

```java
this.playSound(
        ModSounds.EXAMPLE.get(),
        1.0F,
        1.0F
);
```

---

# Animation State

```java
public final AnimationState workAnimationState =
        new AnimationState();
```

Toggle:

```java
workAnimationState.animateWhen(
        working,
        tickCount
);
```

---

# Math

Clamp:

```java
Mth.clamp(value, min, max);
```

Lerp:

```java
Mth.lerp(progress, a, b);
```

Sin:

```java
Mth.sin(value);
```

Distance squared is often cheaper when you only compare distances:

```java
double distanceSq =
        pos.distSqr(other);
```

---

# Debug checklist

```text
Does it compile?
↓
Is it registered?
↓
Does generated data exist?
↓
Is it attached to the correct biome/entity/block?
↓
Is a filter preventing it?
↓
Does an exaggerated test prove it runs?
↓
Is the bug server-side or client-side?
↓
Which state is active?
```

---

# File ownership shortcut

```text
BLOCK REGISTRATION
→ ModBlocks

BLOCK BEHAVIOR
→ block/custom

BLOCK MODEL
→ assets/.../models/block

BLOCKSTATE MODEL SELECTION
→ assets/.../blockstates

TREE RECIPE
→ ConfiguredFeature

TREE PLACEMENT
→ PlacedFeature

TREE BIOME ATTACHMENT
→ BiomeModifier

CUSTOM TREE GEOMETRY
→ TrunkPlacer / FoliagePlacer

TERRAIN SHAPE
→ noise/density generator

BIOME DISTRIBUTION
→ biome source/climate parameters

SURFACE MATERIAL
→ surface rules

NPC DECISION
→ Goal / custom task controller

PATH
→ Navigation / evaluator

MOVEMENT
→ MoveControl

ENTITY MODEL
→ client model

ANIMATION
→ AnimationState / AnimationDefinition

SOUND EVENT
→ ModSounds + sounds.json + OGG
```


---

<!-- SOURCE SECTION: 07_YOUR_FEATURES_TO_CODE_ROADMAP.md -->

# Your Mod Ideas → Exact System Map

This file maps the features you have discussed to the Minecraft/NeoForge system that should own them.

Use it when you know **what you want** but do not know **where the code belongs**.

---

# 1. Willow swamp overhaul

## Desired result

```text
low coastal wetland
+
island-like raised terrain
+
dark green/swamp surface
+
mud
+
cattails
+
custom lily-pad clusters
+
large hanging willow trees
+
remove/replace vanilla swamp oak
+
eventually replace swamp hut
```

## System map

### Low/coastal swamp location

Owned by:

```text
BIOME SOURCE / CLIMATE LAYOUT
+
TERRAIN NOISE CORRELATION
```

Not by the willow feature.

### Swamp basin/island shape

Owned by:

```text
DENSITY / NOISE
```

Not by biome temperature field.

### Mud/grass surface

Owned primarily by:

```text
SURFACE RULES
```

with small patch features where appropriate.

### Willow

Owned by:

```text
ConfiguredFeature
PlacedFeature
BiomeModifier
TrunkPlacer
FoliagePlacer
Sapling TreeGrower
```

### Cattails

Owned by:

```text
custom Block
BlockState HALF
plant placement/survival
Configured/Placed Feature
BiomeModifier
```

### Lily pads

Owned by:

```text
LilyPadsBlock
PADS BlockState
four visual models
natural weighted patch feature
PlacedFeature
BiomeModifier
```

### Swamp hut replacement

Owned by:

```text
Structure
StructureSet
template/jigsaw/structure implementation
biome eligibility
```

---

# 2. Willow tree

## First version

Reuse vanilla algorithms:

```text
custom log
+
vanilla branching trunk placer
+
custom leaves
+
vanilla foliage placer
```

This teaches configuration.

## Final version

Likely:

```text
WillowTrunkPlacer
+
WillowFoliagePlacer
```

Algorithm:

```text
grow trunk 12–17-ish blocks
↓
branch strongly near upper trunk
↓
curve major branches outward/up
↓
create foliage attachment at branch ends
↓
large canopy
↓
choose canopy edge cells
↓
hang leaf strands down
```

### Files

Long-term:

```text
worldgen/tree/WillowTrunkPlacer.java
worldgen/tree/WillowFoliagePlacer.java
worldgen/tree/ModTrunkPlacers.java
worldgen/tree/ModFoliagePlacers.java
worldgen/ModConfiguredFeatures.java
worldgen/ModPlacedFeatures.java
worldgen/ModBiomeModifiers.java
```

---

# 3. Oak/birch/spruce redesign

Do not add a new biome just because a tree changes.

Use:

```text
tree families
+
placement distributions
```

Example:

```text
OAK
normal
wide
old
small

BIRCH
normal
tall
leaning

SPRUCE
small
tall
old/wide
```

One forest can select several.

This creates variety with fewer new biome IDs.

---

# 4. Plains overhaul

## Desired

```text
not flat
broad rolling elevation
buildable
horse-friendly
few annoying micro-bumps
occasional hills
```

## Owned by

```text
NOISE / DENSITY
```

plus:

```text
surface rules
vegetation features
biome placement
```

Conceptual terrain signal:

```text
broad roll: high spatial scale, medium amplitude
local detail: smaller spatial scale, low amplitude
rare hill mask: sparse stronger influence
```

The biome object itself does not have a simple terrain-height field.

---

# 5. Forest overhaul

## Desired

```text
real hills/valleys
less chaotic
horse-traversable
clearings
natural floor
better oak/birch/spruce
```

## Owned by

Terrain:

```text
density/noise
```

Floor:

```text
surface rules
+
patch features
```

Trees:

```text
tree configured features
+
placement distribution
```

Clearings:

```text
vegetation density/noise mask
```

---

# 6. Forest-floor patches

Materials:

```text
moss
rooted dirt
coarse dirt
podzol where appropriate
```

Do not roll every block independently.

Use:

```text
patch center
+
shape/noise mask
+
material sub-noise
```

Architecture:

```text
ForestFloorFeature
        ↓
ConfiguredFeature
        ↓
PlacedFeature
        ↓
BiomeModifier
```

---

# 7. Deeper oceans

Desired:

```text
coastal shelf
ocean basin
deep ocean
rare trench
```

Owned by:

```text
continental terrain/density
+
ocean-depth masks
```

Decoration only handles:

```text
seagrass
kelp
coral
underwater features
```

Do not try to create deep oceans by only lowering the ocean biome temperature or using a feature that digs holes.

---

# 8. Hot macro-continent

Desired:

```text
large coherent hot region
├── desert
├── savanna
└── badlands
```

Owned by:

```text
biome source climate parameter layout
```

Use broad:

```text
temperature
humidity
```

and terrain-related dimensions to separate sub-biomes.

Conceptual:

```text
hot + very dry + flatter
→ desert

hot + less dry + rolling
→ savanna

hot + dry + ridged/eroded
→ badlands
```

---

# 9. Mountains

Desired:

```text
actual difficult mountain regions
not random spikes everywhere
```

Owned by:

```text
mountain region mask
+
ridge/noise shaping
+
erosion/depth functions
```

Then biome source places mountain-family biomes into matching climate/terrain parameter regions.

---

# 10. Custom block models

Use when:

```text
shape differs from vanilla cube
```

Flow:

```text
Blockbench
↓
model JSON / generated model
↓
blockstate JSON
↓
texture
```

Collision separately:

```text
VoxelShape
```

Several boxes in one block collision:

```java
Shapes.or(...)
```

---

# 11. "Several blocks into one" decision

## Several visual variants but same cell/object

Use:

```text
BlockState
```

Example:

```text
lily pad count
```

## Several visible pieces controlled by booleans/directions

Use:

```text
multipart blockstate model
```

Example:

```text
pipe connectors
```

## Lots of dynamic storage/configuration

Use:

```text
BlockEntity
```

## Physical machine spanning several block coordinates

Use:

```text
multiblock structure
```

---

# 12. Lily pads

Desired behavior already fits:

```text
one registered block
PADS=1..4
place same item → increment
four models
no hard collision
more pads → stronger movement slowdown
water survival
natural swamp generation
```

Recommended additions:

```text
source-water/placement rules tuned intentionally
loot behavior for stacked count
weighted worldgen states
patch clustering
possibly rotate variants for visual repetition reduction
```

---

# 13. Cattails

Recommended:

```text
single registered tall-plant block
HALF=LOWER/UPPER
water/ground validation
upper/lower synchronization
natural patch placement
```

If you later want several stalk densities:

```text
COUNT property
```

but synchronize count across both halves.

---

# 14. Worker/WALL-E-like NPC

High-level modules:

```text
WorkerEntity
├── WorkMode
├── WorkArea
├── ExcavationController
├── HarvestController
├── StorageController
├── NavigationPolicy
├── work Goals
└── synchronized visual state
```

Do not make one giant AI file.

---

# 15. R-key HUD / command UI concept

Gameplay UI:

```text
client opens HUD
↓
user chooses mode
↓
network packet to server
↓
server validates ownership/state
↓
server changes WorkMode
↓
entity syncs result
```

Do not directly change server entity state only from client GUI code.

---

# 16. ALT storage-selection concept

```text
client selection UI
↓
selected block position
↓
send to server
↓
server validates distance/container/ownership
↓
store BlockPos on worker
```

---

# 17. A/B excavation selection

Data object:

```text
point A
point B
→ normalized WorkArea(min,max)
```

Validation:

```text
width <= 32
height <= 32
depth <= 32
```

Preview:

```text
blue valid
red too large/invalid
```

Preview is client visual.

Final accepted work area is server state.

---

# 18. Excavation

Desired:

```text
nearest sensible start
top-down
layer-by-layer
zigzag
mine all allowed blocks
safe work positions
recover from caves/uneven terrain
preserve access route
```

Architecture:

```text
WorkArea
↓
LayerPlanner
↓
TargetSelector
↓
WorkPositionSelector
↓
Navigation
↓
MiningTimeline
↓
Inventory
↓
next target
```

---

# 19. "Smarter" pathfinding

Do not begin by replacing A*.

Improve in this order:

```text
1. better target selection
2. better stand-position selection
3. path node costs/malus
4. stuck detection
5. recovery/replan
6. reserved access routes
7. custom node evaluator only when necessary
8. custom pathfinder only if Minecraft's graph fundamentally cannot model the behavior
```

---

# 20. Worker mining

Gameplay state:

```text
NAVIGATE
↓
FACE BLOCK
↓
MINING
↓
timed animation/sounds
↓
server breaks block
↓
loot/inventory
```

Check:

```text
inside WorkArea?
mineable tag?
safe?
reachable?
inventory space?
```

---

# 21. Worker flying/floating

Owned by:

```text
FlyingPathNavigation
FlyingMoveControl
custom hover math if needed
```

Do not use ground navigation and simply set gravity false.

Movement and navigation should both understand 3D space.

---

# 22. Worker wandering

Idle:

```text
stay around owner radius
wander rather than glue to player
look around
```

Goal layer:

```text
random stroll
owner-radius correction
random look
```

Work mode should have higher priority than idle behavior.

---

# 23. Storage return

```text
inventory threshold
↓
RETURNING_STORAGE mode
↓
navigate
↓
validate container
↓
transfer
↓
resume previous work
```

Store previous mode if necessary:

```java
WorkMode resumeMode;
```

---

# 24. Animation

Gameplay states should map to animations:

```text
RESTING
→ idle

NAVIGATING
→ locomotion

MINING
→ mining arm/tool + locomotion stopped

HARVESTING
→ harvest action

FLYING
→ propeller/hover animation
```

Use entity-synchronized state as source.

---

# 25. Timed sounds

Attach to action timeline:

```text
mine tick 5
servo

mine tick 10
impact

mine tick 20
break
```

Do not make sound files responsible for gameplay timing.

---

# 26. Smart debugging roadmap for your project

When Codex adds a new system, test in layers.

## Tree

```text
Can configured feature be manually placed?
↓
Does placed feature spawn?
↓
Does biome modifier attach?
↓
Does sapling grow?
```

## Lily pads

```text
Can place one?
↓
Can stack?
↓
Does model change?
↓
Does slowdown change?
↓
Does survival work?
↓
Does natural generation work?
```

## Worker

```text
Entity spawns?
↓
Can walk to fixed coordinate?
↓
Can select one block?
↓
Can stand beside it?
↓
Can perform timed mining?
↓
Can repeat small 3×3 job?
↓
Can handle full WorkArea?
↓
Can recover?
```

---

# 27. What NOT to combine prematurely

Do not test for the first time as:

```text
new custom terrain
+
new custom biome source
+
new willow
+
new lily pads
+
new hut
+
new worker
```

Every subsystem should have its own proof test.

Then integrate.

---

# 28. Recommended implementation order for the overhaul

```text
1. Finish basic blocks/items
2. Lily-pad state/models
3. CF → PF → BM tree pipeline
4. Oak/birch/spruce test families
5. Willow custom geometry
6. Swamp vegetation
7. Forest-floor patches
8. Rebuild/test vanilla-like biome definitions
9. Learn surface rules
10. Learn climate/OverworldBiomeBuilder
11. Learn density/noise
12. Create terrain prototype
13. Climate + terrain integration
14. Ocean redesign
15. Full swamp terrain
16. structures
17. worker NPC basics
18. worker task state machine
19. excavation
20. smart navigation
21. animation/sound polish
```

The order is not because later systems are less important.

It is because each earlier system gives you concepts needed by the later one.
