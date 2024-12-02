package me.mgin.graves.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GraveEffects {
    public static final StatusEffect DISABLE_GRAVES_EFFECT = new DisableGravesEffect();
    
    public static void register(String MOD_ID) {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MOD_ID, "disablegraves"),
            DISABLE_GRAVES_EFFECT);
    }
}
