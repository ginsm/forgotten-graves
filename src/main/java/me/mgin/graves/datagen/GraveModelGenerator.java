package me.mgin.graves.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mgin.graves.Graves;
import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.GraveBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class GraveModelGenerator extends FabricModelProvider {
    public GraveModelGenerator(FabricDataOutput generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        for (GraveBlockBase grave : GraveBlocks.GRAVE_SET) {
            blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(grave).coordinate(
                    BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                        .register(Direction.NORTH,
                            BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                                .put(VariantSettings.MODEL, new Identifier(Graves.MOD_ID, "block/" + grave.getBlockID()))
                        )
                        .register(Direction.EAST,
                            BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                .put(VariantSettings.MODEL, new Identifier(Graves.MOD_ID, "block/" + grave.getBlockID()))
                        )
                        .register(Direction.SOUTH,
                            BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R0)
                                .put(VariantSettings.MODEL, new Identifier(Graves.MOD_ID, "block/" + grave.getBlockID()))
                        )
                        .register(Direction.WEST,
                            BlockStateVariant.create()
                                .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                .put(VariantSettings.MODEL, new Identifier(Graves.MOD_ID, "block/" + grave.getBlockID()))
                        )
                )
            );
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // ...
    }
}
