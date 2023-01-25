## 1.19-3.0.2
- Fix: Right-clicking on a waxed grave, without an item that can cause decay, should not cause "this grave has been waxed" error message.

## 1.19-3.0.1
- Fix: CurseForge only offers 0.14.12 as the highest fabric loader version for 1.19. The mod now requires ">=0.14.0" instead of "0.14.13".

## 1.19-3.0.0

- New: Updated to Minecraft v1.19.
- New: Added commands to manipulate the client and server configurations.
- New: Graves can now "sink" in different mediums (Air/Water/Lava). Read about that [here](https://github.com/ginsm/forgotten-graves/wiki/Graves#q-why-does-my-grave-sink-when-i-die-in-the-air-or-water).
- Update: Some configuration option names have been reworked.
- Update: Some configuration option values have been reworked.
- Update: The wiki has been updated in its entirety.
- Fix: Your inventory should no longer drop when retrieving a game with `dropType` set to `"DROP"`. Only the items in the grave will drop.
- Fix: There was an edge case where armor could sometimes be duplicated. This has been fixed.
- Fix: Graves should no longer lose GraveEntity data on the client; this was occurring when you didn't have permission to break the grave.
