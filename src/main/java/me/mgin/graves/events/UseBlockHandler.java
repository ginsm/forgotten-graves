package me.mgin.graves.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import me.mgin.graves.block.api.Particles;
import me.mgin.graves.block.entity.GraveBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

		if (blockEntity instanceof GraveBlockEntity graveEntity) {
			ItemStack itemStack = player.getStackInHand(hand);
			Item itemInHand = itemStack.getItem();

			if (!player.isSneaking()) {
				String itemName = itemInHand.asItem().toString();

				if (graveEntity.getGraveOwner() == null && validSkulls.contains(itemName))
					return handlePlayerHeads(graveEntity, world, itemStack, itemInHand, itemName);

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
			put(Items.FLINT_AND_STEEL, Items.FLINT_AND_STEEL.asItem());
		}
	};

	/**
	 * This method determines the type of head (normal head or custom player head)
	 * and assigns it to the clicked on GraveBlockEntity so that it can be
	 * displayed.
	 * <p>
	 *
	 * @param graveEntity
	 * @param world
	 * @param itemStack
	 * @param itemInHand
	 * @param itemName
	 * @return
	 */
	private static ActionResult handlePlayerHeads(GraveBlockEntity graveEntity, World world, ItemStack itemStack,
			Item itemInHand, String itemName) {
		NbtCompound baseNbt = itemStack.getNbt();
		BlockPos pos = graveEntity.getPos();
		BlockState state = graveEntity.getState();
		String graveSkull;

		if (baseNbt != null) {
			NbtCompound startCompound = baseNbt.contains("tag") ? baseNbt.getCompound("tag") : baseNbt;

			graveSkull = startCompound.getCompound("SkullOwner").getCompound("Properties").getList("textures", 10)
					.getCompound(0).getString("Value");

			if (graveSkull != "" && graveSkull != graveEntity.getGraveSkull())
				graveEntity.setGraveSkull(graveSkull);
			else
				return ActionResult.FAIL;
		} else {
			graveEntity.setGraveSkull(itemName);
		}

		// Required for client sync
		world.updateListeners(pos, state, state, Block.NOTIFY_ALL);

		// Polish
		world.playSound(null, pos, SoundEvents.BLOCK_ROOTED_DIRT_HIT, SoundCategory.BLOCKS, 1f, 1f);
		Particles.spawnAtBlockBottom(world, pos, ParticleTypes.SOUL, 6, 0.025, 0.125);

		return ActionResult.SUCCESS;
	}

	public static HashSet<String> validSkulls = new HashSet<>() {
		{
			add("wither_skeleton_skull");
			add("skeleton_skull");
			add("player_head");
			add("zombie_head");
			add("creeper_head");
		}
	};

}
