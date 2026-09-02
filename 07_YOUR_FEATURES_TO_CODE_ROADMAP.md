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
