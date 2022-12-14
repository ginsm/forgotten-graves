package me.mgin.graves.client.registry;

import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.Field;

public class ClientReceivers {
	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(Constants.UPDATE_CLIENTSIDE_CONFIG,
				(client, handler, buf, sender) -> GravesConfig.getConfig().reload());

		ClientPlayNetworking.registerGlobalReceiver(Constants.SET_CLIENTSIDE_CONFIG,
				(client, handler, buf, sender) -> setClientConfig(buf));
	}

	private static void setClientConfig(PacketByteBuf buf) {
		GravesConfig config = GravesConfig.getConfig();
		NbtCompound nbt = buf.readNbt();

		if (nbt == null) return;

		// Passed by respective option handler
		String option = nbt.getString("option");
		String subclass = nbt.getString("subclass");

		// Resolve value by type
		Object value = switch(nbt.getType("value")) {
			case 1 -> nbt.getBoolean("value");
			case 3 -> nbt.getInt("value");
			case 8 -> nbt.getString("value");
			default -> throw new IllegalStateException("Unexpected value: " + nbt.getType("value"));
		};

		try {
			// Dynamically resolve the target and field
			Field target = GravesConfig.class.getDeclaredField(subclass);
			Field field = target.getType().getDeclaredField(option);

			// Set the target instance's field in config
			field.set(target.get(config), value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		// Save the config
		AutoConfig.getConfigHolder(GravesConfig.class).save();
	}
}
