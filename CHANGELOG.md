# 3.2.17

Supports Minecraft versions: `1.20-1.20.4`

## Added
- Added support for the [Forgotten Graves Redefined](https://www.curseforge.com/minecraft/texture-packs/forgotten-graves-redefined) resource pack. Here are some of the things that now happen:
  - Player names and custom text shows up on the models properly.
  - The outline shape changes to match the custom models.
  - The player skull shifts slightly away from the tombstone.

## Fixed
- The config option `maxStageTimeSeconds` now respects waxed graves ([#110](https://github.com/ginsm/forgotten-graves/issues/110)).
- Trinket items that fail to find an appropriate slot whilst equipping are now placed in your main inventory.
- After a grave is placed, any items remaining in the player inventory should now drop on the ground.
  - The goal with this change is to drop any items the code may have missed on the ground, rather than just destroying them.

## Updated
- Updated `en_us.json` and `ru_ru.json` translations thanks to mpustovoi ([#109](https://github.com/ginsm/forgotten-graves/pull/109))!
- Updated `es_mx.json` translation; let me know if there's any mistakes, I'm not a native speaker, thanks!
- Grave item models should now look better in first person, third person, and in item frames.
