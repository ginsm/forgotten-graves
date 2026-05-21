//? if >=1.20.5 {
package me.mgin.graves.networking.config.payload;

import me.mgin.graves.Graves;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RequestConfigS2CPayload() implements CustomPayload {
    public static final Identifier PAYLOAD_ID = Identifier.of(Graves.MOD_ID, "request_config_s2c");
    public static final CustomPayload.Id<RequestConfigS2CPayload> ID = new CustomPayload.Id<>(PAYLOAD_ID);
    public static final PacketCodec<PacketByteBuf, RequestConfigS2CPayload> CODEC = PacketCodec.of(
        (payload, buf) -> {},
        buf -> new RequestConfigS2CPayload()
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
//?}
