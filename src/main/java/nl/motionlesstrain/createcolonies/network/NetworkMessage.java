package nl.motionlesstrain.createcolonies.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public interface NetworkMessage extends CustomPacketPayload {

  interface Factory<T extends NetworkMessage> {
    CustomPacketPayload.Type<T> type();

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

    IPayloadHandler<T> handler();
  }
}
