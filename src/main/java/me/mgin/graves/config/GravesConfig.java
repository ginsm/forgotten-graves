package me.mgin.graves.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "graves")
public class GravesConfig implements ConfigData {
  @ConfigEntry.Gui.CollapsibleObject
  public MainSettings mainSettings = new MainSettings();

  public static GravesConfig getConfig() {
    return AutoConfig.getConfigHolder(GravesConfig.class).getConfig();
  }

  public static class MainSettings {
    @ConfigEntry.Gui.Tooltip
    public boolean enableGraves = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enableGraveRobbing = false;

    @ConfigEntry.Gui.Tooltip
    public boolean sendGraveCoordinates = true;

    @ConfigEntry.Gui.Tooltip
    public int minimumOpLevelToLoot = 4;

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
    public int customXPStoredLevel = 30;
  }
}
