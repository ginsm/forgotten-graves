# 3.2.18

Supports Minecraft versions: `1.20-1.20.4`

## Added
- Added a configuration option to determine the merge order when retrieving graves. You're able to pick from two options: `GRAVE` and `CURRENT`.
    - `GRAVE` will result in the inventory returning to what it was before you died, and then merging in any items you had while retrieving the grave.
    - `CURRENT` will result in the items in your grave being merged into the inventory you currently have while retrieving the grave.

## Updated
- The merge code now tries to fill empty equipment slots with previously equipped gear from both inventories (respecting merge order).
- When retrieving graves, items will now be consolidated into existing stacks within the base inventory (respecting NBT tags).
- Custom models will now use the same collision box as the default models to prevent issues between the client and server disagreeing about collision.
- The method of identifying loaded resource packs and their load order has been improved.
- Updated translations `en_us.json` and `es_mx.json`.

## Fixed
- Fixed an issue causing unintentional block placement when retrieving graves ([#112](https://github.com/ginsm/forgotten-graves/issues/112)).