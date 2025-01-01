package me.mgin.graves.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;

import java.util.Arrays;

public class GraveTestNBTHelper {
    public static String getPlayerInventorySNBT(PlayerEntity player) {
        NbtList list = new NbtList();
        player.getInventory().writeNbt(list);
        return list.toString();
    }

    public static void setPlayerInventoryFromSNBT(PlayerEntity player, String snbt) {
        try {
            GraveTestHelper.clearPlayerInventory(player);
            NbtCompound compound = fromSNBT("{Inventory: " + snbt + "}");
            NbtList inventory = compound.getList("Inventory", NbtElement.COMPOUND_TYPE);
            NbtCompound playerNbt = player.writeNbt(new NbtCompound());
            playerNbt.put("Inventory", inventory);
            player.readCustomDataFromNbt(playerNbt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean compareInventoriesSNBT(String inv1, String inv2) {
        try {
           return normalizeSNBTList(inv1).equals(normalizeSNBTList(inv2));
        } catch(Exception e) {
            return false;
        }
    }

    public static String normalizeSNBTList(String list) throws CommandSyntaxException {
        NbtList nbtList = GraveTestNBTHelper.fromSNBT("{Inventory:" + list + "}").getList("Inventory",
            NbtElement.COMPOUND_TYPE);
        NbtElement[] normalizedList = nbtList.toArray(new NbtElement[0]);
        return Arrays.toString(normalizedList);
    }

    public static String toSNBT(NbtCompound nbt) {
        return NbtHelper.toNbtProviderString(nbt);
    }

    public static NbtCompound fromSNBT(String snbt) throws CommandSyntaxException {
        return NbtHelper.fromNbtProviderString(snbt);
    }
}
