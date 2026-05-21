package me.mgin.graves.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Conditionally excludes mixins that are incompatible with certain Minecraft versions.
 * CompassItemMixin is excluded on 1.20.5+ because CompassItem uses data components
 * instead of NbtCompound.remove() for lodestone tracking.
 */
public class GravesMixinPlugin implements IMixinConfigPlugin {

    private static final boolean IS_1_20_5_OR_NEWER = is1205OrNewer();

    private static boolean is1205OrNewer() {
        try {
            String version = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .orElseThrow()
                .getMetadata()
                .getVersion()
                .getFriendlyString();
            return version.startsWith("1.20.5") || version.startsWith("1.21");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (IS_1_20_5_OR_NEWER && mixinClassName.endsWith("CompassItemMixin")) {
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
