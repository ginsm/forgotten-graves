package me.mgin.graves.mixin;

import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.effects.GraveEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow
    @Final
    private PlayerInventory inventory;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerInventory.dropAll()V"))
    private void dropAll(PlayerInventory inventory) {
        // Do not drop the inventory or place a grave if the player is still alive.
        // This is needed for possession mods like RAT's Mischief, Requiem (Origins), etc.
        PlayerEntity player = this.inventory.player;
        if (player.isAlive()) return;

        // Graves will not spawn if they're not enabled or the location is within a protected area (such as spawn).
        boolean forgottenGravesEnabled = GravesConfig.resolve("graves", player.getGameProfile());
        boolean playerCanPlaceBlocks = player.canModifyAt(player.getWorld(), player.getBlockPos());

        // Graves will not spawn when killed by a player if disableInPvP is true.
        boolean disabledInPvP = GravesConfig.resolve("disableInPvP", player.getGameProfile());
        boolean killedByPlayer = player.getLastAttacker() instanceof PlayerEntity;

        // Players with DISABLE_GRAVES_EFFECT active will not have a grave spawn.
        boolean hasDisableGravesEffect = player.hasStatusEffect(GraveEffects.DISABLE_GRAVES_EFFECT);

        // Read above comments for each conditional
        if (!forgottenGravesEnabled || !playerCanPlaceBlocks || (disabledInPvP && killedByPlayer) || hasDisableGravesEffect) {
            this.inventory.dropAll();
            return;
        }

        PlaceGrave.place(this.getWorld(), this.getPos(), player);
    }
}
