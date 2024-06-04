package me.mgin.graves.abstraction;

/*? if <1.20.5 {*/
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
/*?}*/


/**
 * This class contains method abstractions for Fabric. This is to be used in conjunction
 * with stonecutter-kt to keep the rest of the codebase version agnostic.
 *
 * @see <a href="https://github.com/kikugie/stonecutter-kt">Stonecutter KT</a>
 */
public class FabricAbstraction {
    /*? if <1.20.5 {*/
    public static Item.Settings getFabricItemSettings() {
        return new FabricItemSettings();
    }
    /*?}*/
}
