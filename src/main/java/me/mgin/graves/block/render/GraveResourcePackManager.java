package me.mgin.graves.block.render;

import me.mgin.graves.Graves;
import me.mgin.graves.block.render.packs.DefaultPack;
import me.mgin.graves.block.render.packs.RedefinedPack;
import me.mgin.graves.block.render.packs.GraveResourcePack;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GraveResourcePackManager implements SimpleResourceReloadListener<Void> {
    public static GraveResourcePack defaultPack = new DefaultPack();
    public static GraveResourcePack redefinedPack = new RedefinedPack();
    public static GraveResourcePack activePack = defaultPack;
    public static Map<String, GraveResourcePack> resourcePacks = new HashMap<>();

    public static void initialize() {
        // Forgotten Graves Redefined
        resourcePacks.put("file/Forgotten Graves Redefined.zip", redefinedPack);

        // Register this listener to handle resource reload events
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new GraveResourcePackManager());
    }

    // ResourceReloadListener methods
    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer helper, ResourceManager manager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor) {
        return load(manager, loadProfiler, loadExecutor).thenCompose(helper::whenPrepared).thenCompose(
            (o) -> apply(o, manager, applyProfiler, applyExecutor)
        );
    }

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {
        activePack = defaultPack;

        return CompletableFuture.runAsync(() ->
            MinecraftClient.getInstance().getResourceManager().streamResourcePacks().forEach((pack) -> {
                String filePath = pack.getName();
                if (resourcePacks.containsKey(filePath)) {
                    activePack = resourcePacks.get(filePath);
                }
            }), executor
        );
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Graves.MOD_ID, "resource_pack_checker");
    }

    public static GraveResourcePack getActivePack() {
        return activePack;
    }
}
