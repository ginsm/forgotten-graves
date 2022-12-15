package me.mgin.graves.client.registry;

import me.mgin.graves.config.GraveDropType;
import me.mgin.graves.config.GraveExpStoreType;
import me.mgin.graves.config.GraveRetrievalType;
import me.mgin.graves.config.GravesConfig;
import me.mgin.graves.util.Constants;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientReceivers {
	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(Constants.UPDATE_CLIENTSIDE_CONFIG,
				(client, handler, buf, sender) -> GravesConfig.getConfig().reload());

		ClientPlayNetworking.registerGlobalReceiver(Constants.SET_CLIENTSIDE_CONFIG,
				(client, handler, buf, sender) -> setClientConfig(buf, handler));
	}

	private static void setClientConfig(PacketByteBuf buf, ClientPlayNetworkHandler handler) {
		GravesConfig config = GravesConfig.getConfig();
		NbtCompound nbt = buf.readNbt();
		if (nbt == null) return;

		// Passed by respective option handler
		String option = nbt.getString("option");
		String type = nbt.getString("type");

		try {
			// Dynamically resolve the target and field
			Field target = determineSubClass(option);
			Field field = target.getType().getDeclaredField(option);

			// Set the target instance's field in config
			field.set(target.get(config), extractNbtValue(nbt, option, type));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		// Save the config
		AutoConfig.getConfigHolder(GravesConfig.class).save();
	}

	static private Field determineSubClass(String option) {
		List<Field> fields = Arrays.stream(GravesConfig.class.getDeclaredFields()).filter((field) -> {
			Class<?> subclass = field.getType();
			for (Field f : subclass.getDeclaredFields()) {
				if (f.getName().equals(option)) return true;
			}
			return false;
		}).collect(Collectors.toList());

		if (fields.isEmpty()) throw new RuntimeException("No subclass could be found.");
		return fields.get(0);
	}

	static private Object extractNbtValue(NbtCompound nbt, String option, String type) {
		return switch(type) {
			case "BoolArgumentType" -> nbt.getBoolean("value");
			case "integer" -> nbt.getInt("value");
			case "literal" -> {
				String v = nbt.getString("value");
				yield switch (option) {
					case "retrievalType" -> GraveRetrievalType.valueOf(v);
					case "dropType" -> GraveDropType.valueOf(v);
					case "expStorageType" -> GraveExpStoreType.valueOf(v);
					default -> throw new IllegalStateException("Unexpected value for '" + option + "': " + v);
				};
			}
			default -> throw new IllegalStateException("Unexpected value: " + type);
		};
	}
}
