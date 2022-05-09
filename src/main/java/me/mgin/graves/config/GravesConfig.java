package me.mgin.graves.config;

import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "forgottengraves")
public class GravesConfig extends ConfigNetworking implements ConfigData {

	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public MainSettings main = new MainSettings();

	@ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
	public ItemDecaySettings itemDecay = new ItemDecaySettings();

	@ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
	public ServerSettings server = new ServerSettings();

	public static GravesConfig getConfig() {
		return AutoConfig.getConfigHolder(GravesConfig.class).getConfig();
	}

	@Override
	public void validatePostLoad() {
		main.maxCustomXPLevel = Math.max(main.maxCustomXPLevel, 0);
		itemDecay.maxDecayPercent = Math.max(Math.min(itemDecay.maxDecayPercent, 100), 0);
		server.minOperatorOverrideLevel = Math.max(Math.min(server.minOperatorOverrideLevel, 4), -1);
	}

	public static class MainSettings {
		@ConfigEntry.Gui.Tooltip
		public boolean enableGraves = true;

		@ConfigEntry.Gui.Tooltip
		public boolean sendGraveCoordinates = true;

		@ConfigEntry.Gui.Tooltip
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		public GraveRetrievalType retrievalType = GraveRetrievalType.ON_BOTH;

		@ConfigEntry.Gui.Tooltip
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		public GraveDropType dropType = GraveDropType.PUT_IN_INVENTORY;

		@ConfigEntry.Gui.Tooltip
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		public GraveExpStoreType expStorageType = GraveExpStoreType.STORE_ALL_XP;

		@ConfigEntry.Gui.Tooltip
		public int maxCustomXPLevel = 30;
	}

	public static class ItemDecaySettings {
		@ConfigEntry.Gui.Tooltip
		@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
		public int maxDecayPercent = 0;

		@ConfigEntry.Gui.Tooltip
		public boolean itemDecayBreaksItems = false;
	}

	public static class ServerSettings {
		@ConfigEntry.Gui.PrefixText
		@ConfigEntry.Gui.Tooltip
		public boolean enableGraveRobbing = false;

		@ConfigEntry.Gui.Tooltip
		@ConfigEntry.BoundedDiscrete(min = -1, max = 4)
		public int minOperatorOverrideLevel = 4;

		@ConfigEntry.Gui.Tooltip
		public List<String> clientsideOptions = List.of();
	}
}
