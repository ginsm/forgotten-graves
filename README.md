# Forgotten Graves

Forgotten Graves is a highly configurable fabric mod that adds a grave which stores items and XP upon death; the grave will then begin to age at random intervals between 4 different stages.

The graves are craftable, support custom names & heads, and can be locked at any age stage (via honeycomb). You can easily go back a stage or remove the stage lock by right clicking the grave with a shovel.

This mod has support for [Trinkets API](https://www.curseforge.com/minecraft/mc-mods/trinkets-fabric)!

If you have any issues with the mod or a feature request, please use the [issue tracker](https://github.com/ginsm/forgotten-graves/issues). Any feedback is welcomed, thank you!


 

**Grave Models:**

![Grave Models](docs/screenshots/GraveModels.gif)

 

**GUI Configuration:**

![Forgotten Graves Config](docs/screenshots/ConfigScreen.png)

 

**Configuration (Defaults):**

```
{
  "client": {
    "enableGraves": true,
    "sendGraveCoordinates": true,
    "retrievalType": "ON_BOTH",
    "dropType": "PUT_IN_INVENTORY",
    "expStorageType": "STORE_ALL_XP",
    "maxCustomXPLevel": 30
  },
  "server": {
    "enableGraveRobbing": false,
    "minOperatorOverrideLevel": 4,
    "clientSideOptions": ""
  }
}
```


*Note: Server-side configuration overrides any user set configuration currently.*

 

## Want to help out?
I'm looking for someone who's good at creating block models and textures (as I'm not very good at it) for the biome specific model/texture feature; if that sounds like something you would like to do then please leave a comment on [this issue](https://github.com/ginsm/forgotten-graves/issues/7). Thank you!

 
## Credits

This mod is heavily based on both [Graves Not Forgotten](https://www.curseforge.com/minecraft/mc-mods/not-forgotten) by `SilverGleam` and [Gravestones](https://www.curseforge.com/minecraft/mc-mods/gravestones) by `geometrically_`.

 
## Disclaimer

I'm mostly working on this mod for my own server's personal use, and I'm new to modding, so I cannot promise that all requested functionality will be added (i.e. compatibility with ___ mod). I'll still try though!

 
## Contributing

I still need to write up a contribution guide for this project; however, if you want to tackle one of the issues in the [issue tracker](https://github.com/ginsm/forgotten-graves/issues) then please feel free to and send a pull request!

 

Want to show your support?

<a href="https://www.buymeacoffee.com/mgin"><img src="https://img.buymeacoffee.com/button-api/?text=Buy me a coffee&emoji=&slug=mgin&button_colour=5F7FFF&font_colour=ffffff&font_family=Cookie&outline_colour=000000&coffee_colour=FFDD00"></a>
