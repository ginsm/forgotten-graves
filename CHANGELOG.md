# 3.2.19

Supports Minecraft versions: `1.20-1.20.4`

## Added
- Added a new configuration option, `respectKeepInventory`, which is set to `false` by default.
  - When `true`: The mod will honor Minecraft's `keepInventory` gamerule - no graves will spawn if the rule is enabled. All items will remain on the player.
  - When `false`: Graves will always spawn regardless of the `keepInventory` gamerule, storing items from supported inventories (Vanilla, Trinkets, BackSlot, Inventorio). Items inside unsupported inventories will remain on the player.
  - *Note: If the game's `keepInventory` rule is disabled, and you have unsupported mod inventories, items from those inventories will drop on the ground instead.*

## Fixed
- Fixed compatibility issue with Better Combat where off-hand items could sometimes be lost when retrieving graves containing two-handed weapons.