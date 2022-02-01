package me.mgin.graves.config;

import com.google.gson.Gson;

import me.mgin.graves.util.Constants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.integrated.IntegratedServer;

public class ConfigNetworking {
  /**
   * Transmits JSON-formatted config data to the (dedicated) server. The
   * data will not be transmitted to integrated servers (singleplayer).
   * @param configData
   */
  public void sendToServer() {
    MinecraftClient client = MinecraftClient.getInstance();

    if (!(client.getServer() instanceof IntegratedServer)) {
      PacketByteBuf buf = PacketByteBufs.create();
      buf.writeString(this.serialize());
      ClientPlayNetworking.send(Constants.CLIENT_SEND_CONFIG, buf);
    }
  }

  /**
   * JSONify this GravesConfig instance.
   * @return
   */
  public String serialize() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  /**
   * Convert a JSONified config to a GravesConfig instance.
   * @param config
   * @return GravesConfig instance
   */
  public static GravesConfig deserialize(String config) {
    Gson gson = new Gson();
    return gson.fromJson(config, GravesConfig.class);
  }
}
