package me.mgin.graves.mixin.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mgin.graves.block.utility.Particles;
import me.mgin.graves.block.utility.Permission;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.block.decay.DecayStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void onUseBlock(ItemUsageContext context, CallbackInfoReturnable<?> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        Hand hand = context.getHand();
        Random random = new Random();

        if (blockEntity instanceof GraveBlockEntity graveEntity && Permission.playerCanAttemptRetrieve(player,
            graveEntity))
            if (hand == Hand.MAIN_HAND && (graveEntity.getNoDecay() == 1 || DecayStateManager.decreaseDecayState(world, pos))) {
                // Remove honeycomb
                graveEntity.setNoDecay(0);

                // Damage the item, respecting unbreaking enchant and creative
                float unbreaking = (float) EnchantmentHelper.getLevel(Enchantments.UNBREAKING, player.getActiveItem());
                float breakChance = ((100f / (unbreaking + 1f)) / 100f);

                if (!player.isCreative() && breakChance >= random.nextFloat()) {
                    player.getStackInHand(hand).damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                }

                // Spawn particles and sound in world
                Particles.spawnAtBlock(world, pos, ParticleTypes.WAX_OFF, 8, 3);
                world.playSound(null, pos, SoundEvents.BLOCK_ROOTED_DIRT_BREAK, SoundCategory.BLOCKS, 1f, 1f);
            }
    }

}
