# 3.2.21

Supports Minecraft versions: `1.20-1.20.4`

## Updated
- The experience storage system has been overhauled. Here's a few things to note:
  - Graves that already exist in the world will automatically swap to the new data format, preserving the experience in them.
  - Graves can now store much more experience. It could store roughly 21863 levels worth of experience before; it can now store hundreds of millions of levels.
  - Graves should now be compatible with mods like Linear Levels, Linear XP, and Custom XP Scaling that add custom level scaling to the game ([#113](https://github.com/ginsm/forgotten-graves/issues/113)).
  - The accuracy has been improved and should now reliably give an exact amount of experience points. The previous code would sometimes result in an extra point or two of experience (due to a bug).

If you run into any bugs, please report them using [GitHub issues](https://github.com/ginsm/forgotten-graves/issues/), thank you!