package me.mgin.graves.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.client.MinecraftClient;

import com.google.gson.Gson;

import me.mgin.graves.client.commands.ReloadClientCommand;
import me.mgin.graves.client.registry.ClientEvents;
import me.mgin.graves.client.render.GraveBlockEntityRenderer;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.registry.GraveBlocks;
import me.mgin.graves.util.Identifiers;

public class GravesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register(GraveBlocks.GRAVE_BLOCK_ENTITY, GraveBlockEntityRenderer::new);

		ClientCommandManager.DISPATCHER.register(
			ClientCommandManager.literal("reloadgraves")
					.executes(context -> ReloadClientCommand.execute(context))
		);

		ClientEvents.register();
	}

	/**
	 * Transmits JSON-formatted config data to the (dedicated) server. The
	 * data will not be transmitted to integrated servers (singleplayer).
	 * @param configData
	 */
	static public void sendServerClientConfig(GravesConfig configData) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (!(client.getServer() instanceof IntegratedServer)) {
			PacketByteBuf buf = PacketByteBufs.create();
			Gson gson = new Gson();

			buf.writeUuid(client.player.getUuid());
			buf.writeString(gson.toJson(configData));

			ClientPlayNetworking.send(Identifiers.CLIENT_SEND_CONFIG, buf);
		}
	}
}
