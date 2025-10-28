package nl.motionlesstrain.createcolonies.network.messages;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import nl.motionlesstrain.createcolonies.network.NetworkMessage;

public abstract class ClientBoundNetworkMessage implements NetworkMessage {
  abstract ClientSideHandler getHandler();

  protected void handlePacket(IPayloadContext ctx) {
    getHandler().handleMessage(ctx);
  }

  public interface ClientSideHandler {
    void handleMessage(IPayloadContext ctx);
  }

  public abstract static class Factory<T extends ClientBoundNetworkMessage> implements NetworkMessage.Factory<T> {

    @Override
    public IPayloadHandler<T> handler() {
      return ClientBoundNetworkMessage::handlePacket;
    }
  }
}
