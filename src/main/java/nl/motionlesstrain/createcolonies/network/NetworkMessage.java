package nl.motionlesstrain.createcolonies.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface NetworkMessage {
  void encode(FriendlyByteBuf buffer);

  void handle(Supplier<NetworkEvent.Context> ctx);
}
