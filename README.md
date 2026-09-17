# Plains/Forest flatten override — v1 draft

## What this does
Wraps Tectonic's own `tectonic:terrain_spline/offset/continents` function (the
piece that decides mainland height) with a blend: wherever a column reads as
"solidly inland" AND its erosion value falls in a target band, terrain height
gets pulled toward a flat target instead of whatever region archetype
(rolling hills / plateau / valley / dunes) Tectonic would have picked.
Everything outside that band — oceans, beaches, mountains, valleys — passes
through completely untouched. Tectonic's original function is preserved
byte-for-byte in `tectonic_continents_base.json` and just referenced, not
duplicated/rewritten.

## Where the files go
Drop the `data/` folder into your mod's resources
(`src/main/resources/data/...`), matching what's already there:
- `data/tectonic/worldgen/density_function/terrain_spline/offset/continents.json`
  — this **replaces** Tectonic's file at that same resource location. Because
  your mod depends on Tectonic, your data loads after it and wins.
- `data/meowski/worldgen/density_function/terrain_spline/offset/tectonic_continents_base.json`
  — untouched copy of Tectonic's original, kept under your own namespace so
  the override above can call back into it.

## Why the numbers are placeholders
I traced the actual density function graph, but two things I genuinely can't
know without your game running:
1. **Tectonic's raw erosion noise range that plains/forest land in.** I used
   0.30–0.90 as a starting guess based on general erosion-band knowledge, but
   Tectonic's internal raw erosion coordinate isn't the same scale as
   vanilla's -1..1 climate erosion — there's a spline in between.
2. **What offset value actually produces Y66.** That depends on your
   Tectonic config's vertical scale.

## Calibration steps (5-10 min)
1. Build the mod, load a dev world with a plains or forest chunk nearby.
2. Press F3. Look at the "Target Block" section — newer versions show
   biome climate values (or use `/locate biome` + world height at that spot).
3. Walk from clearly-flat plains into clearly-hilly terrain. Note the erosion
   value right at the point it starts looking too rough — that's roughly your
   upper bound. Note it again somewhere flat and confidently "plains-ish" for
   a lower bound.
4. Note the actual Y height you get with the placeholder target of `0`.
5. Send me both readings and I'll recompute the exact spline points and the
   flat-target constant with you — that's a 2-minute fix once we have real
   numbers instead of guesses.

## Next after this is dialed in
- Same technique, new wrapper, for beach: cap it ≤64, slope from plains(66)
  down through beach into ocean.
- Then: the "secondary elevation" system you mentioned, and the placement
  conditions for when it's allowed.
