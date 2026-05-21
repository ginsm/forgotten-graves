//? if >=1.20.5 {
package me.mgin.graves.networking.config.payload;

import me.mgin.graves.Graves;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ApplyConfigC2SPayload(String configJson) implements CustomPayload {
    public static final Identifier PAYLOAD_ID = Identifier.of(Graves.MOD_ID, "apply_config_c2s");
    public static final CustomPayload.Id<ApplyConfigC2SPayload> ID = new CustomPayload.Id<>(PAYLOAD_ID);
    public static final PacketCodec<PacketByteBuf, ApplyConfigC2SPayload> CODEC = PacketCodec.of(
        (payload, buf) -> buf.writeString(payload.configJson),
        buf -> new ApplyConfigC2SPayload(buf.readString())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
//?}
