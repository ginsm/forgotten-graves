# 3.2.16

Supports Minecraft versions: `1.20-1.20.4`

This version adds a lot of changes to the config and its GUI, as such, please reset and reconfigure the mod if you run into any issues. If that doesn't solve your issue, please [report the issue](https://github.com/ginsm/forgotten-graves/issues/new?assignees=&labels=bug&projects=&template=bug_report.md&title=%5BBUG%5D) on GitHub, thanks!

## Added
- Added new config options to configure the chance a grave will decay for each stage of decay ([#77](https://github.com/ginsm/forgotten-graves/issues/77)).
- Added new config options to configure the min and max amount of seconds a grave stays in each stage ([#77](https://github.com/ginsm/forgotten-graves/issues/77)).
  - You can set the max amount of seconds to `0` to disable the feature.
- Added new config options to toggle whether graves can replace or sink through blocks ([#108](https://github.com/ginsm/forgotten-graves/issues/108)).
- Added a new `Disable Graves` effect that will prevent graves from spawning for any player affected by it ([#89](https://github.com/ginsm/forgotten-graves/issues/89)).

## Updated
- The configuration GUI has been segmented into different categories (located at the top of the config GUI).
- Configuration option values are now better validated to ensure no invalid values are provided.

## Fixed
- Graves will no longer replace torches ([#106](https://github.com/ginsm/forgotten-graves/issues/106)).
- The `do_not_replace` tag now supersedes the `replaceable` tag; blocks found within both will not be replaced ([#107](https://github.com/ginsm/forgotten-graves/issues/107)).