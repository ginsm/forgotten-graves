package me.mgin.graves.mixin;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.block.utility.PlaceGrave;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.effects.GraveEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow
    @Final
    private PlayerInventory inventory;

    @Shadow protected abstract void vanishCursedItems();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void dropAll(CallbackInfo ci) {
        PlayerEntity player = this.inventory.player;
        GameProfile profile = player.getGameProfile();

        // Do not drop the inventory or place a grave if the player is still alive.
        // This is needed for possession mods like RAT's Mischief, Requiem (Origins), etc.
        if (player.isAlive()) return;

        // Graves will not spawn if they're not enabled or the location is within a protected area (such as spawn).
        boolean forgottenGravesEnabled = GravesConfig.resolve("graves", profile);
        boolean playerCanPlaceBlocks = player.canModifyAt(player.getWorld(), player.getBlockPos());

        // Graves will not spawn when killed by a player if disableInPvP is true.
        boolean disabledInPvP = GravesConfig.resolve("disableInPvP", profile);
        boolean preventedInPvP = disabledInPvP && (player.getLastAttacker() instanceof PlayerEntity);

        // Players with DISABLE_GRAVES_EFFECT active will not have a grave spawn.
        boolean preventedByEffect = player.hasStatusEffect(GraveEffects.DISABLE_GRAVES_EFFECT);

        // Graves will not spawn if respectKeepInventory is set to true.
        boolean keepInventory = this.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
        boolean respectKeepInventory = GravesConfig.resolve("respectKeepInventory", profile);
        boolean preventedByKeepInventory = keepInventory && respectKeepInventory;

        // Read above comments for each conditional
        boolean shouldPlaceGrave = forgottenGravesEnabled &&
            playerCanPlaceBlocks &&
            !preventedInPvP &&
            !preventedByEffect &&
            !preventedByKeepInventory;

        if (shouldPlaceGrave) {
            PlaceGrave.place(this.getWorld(), this.getPos(), player);
        }
    }
}
