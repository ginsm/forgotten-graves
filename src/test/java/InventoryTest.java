import me.mgin.graves.block.utility.Inventory;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    public void testMerge() {
        List<ItemStack> source = new ArrayList<>();
        source.add(new ItemStack(Items.DIAMOND, 32));
        source.add(new ItemStack(Items.GOLD_INGOT, 16));

        List<ItemStack> target = new ArrayList<>();
        target.add(new ItemStack(Items.DIAMOND, 32)); // Partially filled
        target.add(ItemStack.EMPTY); // Empty slot

        Inventory.mergeInventories(source, target);

        assertEquals(64, target.get(0).getCount());
        assertEquals(16, target.get(1).getCount()); // GOLD_INGOT should fill the empty slot
        assertTrue(source.isEmpty());
    }

    @Test
    public void testMergeWithNBT() {
        ItemStack enchantedStick = new ItemStack(Items.STICK);
        enchantedStick.getOrCreateNbt().putString("Enchantments", "[{id:\"minecraft:sharpness\",lvl:3}]");

        List<ItemStack> source = new ArrayList<>();
        source.add(enchantedStick);

        List<ItemStack> target = new ArrayList<>();
        target.add(new ItemStack(Items.STICK)); // Empty book, no NBT

        Inventory.mergeInventories(source, target);

        // The enchanted stick should be in the source inventory still, as target has no empty (Items.AIR) slots and
        // the other stick doesn't have matching NBT.
        assertEquals(enchantedStick, source.get(0));
        assertFalse(target.get(0).hasNbt());
        assertEquals(1, source.size());
    }
}
