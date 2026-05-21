package me.mgin.graves.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;

import java.lang.reflect.Method;

/**
 * Compatibility layer for GameTest API changes in Minecraft 1.20.5.
 * TestContext.createMockSurvivalPlayer/createMockCreativePlayer were refactored.
 */
public class GameTestCompat {

    public static PlayerEntity createMockSurvivalPlayer(TestContext context) {
        //? if <1.20.5 {
        return context.createMockSurvivalPlayer();
        //?} else {
        return createMockPlayer(context, GameMode.SURVIVAL);
        //?}
    }

    public static PlayerEntity createMockCreativePlayer(TestContext context) {
        //? if <1.20.5 {
        return context.createMockCreativePlayer();
        //?} else {
        return createMockPlayer(context, GameMode.CREATIVE);
        //?}
    }

    //? if >=1.20.5 {
    private static PlayerEntity createMockPlayer(TestContext context, GameMode mode) {
        for (String name : new String[]{"makeTickingMockServerPlayer", "createMockPlayer"}) {
            try {
                Method m = context.getClass().getMethod(name, GameMode.class);
                return (PlayerEntity) m.invoke(context, mode);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                throw new RuntimeException("Failed to create mock " + mode + " player via " + name, e);
            }
        }
        throw new RuntimeException("No mock player method found on TestContext for 1.20.5");
    }
    //?}
}
