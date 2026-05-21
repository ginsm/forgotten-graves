//? if >=1.20.5 {
package me.mgin.graves.networking.config.payload;

import me.mgin.graves.Graves;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ReloadClientConfigS2CPayload() implements CustomPayload {
    public static final Identifier PAYLOAD_ID = Identifier.of(Graves.MOD_ID, "reload_config_s2c");
    public static final CustomPayload.Id<ReloadClientConfigS2CPayload> ID = new CustomPayload.Id<>(PAYLOAD_ID);
    public static final PacketCodec<PacketByteBuf, ReloadClientConfigS2CPayload> CODEC = PacketCodec.of(
        (payload, buf) -> {},
        buf -> new ReloadClientConfigS2CPayload()
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
//?}
