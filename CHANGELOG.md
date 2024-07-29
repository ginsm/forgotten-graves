# 3.2.13

Supports Minecraft versions: `1.20-1.20.4`

## Updated
- Updated the lang files to include translations for the `<player> died on <date>` string.
- mpustovoi updated the `ru_ru` translation to fix some typos and update some lines, thanks!

## Fixed
- The date and time shown when punching a player-owned grave should now be in the player's local time.
- Graves should no longer spawn in spawn protected areas; items and xp will drop as normal.
- Unowned graves should always be breakable, even when the Retrieval Type is set to `USE`.
- Minecraft's `/setblock` command can now replace graves (both player-owned and unowned).