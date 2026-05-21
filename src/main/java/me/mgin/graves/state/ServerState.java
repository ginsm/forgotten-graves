package me.mgin.graves.state;

import me.mgin.graves.Graves;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.versioned.VersionedCode;
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

    //? if >=1.20.5 {
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup lookup) {
        return writeNbtCustom(nbt);
    }
    //?} else {
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return writeNbtCustom(nbt);
    }
    //?}

    private NbtCompound writeNbtCustom(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.put("graves", playerData.graves);
            playersNbt.put(String.valueOf(uuid), playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }

    //? if >=1.20.5 {
    private static final net.minecraft.world.PersistentState.Type<ServerState> TYPE = new net.minecraft.world.PersistentState.Type<>(
        ServerState::new,
        (nbt, lookup) -> ServerState.createFromNbt(nbt),
        null
    );
    //?}
    //? if >=1.20.2 && <1.20.5 {
    private static final Type<ServerState> type = new Type<>(
        ServerState::new,
        ServerState::createFromNbt,
        null
    );
    //?}

    public static ServerState getServerState(MinecraftServer server) {
        if (server == null) return null;

        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        //? if >=1.20.5 {
        return persistentStateManager.getOrCreate(TYPE, Graves.MOD_ID);
        //?} else if >=1.20.2 {
        return persistentStateManager.getOrCreate(type, Graves.MOD_ID);
        //?} else {
        return persistentStateManager.getOrCreate(ServerState::createFromNbt, ServerState::new, Graves.MOD_ID);
        //?}
    }

    public static PlayerState getPlayerState(MinecraftServer server, UUID uuid) {
        // Get server state
        ServerState serverState = getServerState(server);

        // Get or create player state by UUID
        return serverState.players.computeIfAbsent(uuid, id -> new PlayerState());
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
        graveNbt.putString("dimension", VersionedCode.Worlds.getDimensionKey(graveEntity.getWorld()));

        // Store the grave nbt in the global state
        playerState.graves.add(graveNbt);

        // Remove any old graves above the stored graves limit
        cleanupPlayerGraves(playerState);

        // Mark dirty to commit server state
        Objects.requireNonNull(getServerState(server)).markDirty();
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
