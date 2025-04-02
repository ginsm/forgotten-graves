package me.mgin.graves.config;

import me.mgin.graves.block.decay.DecayingGrave;
import me.mgin.graves.config.enums.*;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "forgottengraves")
public class GravesConfig extends ConfigHelpers implements ConfigData {

    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.TransitiveObject
    public MainSettings main = new MainSettings();

    @ConfigEntry.Category("experience")
    @ConfigEntry.Gui.TransitiveObject
    public ExperienceSettings experience = new ExperienceSettings();

    @ConfigEntry.Category("spawning")
    @ConfigEntry.Gui.TransitiveObject
    public SinkSettings sink = new SinkSettings();

    @ConfigEntry.Category("decay")
    @ConfigEntry.Gui.TransitiveObject
    public DecaySettings decay = new DecaySettings();

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public ServerSettings server = new ServerSettings();

    @Override
    public void validatePostLoad() {
        experience.percentage = Math.max(Math.min(experience.percentage, 100), 0);
        experience.cap = Math.max(experience.cap, -1);

        decay.decayModifier = Math.max(Math.min(decay.decayModifier, 100), 0);
        decay.freshGraveDecayChance = Math.max(Math.min(decay.freshGraveDecayChance, 50), 0);
        decay.oldGraveDecayChance = Math.max(Math.min(decay.oldGraveDecayChance, 50), 0);
        decay.weatheredGraveDecayChance = Math.max(Math.min(decay.weatheredGraveDecayChance, 50), 0);
        decay.minStageTimeSeconds = Math.max(Math.min(decay.minStageTimeSeconds, decay.maxStageTimeSeconds), 0);
        decay.maxStageTimeSeconds = Math.max(decay.maxStageTimeSeconds, 0);

        server.storedGravesAmount = Math.max(Math.min(server.storedGravesAmount, 40), 0);
        server.OPOverrideLevel = Math.max(Math.min(server.OPOverrideLevel, 4), -1);
    }

    public static class MainSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean graves = true;

        @ConfigEntry.Gui.Tooltip
        public boolean disableInPvP = false;

        @ConfigEntry.Gui.Tooltip
        public boolean graveCoordinates = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GraveRetrievalType retrievalType = GraveRetrievalType.BOTH;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GraveMergeOrder mergeOrder = GraveMergeOrder.GRAVE;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public GraveDropType dropType = GraveDropType.EQUIP;

        @ConfigEntry.Gui.Tooltip
        public boolean sneakSwapsDropType = true;

        @ConfigEntry.Gui.Tooltip
        public boolean respectKeepInventory = false;
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

        @ConfigEntry.Gui.Tooltip
        public boolean sinkThroughBlocks = true;

        @ConfigEntry.Gui.Tooltip
        public boolean replaceBlocks = true;
    }

    public static class DecaySettings {
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.Tooltip
        public boolean decayEnabled = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int decayModifier = 50;

        @ConfigEntry.Gui.Tooltip
        public boolean decayBreaksItems = false;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public DecayingGrave.BlockDecay decayRobbing = DecayingGrave.BlockDecay.FRESH;

        @ConfigEntry.Gui.PrefixText

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 50)
        public int freshGraveDecayChance = 5;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 50)
        public int oldGraveDecayChance = 12;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 50)
        public int weatheredGraveDecayChance = 16;

        @ConfigEntry.Gui.Tooltip
        public int minStageTimeSeconds = 30;

        @ConfigEntry.Gui.Tooltip
        public int maxStageTimeSeconds = 300;
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
