# 3.2.10

## Added
- Forgotten Graves now leverages tags to denote which blocks should and shouldn't be replaced while spawning graves (tags: `replace` and `do_not_replace`).

## Fixed
- Fixed issue [#88](https://github.com/ginsm/forgotten-graves/issues/88): Create's drill block no longer destroy graves.
- Fixed issue [#96](https://github.com/ginsm/forgotten-graves/issues/96): Graves are now more resilient to being removed by other mods in general.

## Changed
- The grave placement algorithm now searches a larger area for an optimal placement position if the initial position is suboptimal.