# 3.2.12

Supports Minecraft versions: `1.20-1.20.4`

## Added
- Grave names now support Minecraft's [formatting codes](https://minecraft.fandom.com/wiki/Formatting_codes) using the prefix `&` instead of `§`.
  - This also adds better optimization for the new text renderer.
- You can now see the date and time a player died by punching a player-owned grave.
- Added a new `decay_item` tag to specify which blocks can be used to decay graves.

## Fixed
- Fixed a bug that could cause a server crash, and no grave to be created, if a player died before they finished logging in.