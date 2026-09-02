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
