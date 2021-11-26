package me.mgin.graves;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import me.mgin.graves.api.GravesApi;
import me.mgin.graves.block.AgingGrave.BlockAge;
import me.mgin.graves.block.GraveBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.mgin.graves.util.ExperienceCalculator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class Graves implements ModInitializer {

	public static final GraveBase GRAVE_FORGOTTEN = new GraveBase(BlockAge.FORGOTTEN,
			FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));
	public static final GraveBase GRAVE_WEATHERED = new GraveBase(BlockAge.WEATHERED,
			FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));
	public static final GraveBase GRAVE_OLD = new GraveBase(BlockAge.OLD,
			FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));
	public static final GraveBase GRAVE = new GraveBase(BlockAge.FRESH,
			FabricBlockSettings.of(Material.ORGANIC_PRODUCT).strength(0.8f, -1f));

	public static BlockEntityType<GraveBlockEntity> GRAVE_BLOCK_ENTITY;

	public static final ArrayList<GravesApi> apiMods = new ArrayList<>();

	public static String MOD_ID = "forgottengraves";
	public static String BRAND_BLOCK = "grave";

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BRAND_BLOCK), GRAVE);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BRAND_BLOCK + "_old"), GRAVE_OLD);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BRAND_BLOCK + "_weathered"), GRAVE_WEATHERED);
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BRAND_BLOCK + "_forgotten"), GRAVE_FORGOTTEN);
		GRAVE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, MOD_ID + ":" + BRAND_BLOCK,
				FabricBlockEntityTypeBuilder
						.create(GraveBlockEntity::new, GRAVE, GRAVE_OLD, GRAVE_WEATHERED, GRAVE_FORGOTTEN).build(null));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, BRAND_BLOCK),
				new BlockItem(GRAVE, new Item.Settings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, BRAND_BLOCK + "_old"),
				new BlockItem(GRAVE_OLD, new Item.Settings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, BRAND_BLOCK + "_weathered"),
				new BlockItem(GRAVE_WEATHERED, new Item.Settings().group(ItemGroup.DECORATIONS)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, BRAND_BLOCK + "_forgotten"),
				new BlockItem(GRAVE_FORGOTTEN, new Item.Settings().group(ItemGroup.DECORATIONS)));

		apiMods.addAll(FabricLoader.getInstance().getEntrypoints(MOD_ID, GravesApi.class));

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
			if (entity instanceof GraveBlockEntity graveBlockEntity) {
				// This will eventually be moved to a configuration option
				if (player.hasPermissionLevel(4) && graveBlockEntity.getGraveOwner() != null
						&& !graveBlockEntity.getGraveOwner().getId().equals(player.getGameProfile().getId())) {
					System.out.println("[Graves] Operator overrided grave protection at: " + pos);
					return true;
				}

				if (graveBlockEntity.getGraveOwner() != null)
					if (!graveBlockEntity.getGraveOwner().getId().equals(player.getGameProfile().getId()))
						return false;
			}

			return true;
		});
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
				int currentExperience = ExperienceCalculator.calculateTotalExperience(player.experienceLevel,
						player.experienceProgress);
				graveBlockEntity.setXp(currentExperience);
				player.totalExperience = 0;
				player.experienceProgress = 0;
				player.experienceLevel = 0;
				world.addBlockEntity(graveBlockEntity);

				if (world.isClient())
					graveBlockEntity.sync();
				block.onBreak(world, blockPos, blockState, player);

				player.sendMessage(new TranslatableText("text.forgottengraves.mark_coords", gravePos.getX(),
						gravePos.getY(), gravePos.getZ()), false);
				System.out.println("[Graves] Grave spawned at: " + gravePos.getX() + ", " + gravePos.getY() + ", "
						+ gravePos.getZ());
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
