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
