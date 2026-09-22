# Deferred source

`deferred/excludes.txt` lists ant-style paths excluded from the main source set by
`build.gradle.kts`. Everything here is upstream code that is present in the tree but not
compiled. Each entry says why.

## Still excluded

| Excluded | Why |
|---|---|
| `**/compat/computercraft/**` | The `dan200` API is not on the build classpath. |

P3 restored the whole client half. P4 restored the ten ponder scenes, the basin mold layout,
the train-mounted engine sound and the pumpjack crank and hole ambient loops, so
`deferred/client-halves/` is now reference only.

## Dropped, with reasons

- **`TurretOperatorHatLayer`** - deleted. It is a cosmetic hat on the turret operator, it targeted
  `AgeableListModel` (gone in 26.2) through an accessor mixin, and 26.2 `RenderLayer` is
  `RenderLayer<S extends LivingEntityRenderState, M>` with a separate extract pass. The scope doc
  allowed scoping it out. `mixins/AgeableListModelAccessor` and `mixins/ModelPartAccessor` went with
  it (`ModelPart` is public in 26.2 anyway).
- **`ChemicalSprayerItem.getArmPose`** - `CustomArmPoseItem` has no Fly analogue.

## Deleted rather than deferred

- `compat/jei/**` (13 files) — JEI breaks multiplayer joins on Create Fly; REI is the project
  default, so the recipe viewer half is rebuilt later, not ported.
- `compat/kubejs/**`, `compat/strut_your_stuff/**`, `compat/EveryCompatCompat.java` — optional
  companion mods with no verified Fabric 26.2 build.
- `events/datagen/**` and the four `*Generator` blockstate providers — the port ships upstream's
  committed `src/generated/resources` instead of running datagen.
- `assets/everycomp/**`, `assets/quark/**` — art for blocks those companion mods generate.
- `CDGMixinPlugin` — only gated the Sable mixin, which is deleted.

## Known limitations on Create Fly

- The server config is not sent to clients. Create Fly's catnip config Builder has no networking at
  all, so a dedicated server and its players each read their own `createdieselgenerators-server`
  file. Only presentation is affected: the engine sound loop and the fuel-stat item tooltip evaluate
  the client's copy of the engine tier, redstone and analog options. Create's own server config has
  the same gap. Syncing it would need a login packet and would force clients and servers to update
  together.
- A contraption-mounted Oil Barrel shows no goggle information. `OilBarrelMountedStorage.afterSync`
  tests for Create's `FluidTankBlockEntity`, which the Oil Barrel is not; upstream has the identical
  test. The barrel has no renderer, so nothing else depends on it.
- The Oil Scanner has no ticking sound while it scans. 26.2 `Item.inventoryTick` is server-only, so
  upstream's client-side per-tick cue has no direct equivalent.
