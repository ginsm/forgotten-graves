package me.mgin.graves.config;

import me.mgin.graves.config.enums.GraveDropType;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.GraveRetrievalType;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "forgottengraves")
public class GravesConfig extends ConfigHelpers implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public MainSettings main = new MainSettings();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public FloatingSettings floating = new FloatingSettings();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public ItemDecaySettings itemDecay = new ItemDecaySettings();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public ServerSettings server = new ServerSettings();

    @Override
    public void validatePostLoad() {
        main.maxCustomXPLevel = Math.max(main.maxCustomXPLevel, 0);
        itemDecay.decayModifier = Math.max(Math.min(itemDecay.decayModifier, 100), 0);
        server.OPOverrideLevel = Math.max(Math.min(server.OPOverrideLevel, 4), -1);
    }

    public static class MainSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean graves = true;

        @ConfigEntry.Gui.Tooltip
        public boolean graveCoordinates = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GraveRetrievalType retrievalType = GraveRetrievalType.BOTH;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GraveDropType dropType = GraveDropType.INVENTORY;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GraveExpStoreType expStorageType = GraveExpStoreType.ALL;

        @ConfigEntry.Gui.Tooltip
        public int maxCustomXPLevel = 30;
    }


    public static class FloatingSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean floatInAir = false;
        
        @ConfigEntry.Gui.Tooltip
        public boolean floatInWater = false;

        @ConfigEntry.Gui.Tooltip
        public boolean floatInLava = true;
    }

    public static class ItemDecaySettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int decayModifier = 0;

        @ConfigEntry.Gui.Tooltip
        public boolean decayBreaksItems = false;
    }

    public static class ServerSettings {
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.Tooltip
        public boolean graveRobbing = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 40)
        public int storedGravesAmount = 15;

        @ConfigEntry.Gui.Tooltip
        public boolean destructiveDeleteCommand = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = -1, max = 4)
        public int OPOverrideLevel = 4;

        @ConfigEntry.Gui.Tooltip
        public List<String> clientOptions = new ArrayList<>();
    }
}
