package me.mgin.graves.util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.Date;

public class NbtHelper {
    /**
     * Read an inventory from NBT.
     *
     * @param key String
     * @param nbt NbtCompound
     * @return DefaultedList.ItemStack
     */
    static public DefaultedList<ItemStack> readInventory(String key, NbtCompound nbt) {
        if (nbt.contains(key)) {
            int itemCount = nbt.getCompound("ItemCount").getInt(key);

            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(itemCount, ItemStack.EMPTY);

            Inventories.readNbt(nbt.getCompound(key), stacks);

            return stacks;
        }

        return DefaultedList.ofSize(0);
    }

    /**
     * Write an inventory to NBT.
     *
     * @param key    String
     * @param stacks DefaultedList.ItemStack
     * @param nbt    NbtCompound
     * @return NbtCompound
     */
    public static NbtCompound writeInventory(String key, DefaultedList<ItemStack> stacks, NbtCompound nbt) {
        if (stacks == null)
            return nbt;

        // Write item count
        NbtCompound itemCount = new NbtCompound();

        if (nbt.contains("ItemCount"))
            itemCount = nbt.getCompound("ItemCount");

        itemCount.putInt(key, stacks.size());
        nbt.put("ItemCount", itemCount);

        // Store the inventory
        nbt.put(key, Inventories.writeNbt(new NbtCompound(), stacks, true));

        return nbt;
    }

    /**
     * Wrapper for <i>NbtHelper.toGameProfile</i>.
     *
     * @param nbt NbtCompound
     * @return GameProfile
     */
    public static GameProfile toGameProfile(NbtCompound nbt) {
        return net.minecraft.nbt.NbtHelper.toGameProfile(nbt);
    }

