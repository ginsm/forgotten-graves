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

	public static void placeGrave(World world, Vec3d vecPos, PlayerEntity player) {
		if (world.isClient)
			return;

		BlockPos pos = new BlockPos(vecPos.x, vecPos.y - 1, vecPos.z);

		DefaultedList<ItemStack> combinedInventory = DefaultedList.of();

		combinedInventory.addAll(player.getInventory().main);
		combinedInventory.addAll(player.getInventory().armor);
		combinedInventory.addAll(player.getInventory().offHand);

		for (GravesApi GravesApi : Graves.apiMods) {
			combinedInventory.addAll(GravesApi.getInventory(player));
		}

		// Lowest Y Height
		if (pos.getY() < -64) {
			pos = new BlockPos(pos.getX(), -60, pos.getZ());
		}

		for (BlockPos gravePos : BlockPos.iterateOutwards(pos.add(new Vec3i(0, 1, 0)), 5, 5, 5)) {
			BlockState state = world.getBlockState(gravePos);
			Block block = state.getBlock();

			if (canPlaceGrave(world, block, gravePos)) {
				world.setBlockState(gravePos, GraveBlocks.GRAVE.getDefaultState().with(Properties.HORIZONTAL_FACING,
						player.getHorizontalFacing()));

				GraveBlockEntity graveEntity = new GraveBlockEntity(gravePos, world.getBlockState(gravePos));

				graveEntity.setItems(combinedInventory);
				graveEntity.setGraveOwner(player.getGameProfile());

				int experience = ExperienceCalculator.calculateExperienceStorage(player.experienceLevel,
						player.experienceProgress);

				graveEntity.setXp(experience);
				player.totalExperience = 0;
				player.experienceProgress = 0;
				player.experienceLevel = 0;
				world.addBlockEntity(graveEntity);

				if (world.isClient())
					graveEntity.sync(world, gravePos);
				block.onBreak(world, pos, state, player);

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

	private static boolean canPlaceGrave(World world, Block block, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity != null)
			return false;

		Set<Block> blackListedBlocks = new HashSet<Block>() {
			{
				add(Blocks.BEDROCK);
			}
		};

		if (blackListedBlocks.contains(block))
			return false;

		return !(pos.getY() < 0 || pos.getY() > 255);
	}
}
