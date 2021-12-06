package me.mgin.graves.events;

import java.util.HashMap;
import java.util.Map;

import me.mgin.graves.api.ParticlesApi;
import me.mgin.graves.api.SkullApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UseBlockHandler {

	public static ActionResult handleEvent(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());

		if (blockEntity instanceof GraveBlockEntity graveBlockEntity) {
			ItemStack itemStack = player.getStackInHand(hand);
			Item itemInHand = itemStack.getItem();

			if (!player.isSneaking()) {
				String itemName = itemInHand.asItem().toString();

				if (graveBlockEntity.getGraveOwner() == null && SkullApi.skulls.containsKey(itemName))
					return handlePlayerHeads(graveBlockEntity, world, itemStack, itemInHand, itemName);

				// Prevent block placement and unintentional item usage
				if (Item.BLOCK_ITEMS.containsValue(itemInHand) || restrictedItems.containsValue(itemInHand))
					return ActionResult.FAIL;
			}
		}

		return ActionResult.PASS;
	}

	/**
	 * Items that should be prevented from activating. Mostly blocks.
	 */
	private static Map<Item, Item> restrictedItems = new HashMap<Item, Item>() {
		{
			put(Items.LAVA_BUCKET, Items.LAVA_BUCKET.asItem());
			put(Items.WATER_BUCKET, Items.WATER_BUCKET.asItem());
			put(Items.FLINT_AND_STEEL, Items.FLINT_AND_STEEL.asItem());
		}
	};

	/**
	 * This method determines the type of head (normal head or custom player head) and
	 * assigns it to the clicked on GraveBlockEntity so that it can be displayed.
	 * <p>
	 * 
	 * @param graveBlockEntity
	 * @param world
	 * @param itemStack
	 * @param itemInHand
	 * @param itemName
	 * @return
	 */
	private static ActionResult handlePlayerHeads(GraveBlockEntity graveBlockEntity, World world, ItemStack itemStack,
			Item itemInHand, String itemName) {
		NbtCompound baseNbt = itemStack.getNbt();
		BlockPos pos = graveBlockEntity.getPos();
		String skinURL;

		if (baseNbt != null) {
			NbtCompound startCompound = baseNbt.contains("tag") ? baseNbt.getCompound("tag") : baseNbt;

			skinURL = startCompound.getCompound("SkullOwner").getCompound("Properties").getList("textures", 10)
					.getCompound(0).getString("Value");

			if (skinURL != "" && skinURL != graveBlockEntity.getSkinURL())
				graveBlockEntity.setSkinURL(skinURL);
			else
				return ActionResult.FAIL;
		} else {
			graveBlockEntity.setSkinURL(itemName);
		}

		world.playSound(null, pos, SoundEvents.BLOCK_ROOTED_DIRT_HIT, SoundCategory.BLOCKS, 1f, 1f);
		ParticlesApi.spawnAtBlockBottom(world, pos, ParticleTypes.SOUL, 6, 0.025, 0.125);

		return ActionResult.SUCCESS;
	}

}
