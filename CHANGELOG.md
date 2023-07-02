## 1.20-3.2.0

### IMPORTANT
This update largely revolves around sprucing up the config. As such, it would be a good idea to reconfigure the mod (especially for servers).

### Added
- New experience settings, see [here](https://github.com/ginsm/forgotten-graves/wiki/Config#experience-settings).
- New `decayEnabled` option. Allows you to toggle natural decay.
- New `decayRobbing` option. Allows you to set which stage of decay the graves need to be at before they can be robbed via `graveRobbing`.
- New `shiftSwapsDropType` option (default `true`). Allows you to briefly switch between `DROP` and `EQUIP` by sneaking whilst retrieving graves.

### Updated
- Floating settings have been renamed to sink settings; i.e. `floatInWater` is now `sinkInWater`.
- `decayModifier` now defaults to `60`.
- `dropType`'s `INVENTORY` value has been renamed to `EQUIP`.
- Chat messages should now be formatted consistently.