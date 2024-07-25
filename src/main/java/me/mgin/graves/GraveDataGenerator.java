package me.mgin.graves;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import me.mgin.graves.datagen.*;

public class GraveDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(GraveBlockTagGenerator::new);
        pack.addProvider(GraveRecipeGenerator::new);
        pack.addProvider(GraveItemTagGenerator::new);
    }

}