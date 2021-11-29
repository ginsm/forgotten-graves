package me.mgin.graves;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import me.mgin.graves.config.GravesConfig;

@Environment(EnvType.CLIENT)
public class GravesModMenuIntegration implements ModMenuApi {
  
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return parent -> AutoConfig.getConfigScreen(GravesConfig.class, parent).get();
  }

}
