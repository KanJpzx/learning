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
