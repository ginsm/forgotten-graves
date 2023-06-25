package me.mgin.graves.config;

import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.config.enums.GraveDropType;
import me.mgin.graves.config.enums.GraveExpStoreType;
import me.mgin.graves.config.enums.GraveRetrievalType;
import me.mgin.graves.config.enums.ExperienceType;
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
    public ExperienceSettings experience = new ExperienceSettings();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public SinkSettings sink = new SinkSettings();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public DecaySettings decay = new DecaySettings();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public ServerSettings server = new ServerSettings();

    @Override
    public void validatePostLoad() {
        experience.cap = Math.max(experience.cap, -1);
        decay.decayModifier = Math.max(Math.min(decay.decayModifier, 100), 0);
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
        public GraveDropType dropType = GraveDropType.EQUIP;

        @ConfigEntry.Gui.Tooltip
        public boolean sneakSwapsDropType = true;
    }

    public static class ExperienceSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GraveExpStoreType expStorageType = GraveExpStoreType.ALL;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int percentage = 100;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ExperienceType percentageType = ExperienceType.POINTS;

        @ConfigEntry.Gui.Tooltip
        public int cap = -1;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ExperienceType capType = ExperienceType.LEVELS;
    }

    public static class SinkSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean sinkInAir = true;

        @ConfigEntry.Gui.Tooltip
        public boolean sinkInWater = true;

        @ConfigEntry.Gui.Tooltip
        public boolean sinkInLava = false;
    }

    public static class DecaySettings {
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.Tooltip
        public boolean decayEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int decayModifier = 60;

        @ConfigEntry.Gui.Tooltip
        public boolean decayBreaksItems = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public DecayingGrave.BlockDecay decayRobbing = DecayingGrave.BlockDecay.FRESH;
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
