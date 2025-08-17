package me.mgin.graves.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompassItem.class)
public abstract class CompassItemMixin {

    @Inject(
            method = "inventoryTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NbtCompound;remove(Ljava/lang/String;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void graves$skipLodestoneCheck(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("GraveMarker")) {
            // Cancel so that vanilla doesn't remove the LodestonePos tag
            ci.cancel();
        }
    }
}
