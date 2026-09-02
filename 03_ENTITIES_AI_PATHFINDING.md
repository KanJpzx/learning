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
