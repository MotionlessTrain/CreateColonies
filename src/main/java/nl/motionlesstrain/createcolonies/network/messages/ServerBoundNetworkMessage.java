package nl.motionlesstrain.createcolonies.network.messages;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import nl.motionlesstrain.createcolonies.network.NetworkMessage;

public abstract class ServerBoundNetworkMessage<T extends ServerBoundNetworkMessage<T>> implements NetworkMessage, IPayloadHandler<T> {
  abstract void handlePacket(IPayloadContext ctx);

  public static abstract class Factory<T extends ServerBoundNetworkMessage<T>> implements NetworkMessage.Factory<T> {
    @Override
    public IPayloadHandler<T> handler() {
      return ServerBoundNetworkMessage::handlePacket;
    }
  }
}
