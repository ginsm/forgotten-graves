# 3.2.11

Supports Minecraft versions: `1.20-1.20.4`

## Added
- Added a new `sink_through` tag to specify blocks that graves should sink through when spawning.
- You can rename crafted graves using name tags now. Simply right-click the grave with a renamed name tag in your main hand.
    - The name tag will be consumed in the process unless you're in creative mode.
    - You can still rename graves in the Anvil.
- Grave names can span multiple lines now; and you can force a new line with `&z`.
- You can set the head model of crafted graves to the Piglin Head model now. Simply right-click the grave with a Piglin Head in your main hand.
- Added Ukrainian translation (`uk-ua`), [thanks alexpuhach!](https://github.com/ginsm/forgotten-graves/pull/98)

## Changed
- The `replace` tag has been renamed to `replaceable`, and the list of blocks has been expanded.
- The collision and outline shapes have been reworked to better match the grave models.
- Item decay is no longer linear, it now follows an [S-curve](https://mgin.me/DecayRate.png) to better simulate realistic decay; [thanks Chaos02 for the suggestion!](https://github.com/ginsm/forgotten-graves/issues/44)
    - This change makes damaged items decay much quicker, so you may want to consider adjusting your `Decay Modifier` setting (default is `50`, down from `60`).