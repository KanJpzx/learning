# Meowski Terrain & Nature Rework — Plan

## Where things stand right now

**Live and working:**
- Forest grass/fern density fix (`ModPlacedFeatures` / `ModBiomeModifiers`) — done.
- Boulder ore mix (`boulderStateProvider()`) — stone/andesite + coal/iron/copper ore, granite/diorite explicitly excluded. Done.
- `TectonicConfigPatcher.applyDesiredSettings()` — patches `tectonic.json` on disk before Tectonic reads it. Wired into the `meowski` constructor, ordering confirmed via `ordering="AFTER"` on the `tectonic` dependency in `neoforge.mods.toml`.
- `/climate` command — reports biome, feet Y, surface Y, and the real continentalness/erosion/temperature/humidity/weirdness/depth values at the player's position. **Just confirmed working.**
- First-draft terrain flatten override — `data/tectonic/worldgen/density_function/terrain_spline/offset/continents.json` wraps Tectonic's own offset function, blending toward a flat target when "solidly inland" + erosion falls in a placeholder band. Original Tectonic function preserved untouched at `data/meowski/.../tectonic_continents_base.json`.

**Known problems with the current draft (confirmed by screenshots):**
1. Plains/forest still climb way too high in places (Y74, Y88 near taiga) — the soft blend lets Tectonic's original hill partially show through mid-ramp.
2. Beach generating at Y66 (should be ≤64) — the "inland" gate starts too early, catching part of the coastal zone.
3. Plains sometimes reads at Y64 instead of Y66 — the flat-target constant is still the placeholder `0`, needs real calibration.
4. Stony shore has **zero** clamp — never touched yet, hence the giant unclamped rock dome.
5. Plains/snowy-mountain biome sit at the same elevation band, so they visibly collide mid-slope (mountain biome shouldn't be reachable from a plains-height climb at all).
6. Transitions are all smooth ramps — goal is a **hard ceiling/floor** (not blend) plus a **narrow, steep transition** where elevation does change, so it reads as a wall, not a round hill.

## Immediate next step: calibration

Use `/climate` at these specific spots and record biome / erosion / continentalness / actual Y:
1. A normal, correctly-flat-looking plains spot (baseline).
2. The Y74/Y88 overshoot spots from the screenshots.
3. The stony-shore dome top, and its base near water.
4. The beach spot currently reading Y66.
5. The exact plains/snowy-mountain seam from the mountain screenshot.

Bring all of those back in one batch — that's everything needed to rebuild the wrapper with real numbers instead of guesses.

## Once calibrated: rebuild the terrain system properly

- **Hard min/max clamp** (not soft blend) for plains + forest: 66–71.
- **Same clamp treatment for stony shore**: 66–71.
- **Swamp**: separate hard ceiling near sea level (~61–63), always low/flooded-feeling regardless of neighbors.
- **Narrow steep-transition band** at the edges of each clamp zone, replacing the current wide smooth ramp — the "wall, not ramp" goal, so building-flat areas don't get eaten by a gradual climb.
- **Slope character variation**: a second, independent large-scale noise deciding per-region whether a given transition is steep/cliff-like or gentle/smooth — the "50/50, not monotonous" idea, inspired by (not copied from) Midgard's terrain feel.
- **Two-tier plains/forest elevation**: a low tier (66–71, the default) and a raised plateau tier, selected by that same secondary noise — "when is the secondary elevation allowed" from a few days back.
- **Biome separation (Lithostitched Regions)**: keep arid biomes (desert/savanna/badlands) clustered away from everything else, and stop plains/mountain biomes from being placed in overlapping climate zones in the first place — a complement to the height clamp, not a replacement for it.

## Continent system (v1 built, needs in-game testing)

**Status:** first draft shipped — `continent_selector.json` (the large-scale
zone noise) plus overrides for `temperature.json` and `vegetation.json`
(vanilla's internal name for humidity) that remap each into a per-zone band.
Not yet flown around and checked. Numbers (zone boundary locations, target
ranges) are a reasonable starting guess, same as the original height-clamp
draft was — expect to retune after seeing it in-world.


**Core model:** continent = *which biomes are even reachable here*. Elevation
(erosion, same system as the height-clamp work above) = *which of those
reachable biomes actually shows up at this spot*. The two axes are
independent and don't interfere with each other.

**How it'll be built:** not Terrablender, not needed — skip it entirely.
Lithostitched already replaces that role, but even Lithostitched Regions
aren't the right layer here (they're a Terrablender-style weighted biome
*injection* system, still driven by the same global climate noise — doesn't
give hard non-interleaving landmasses). Instead: a large-wavelength spatial
noise biases the **temperature and humidity** density functions per
continent zone. Continentalness, erosion, and weirdness stay completely
untouched — they keep doing their normal job (hills vs. flat vs. coast, plus
the height-clamp work above) *within* whichever continent a spot belongs to.
Because vanilla biome selection works by nearest-climate-point-match, biasing
temp/humidity hard enough makes off-continent biomes' parameter points
mathematically unreachable there.

**Important consequence, free of extra work:** vanilla already computes a
separate, elevation-adjusted temperature for extreme-erosion (peak) spots,
independent of the area's normal ground-level temperature — that's why
deserts can have snow-capped mountains in vanilla right now. Since our
continent bias sets the *baseline* temperature per zone, that baseline
carries through into the peak calculation automatically. Result: Hot/Tropical
continents naturally land on stony/jagged peaks instead of frozen ones at
extreme elevation, with no separate rule needed — it just falls out of the
one bias.

**Calibration data already being collected (erosion/continentalness) stays
valid** — continents only touch temperature/humidity, so nothing about the
plains/forest/stony-shore height-clamp work needs to be redone once
continents exist.

**Zone sizing:** big, contiguous landmasses, not scattered small patches —
low-frequency continent-selector noise, split 4 ways (Hot/Cold/Tropical/Mix
— see Fantasy note below for why it's not a 5th slice of this same noise).
Mix is the largest continent (most biome variety riding on it). Number of
distinct landmasses per continent type can be semi-random, roughly 8+ each,
no hard cap.

**Zone → biome assignments (draft, confirm/adjust as biomes get cut):**

- **Hot:** desert, badlands, eroded badlands, wooded badlands, savanna,
  savanna plateau, windswept savanna. Peaks here land on stony (never
  frozen) via the elevation-adjusted-temperature mechanic above.
- **Cold:** snowy plains, ice spikes, snowy taiga, grove, snowy slopes,
  frozen peaks, jagged peaks, stony peaks, frozen ocean, deep frozen ocean,
  snowy beach, frozen river.
- **Tropical:** jungle, sparse jungle, bamboo jungle, mangrove swamp, warm
  ocean, lukewarm ocean, deep lukewarm ocean. Peaks here land on stony
  (never frozen), same mechanic as Hot.
- **Mix (largest):** plains, sunflower plains, forest, flower forest, birch
  forest, old growth birch forest, dark forest, taiga, old growth pine
  taiga, old growth spruce taiga, meadow, cherry grove, swamp, river,
  beach, stony shore, ocean, cold ocean, deep cold ocean. Frozen/jagged/stony peaks all reachable here at
  extreme elevation (this is the one continent cold enough at baseline to
  get true frozen peaks). Taiga + cherry grove sit at the high-elevation
  tier; forest/birch/dark-oak/plains sit low, per the elevation-tier plan
  above.
- **Zone-agnostic, not part of the continent system at all:** cave biomes
  (dripstone, lush, deep dark) — depth-placed, generate everywhere
  regardless of what's overhead.
- **Cut for sure:** gravelly hills, windswept hills. (Future idea, not now:
  a config toggle to let people re-enable cut biomes if they don't want the
  same trims — noted for later, not blocking current work.)

**"Fantasy" — not a 5th continent, a rare special-case pocket:** originally
floated as a 5th landmass, corrected — mushroom island (and pale oak, if
ported) aren't landmass-scale, they're rare pockets, same as mushroom fields
already work in vanilla today (gated by a narrow rarity condition + a
specific placement rule like "surrounded by deep ocean," independent of the
normal climate system). Building Fantasy as a full 5th slice of the
continent-partition noise would force it to be a giant contiguous landmass,
which is the opposite of the intended feel. Instead: **the main spatial
split stays 4-way** (Hot/Cold/Tropical/Mix), and Fantasy biomes get bolted
on as their own independent rare-pocket rule — reusing vanilla's existing
mushroom-field mechanism as the template rather than inventing a new system.
Can land inside any of the 4 continents, or be restricted to ocean like
vanilla, once we decide its exact placement rule.

**Open items before build:**
- Confirm/adjust the draft list above as biomes get cut.

## After terrain is stable: content pass ("fix the dead biomes")

Design philosophy: take *taste* inspiration from Midgard (terrain character) and Blooming Nature (atmosphere via small details) without copying assets, stay closer to vanilla's block palette than either — iterate/remodel/retexture until it looks like it could've shipped with vanilla, don't ship anything that doesn't clear that bar (the "cut the blueberries if they don't earn their place" standard).

- **Swamp rework** (top priority — most "dead" biome currently): oak-willow mix trees instead of plain oak, moss, small lily pads, cattails already prototyped. Willow log/plank color needs a redo first — pull the hue back toward vanilla's brown wood family instead of a green-shifted base, and make sure the log's cross-section color matches the plank tone.
- **Boulder shape/texture rework**: shell (mossy/weathered) vs. core (clean stone) texture split; real per-voxel surface noise instead of a smooth ellipsoid; distinct fused lumps instead of overlapping duplicates; optional flat "broken face" cut.
- Custom trees kept close to vanilla, not a full reinvention.
