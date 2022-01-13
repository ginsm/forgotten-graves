package me.mgin.graves.mixin;

import me.mgin.graves.Graves;
import me.mgin.graves.compat.TrinketsCompat;
import me.mgin.graves.config.GravesConfig;
import net.fabricmc.loader.api.FabricLoader;
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
	public PlayerInventory inventory;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerInventory.dropAll()V"))
	private void dropAll(PlayerInventory inventory) {
		boolean forgottenGravesEnabled = GravesConfig.getConfig().mainSettings.enableGraves;

		if (!forgottenGravesEnabled) {
			this.inventory.dropAll();
			return;
		}

		Graves.placeGrave(this.world, this.getPos(), this.inventory.player);

		if (FabricLoader.getInstance().isModLoaded("trinkets"))
			TrinketsCompat.clearInventory((PlayerEntity) (Object) this);
	}
}
