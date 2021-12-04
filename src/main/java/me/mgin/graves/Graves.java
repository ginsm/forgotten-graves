package me.mgin.graves;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import me.mgin.graves.api.GravesApi;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.util.ExperienceCalculator;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.registry.GraveBlocks;
import me.mgin.graves.registry.registerBlocks;
import me.mgin.graves.registry.registerEvents;
import me.mgin.graves.registry.registerItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class Graves implements ModInitializer {

	public static final ArrayList<GravesApi> apiMods = new ArrayList<>();
	public static String MOD_ID = "forgottengraves";
	public static String BRAND_BLOCK = "grave";

	@Override
	public void onInitialize() {
		registerBlocks.register(MOD_ID, BRAND_BLOCK);
		registerItems.register(MOD_ID, BRAND_BLOCK);
		registerEvents.register();
		AutoConfig.register(GravesConfig.class, GsonConfigSerializer::new);
		apiMods.addAll(FabricLoader.getInstance().getEntrypoints(MOD_ID, GravesApi.class));
	}

	public static void placeGrave(World world, Vec3d pos, PlayerEntity player) {
		if (world.isClient)
			return;

		BlockPos blockPos = new BlockPos(pos.x, pos.y - 1, pos.z);

		DefaultedList<ItemStack> combinedInventory = DefaultedList.of();

		combinedInventory.addAll(player.getInventory().main);
		combinedInventory.addAll(player.getInventory().armor);
		/*
		 * if (){//Compat Inventories. when I don't need to sleep lol
		 * combinedInventory.addAll(player.getInventory().armor); }
		 */
		combinedInventory.addAll(player.getInventory().offHand);

		for (GravesApi GravesApi : Graves.apiMods) {
			combinedInventory.addAll(GravesApi.getInventory(player));
		}

		if (blockPos.getY() < 0) {
			blockPos = new BlockPos(blockPos.getX(), 10, blockPos.getZ());
		}

		for (BlockPos gravePos : BlockPos.iterateOutwards(blockPos.add(new Vec3i(0, 1, 0)), 5, 5, 5)) {
			BlockState blockState = world.getBlockState(gravePos);
			Block block = blockState.getBlock();

			if (canPlaceGrave(world, block, gravePos)) {
				world.setBlockState(gravePos, Graves.GRAVE.getDefaultState().with(Properties.HORIZONTAL_FACING,
						player.getHorizontalFacing()));

				GraveBlockEntity graveBlockEntity = new GraveBlockEntity(gravePos, world.getBlockState(gravePos));

				graveBlockEntity.setItems(combinedInventory);
				graveBlockEntity.setGraveOwner(player.getGameProfile());

				int experience = ExperienceCalculator.calculateExperienceStorage(player.experienceLevel,
						player.experienceProgress);

				graveBlockEntity.setXp(experience);
				player.totalExperience = 0;
				player.experienceProgress = 0;
				player.experienceLevel = 0;
				world.addBlockEntity(graveBlockEntity);

				if (world.isClient())
					graveBlockEntity.sync(world, gravePos);
				block.onBreak(world, blockPos, blockState, player);

				if (GravesConfig.getConfig().mainSettings.sendGraveCoordinates) {
					player.sendMessage(new TranslatableText("text.forgottengraves.mark_coords", gravePos.getX(),
							gravePos.getY(), gravePos.getZ()), false);
				}

				System.out.println("[Graves] Grave spawned at: " + gravePos.getX() + ", " + gravePos.getY() + ", "
						+ gravePos.getZ() + " for player " + player.getName().asString() + ".");

				break;
			}
		}
	}

	private static boolean canPlaceGrave(World world, Block block, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);

		if (blockEntity != null)
			return false;
		// Check if it's in a blacklisted/non-whitelisted dimension
		// Check if it's in a blacklisted/non-whitelisted biome

		Set<Block> blackListedBlocks = new HashSet<Block>() {
			{
				add(Blocks.BEDROCK);
			}
		};

		if (blackListedBlocks.contains(block))
			return false;

		return !(blockPos.getY() < 0 || blockPos.getY() > 255);
	}
}