    /**
     * Wrapper for <i>NbtHelper.writeGameProfile</i>.
     *
     * @param nbt NbtCompound
     * @param profile GameProfile
     * @return NbtCompound
     */
    public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
        return net.minecraft.nbt.NbtHelper.writeGameProfile(nbt, profile);
    }

    /**
     * Wrapper for <i>NbtHelper.fromNbtProviderString</i>.
     *
     * @param nbtString String
     * @return NbtCompound
     */
    public static NbtCompound fromNbtProviderString(String nbtString) throws CommandSyntaxException {
        return net.minecraft.nbt.NbtHelper.fromNbtProviderString(nbtString);
    }

    /**
     * Wrapper for <i>NbtHelper.toPrettyPrintedText</i>.
     *
     * @param nbt NbtElement
     * @return Text
     */
    public static Text toPrettyPrintedText(NbtElement nbt) {
        return net.minecraft.nbt.NbtHelper.toPrettyPrintedText(nbt);
    }

    /**
     * Creates a new BlockPos based on stored coordinates in the given NBT.
     *
     * @param nbt NbtCompound
     * @return BlockPos
     */
    public static BlockPos readCoordinates(NbtCompound nbt) {
        return new BlockPos(
            nbt.getInt("x"),
            nbt.getInt("y"),
            nbt.getInt("z")
        );
    }

    /**
     * Upgrades any old graves nbt to newer formats
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    public static NbtCompound upgradeOldGraves(NbtCompound nbt) {
        if (nbt.getType("ItemCount") == NbtElement.INT_TYPE)
            nbt = upgradeInventories(nbt);

        if (nbt.contains("noAge"))
            nbt = upgradeNoAge(nbt);

        if (nbt.getLong("mstime") == 0)
            nbt = upgradeMsTime(nbt);

        if (nbt.contains("GraveSkull", NbtElement.STRING_TYPE))
            nbt = removeOldCustomSkullTexture(nbt);

        if (nbt.getType("XP") == NbtElement.INT_TYPE)
            nbt = upgradeXP(nbt);

        return nbt;
    }

    /**
     * Converts old graves from having a mstime of 0 to the time they're
     * first seen in the world.
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    private static NbtCompound upgradeMsTime(NbtCompound nbt) {
        nbt.putLong("mstime", (new Date()).getTime());
        return nbt;
    }

    /**
     * Converts noAge key to noDecay key while preserving the value.
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    private static NbtCompound upgradeNoAge(NbtCompound nbt) {
        int noAge = nbt.getInt("noAge");
        nbt.putInt("noDecay", noAge);
        nbt.remove("noAge");
        return nbt;
    }

    /**
     * Converts the old inventory nbt format to the new format.
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    private static NbtCompound upgradeInventories(NbtCompound nbt) {
        // Retrieve the items like normal
        DefaultedList<ItemStack> oldItems = DefaultedList.ofSize(nbt.getInt("ItemCount"), ItemStack.EMPTY);
        Inventories.readNbt(nbt.getCompound("Items"), oldItems);

        // Separate the item lists
        DefaultedList<ItemStack> items = DefaultedList.ofSize(0);
        items.addAll(oldItems.subList(0, 41));

        DefaultedList<ItemStack> trinkets = DefaultedList.ofSize(0);
        if (oldItems.size() > 41) {
            trinkets.addAll(oldItems.subList(41, oldItems.size()));
        }

        // Create/store new ItemCount format
        NbtCompound itemCount = new NbtCompound();
        itemCount.putInt("Items", items.size());
        itemCount.putInt("trinkets", trinkets.size());
        nbt.put("ItemCount", itemCount);

        // Store the two inventories
        nbt.put("Items", Inventories.writeNbt(new NbtCompound(), items, true));
        nbt.put("trinkets", Inventories.writeNbt(new NbtCompound(), trinkets, true));

        return nbt;
    }

    private static NbtCompound removeOldCustomSkullTexture(NbtCompound nbt) {
        nbt.remove("GraveSkull");
        return nbt;
    }

    private static NbtCompound upgradeXP(NbtCompound nbt) {
        // Get experience points from grave's nbt and remove XP
        long storedExperiencePoints = nbt.getInt("XP");
        nbt.remove("XP");

        // Handle graves with no stored XP
        if (storedExperiencePoints == 0) {
            nbt.putIntArray("XP", new int[]{0,0});
            return nbt;
        }

        // Create initial variables
        int[] experienceArray = new int[2];
        int level;

        // Necessary to check which equation to use (similar to vanilla equations)
        int level16Exp = 352;
        int level31Exp = 1507;

        // Calculate level based on the inverse equations of calculateLevelExperience
        if (storedExperiencePoints <= level16Exp) {
            level = (int) Math.floor((-6 + Math.sqrt(36 + 4 * storedExperiencePoints)) / 2);
        } else if (storedExperiencePoints <= level31Exp) {
            level = (int) Math.floor((40.5 + Math.sqrt(1640.25 - 10 * (360 - storedExperiencePoints))) / 5);
        } else {
            level = (int) Math.floor((162.5D + Math.sqrt(26406.25D - 18L * (2220L - storedExperiencePoints))) / 9L);
        }

        // This should almost never run, as the above L31+ equation should handle Integer.MAX_VALUE just fine (from my
        // testing), but I'll add this as a fallback just in case.
        if (updateXP$calculateLevelExperience(level + 1) <= storedExperiencePoints) {
            level = upgradeXP$findHighStoredXPLevel(storedExperiencePoints);
        }

        // Calculate remaining points
        int points = (int) (storedExperiencePoints - updateXP$calculateLevelExperience(level));

        // Store the level and points as an int array in the nbt and return it
        nbt.putIntArray("XP", new int[]{level, points});
        return nbt;
    }

    /**
     * Calculates how many points are in a given level. The equations are from the
     * <a href="https://minecraft.fandom.com/wiki/Experience#Leveling_up">Minecraft Wiki</a>.
     * @param level int
     * @return int
     */
    public static long updateXP$calculateLevelExperience(int level) {
        long levelSquared = (long) level * level;
        long levelExperience = 0;

        if (level <= 16)
            levelExperience = levelSquared + 6L * level;
        if (level >= 17 && level <= 31)
            levelExperience = (long) (2.5d * levelSquared - 40.5D * level + 360L);
        if (level > 31)
            levelExperience = (long) (4.5d * levelSquared - 162.5D * level + 2220L);

        return levelExperience;
    }

    public static Integer upgradeXP$findHighStoredXPLevel(long storedExperiencePoints) {
        int low = 0;
        int high = 21864; // The old graves could only store 21863 levels and some points.
        int level = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            long xpAtMid = updateXP$calculateLevelExperience(mid);

            if (xpAtMid <= storedExperiencePoints) {
                level = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return level;
    }
}
