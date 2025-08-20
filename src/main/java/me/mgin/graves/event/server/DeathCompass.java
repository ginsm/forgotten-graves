package me.mgin.graves.event.server;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.api.InventoriesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.state.PlayerState;
import me.mgin.graves.state.ServerState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeathCompass {
    public static void give(ServerPlayerEntity oldPlayer, ServerPlayerEntity player, boolean alive) {
        GameProfile profile = player.getGameProfile();
        boolean giveDeathCompass = GravesConfig.resolve("giveDeathCompass", profile);
        boolean gravesEnabled = GravesConfig.resolve("graves", profile);
        PlayerState playerState = null;

        if (!giveDeathCompass) return;

        if (gravesEnabled) {
            playerState = ServerState.getPlayerState(player.getServer(), profile.getId());
        }

        ItemStack compass = createDeathCompass(player, playerState);

        if (!compass.isEmpty()) {
            if (!player.giveItemStack(compass)) {
                player.dropItem(compass, true);
            }
        }
    }

    private static ItemStack createDeathCompass(ServerPlayerEntity player, PlayerState state) {
        ItemStack compass = new ItemStack(Items.COMPASS);
        NbtCompound tag = compass.getOrCreateNbt();
        List<Text> lore = new ArrayList<>();

        BlockPos pos = null;
        String dimension = null;

        // State will be null if graves wasn't enabled (see Events.java)
        if (state != null && !state.graves.isEmpty()) {
            NbtCompound graveNbt = (NbtCompound) state.graves.get(state.graves.size() - 1);
            tag.putLong("GraveMarker", graveNbt.getLong("mstime")); // Matches compass to respective grave
            pos = new BlockPos(graveNbt.getInt("x"), graveNbt.getInt("y"), graveNbt.getInt("z"));
            dimension = graveNbt.getString("dimension");
        }
        // Otherwise use the last death position
        else {
            Optional<GlobalPos> lastDeath = player.getLastDeathPos();
            if (lastDeath.isPresent()) {
                GlobalPos globalPos = lastDeath.get();
                pos = globalPos.getPos();
                dimension = globalPos.getDimension().getValue().toString();
                tag.putLong("GraveMarker", 0L); // See ItemCompassMixin, allows for Lodestone behavior without Lodestone
            }
        }

        if (pos == null) return ItemStack.EMPTY;

        tag.put("LodestonePos", NbtHelper.fromBlockPos(pos));
        tag.putString("LodestoneDimension", dimension);
        tag.putBoolean("LodestoneTracked", true);

        lore.add(Text.translatable("item.forgotten_graves.death_compass.lore.dimension_warning"));
        lore.add(Text.translatable("grave.coordinates", pos.getX(), pos.getY(), pos.getZ()).formatted(Formatting.DARK_GRAY));
        lore.add(Text.literal("(" + dimension + ")").formatted(Formatting.DARK_GRAY));

        setLore(compass, lore);
        compass.setCustomName(Text.literal("Death Compass").formatted(Formatting.GOLD));

        return compass;
    }

    public static void removeCompassFromInventory(PlayerEntity player, InventoriesApi api, GraveBlockEntity entity) {
        DefaultedList<ItemStack> inventory = api.getInventory(player);
        long mstime = entity.getMstime();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack item = inventory.get(i);
            if (item.getItem() instanceof CompassItem) {
                if (item.hasNbt()) {
                    NbtCompound nbt = item.getNbt();
                    if (nbt != null && nbt.contains("GraveMarker")) {
                        long graveMarker = nbt.getLong("GraveMarker");
                        if (graveMarker == mstime) {
                            inventory.set(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }

        api.clearInventory(player, false);
        api.setInventory(inventory, player, false);
    }

    // TODO - Move to NbtHelper (and rename NbtHelper to GraveNbtHelper)
    public static void setLore(ItemStack stack, List<Text> lines) {
        NbtList loreList = new NbtList();
        for (Text line : lines) {
            loreList.add(NbtString.of(Text.Serializer.toJson(line)));
        }
        stack.getOrCreateSubNbt("display").put("Lore", loreList);
    }
}
