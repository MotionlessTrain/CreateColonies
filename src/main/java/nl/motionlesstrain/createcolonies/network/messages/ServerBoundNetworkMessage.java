package nl.motionlesstrain.createcolonies.network.messages;

import nl.motionlesstrain.createcolonies.network.NetworkMessage;
import java.util.function.Supplier;
import net.neoforged.neoforge.network.NetworkEvent;

public abstract class ServerBoundNetworkMessage implements NetworkMessage {
  abstract void handlePacket(NetworkEvent.Context ctx);

  @Override
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    final var networkContext = ctx.get();
    networkContext.enqueueWork(() -> handlePacket(ctx.get()));
    networkContext.setPacketHandled(true);
  }
}
