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
