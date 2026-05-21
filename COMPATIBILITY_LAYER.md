# Minecraft 1.20.5 Compatibility Layer

Compatibility layer for API changes between Minecraft 1.20.4 and 1.20.5.

## Components

### BlockEntityCompat
- `readNbt(BlockEntity, NbtCompound)` – Handles WrapperLookup parameter in 1.20.5+
- `writeNbt(BlockEntity, NbtCompound)` – Handles WrapperLookup parameter in 1.20.5+
- `toNbt(BlockEntity)` – Creates NBT compound from BlockEntity

### ItemStackCompat
- `hasCustomName(ItemStack)` – Pre-1.20.5 vs Data Components
- `getNbt(ItemStack)` – NBT vs component-based storage
- `damage(ItemStack, int, LivingEntity, Hand)` – Method signature changes

### ProfileCompat
- `writeGameProfile(NbtCompound, GameProfile)` – NbtHelper changes
- `toGameProfile(NbtCompound)` – NbtHelper changes
- `toProfileComponent(GameProfile)` – ProfileComponent for 1.20.5+

### PersistentStateCompat
- `writeNbt(PersistentState, NbtCompound)` – WrapperLookup parameter
- `createFromNbt(NbtCompound, Function)` – Factory pattern
- `createFactory(Function)` – Wraps old-style factory

### WorldCompat
- `getDimensionKey(World)` – getDimensionKey vs getDimension/getRegistryKey

### SerializationHelper
- `getWrapperLookup()` – Registry lookup for serialization
- `writeGameProfile` / `readGameProfile` – GameProfile NBT
- `writeInventories` / `readInventories` – ItemStack serialization with WrapperLookup
- `serializeItemStack` – ItemStack to NBT (handles both formats)

## Usage

Replace direct API calls with compat layer calls:

```java
// BlockEntity
BlockEntityCompat.readNbt(blockEntity, nbt);
BlockEntityCompat.writeNbt(blockEntity, nbt);

// ItemStack
ItemStackCompat.hasCustomName(stack);
ItemStackCompat.getNbt(stack);

// GameProfile
ProfileCompat.writeGameProfile(nbt, profile);
ProfileCompat.toGameProfile(nbt);

// World
WorldCompat.getDimensionKey(world);
```

## Implementation

Uses reflection to detect and call the appropriate method per Minecraft version. Tries 1.20.5+ signatures first, falls back to pre-1.20.5.
