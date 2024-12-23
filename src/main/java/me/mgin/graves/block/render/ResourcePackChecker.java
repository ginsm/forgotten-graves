package me.mgin.graves.block.render;

import me.mgin.graves.Graves;
import me.mgin.graves.block.render.packs.DefaultPack;
import me.mgin.graves.block.render.packs.RedefinedPack;
import me.mgin.graves.block.render.packs.ResourcePack;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourcePackChecker implements SimpleResourceReloadListener<Void> {
    public static ResourcePack activePack = new DefaultPack();

    public static void initialize() {
        // Register this listener to handle resource reload events
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ResourcePackChecker());
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
        activePack = new DefaultPack();
        return CompletableFuture.runAsync(() -> {
            updateResourcePackStatus(new RedefinedPack());
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Graves.MOD_ID, "resource_pack_checker");
    }


    /**
     * Updates the active pack with the last active pack.
     *
     * @param pack - Class representing a resource pack.
     */
    public void updateResourcePackStatus(ResourcePack pack) {
        List<String> files = pack.getFiles();
        if (files.isEmpty() || files.stream().allMatch(ResourcePackChecker::isResourcePackFilePresent)) {
            activePack = pack;
        }
    }

    /**
     * Checks the resource manager for the given file path.
     *
     * @param filePath - The path to a file within the resource pack.
     * @return A boolean representing the existence of the resource.
     */
    private static boolean isResourcePackFilePresent(String filePath) {
        return MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(Graves.MOD_ID, filePath)).isPresent();
    }

    public static ResourcePack getActivePack() {
        return activePack;
    }
}
