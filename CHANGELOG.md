# 3.2.24

Supports Minecraft versions: `1.20-1.20.4`

### NOTICE: This release will cause your configuration file to regenerate.

## Added
- Added Death Compasses; they are disabled by default -- you can enable them via the `giveDeathCompass` option in the `Spawning` category.
  - These compasses will point towards your last grave and will be given to you upon respawning.
  - These compasses will be removed from your inventory upon retrieving their respective grave.
- Added a `graves config applyToServer` command; this command applies your config to the server.

## Changed
- Replaced `graves server config <reload|reset>` command with `graves config <reload|reset> server` (when operator).
- The `graves list` command now has two branches:
  - `graves list <page>`
  - `graves list <player> <page> [recipient]` (operator only)
- The config option `graveCoordinates` now properly applies to server logs.
  - Disable this option on the server to prevent logging to the console whenever a grave is created.

## Removed
- Removed the `respectKeepInventory` option (previously in the main configuration category).
  - It provided the same exact functionality as disabling grave spawning when keep inventory is enabled.

If you find any bugs, please report them on [GitHub issues](https://github.com/ginsm/forgotten-graves/issues/), thank you!