package me.mgin.graves.state;

import me.mgin.graves.Graves;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ServerState extends PersistentState {
    public HashMap<UUID, PlayerState> players = new HashMap<>();

    public static ServerState createFromNbt(NbtCompound tag) {
        ServerState serverState = new ServerState();

        // Extract every player's data from the provided tag
        NbtCompound playersTag = tag.getCompound("players");
        playersTag.getKeys().forEach(key -> {
            PlayerState playerState = new PlayerState();

            // Get graves and uuid from nbt
            playerState.graves = (NbtList) playersTag.getCompound(key).get("graves");
            UUID uuid = UUID.fromString(key);

            // Store data in server state instance
            serverState.players.put(uuid, playerState);
        });

        return serverState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // Store each player's data in a single nbt tag
        NbtCompound playersNbt = new NbtCompound();

        players.forEach((UUID, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.put("graves", playerData.graves);
            playersNbt.put(String.valueOf(UUID), playerNbt);
        });

        // Put all players nbt into the server state's nbt
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static ServerState getServerState(MinecraftServer server) {
        if (server == null) return null;

        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        return persistentStateManager.getOrCreate(
            ServerState::createFromNbt,
            ServerState::new,
            Graves.MOD_ID
        );
    }

    public static PlayerState getPlayerState(MinecraftServer server, UUID uuid) {
        // Get server state
        ServerState serverState = getServerState(server);

        // Get or create player state by UUID
        PlayerState playerState = serverState.players.computeIfAbsent(uuid, id -> new PlayerState());

        return playerState;
    }

    public static void storePlayerGrave(PlayerEntity player, GraveBlockEntity graveEntity) {
        if (graveEntity == null || player == null) return;

        MinecraftServer server = Objects.requireNonNull(graveEntity.getWorld()).getServer();

        // Get player state
        PlayerState playerState = getPlayerState(server, player.getUuid());

        // Remove all graves and cancel execution if storing graves is disabled
        if (GravesConfig.getConfig().server.storedGravesAmount == 0) {
            cleanupPlayerGraves(playerState);
            return;
        }

        // Convert GraveBlockEntity into nbt
        NbtCompound graveNbt = graveEntity.toNbt();

        // Store the grave's position in nbt
        BlockPos gravePos = graveEntity.getPos();
        graveNbt.putInt("x", gravePos.getX());
        graveNbt.putInt("y", gravePos.getY());
        graveNbt.putInt("z", gravePos.getZ());

        // Store the grave's dimension in nbt
        graveNbt.putString("dimension", String.valueOf(graveEntity.getWorld().getDimensionKey().getValue()));

        // Store the grave nbt in the global state
        playerState.graves.add(graveNbt);

        // Remove any old graves above the stored graves limit
        cleanupPlayerGraves(playerState);

        // Mark dirty to commit server state
        getServerState(server).markDirty();
    }

    private static void cleanupPlayerGraves(PlayerState playerState) {
        GravesConfig config = GravesConfig.getConfig();
        int storedGravesAmount = config.server.storedGravesAmount;
        int amountOfStoredGraves = playerState.graves.size();
        int difference = amountOfStoredGraves - storedGravesAmount;

        // The list goes from oldest to newest; thus removing the first entry as many times
        // as the difference between the two values will remove only old graves.
        if (difference > 0) {
            playerState.graves.subList(0, difference).clear();
        }
    }
}
