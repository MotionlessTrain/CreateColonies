package nl.motionlesstrain.createcolonies.network.messages;

import nl.motionlesstrain.createcolonies.network.NetworkMessage;
import java.util.function.Supplier;
import net.neoforged.neoforge.network.NetworkEvent;

public abstract class ClientBoundNetworkMessage implements NetworkMessage {
  protected abstract ClientsideHandler createHandler();

  @Override
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    final var networkContext = ctx.get();
    networkContext.enqueueWork(() ->
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
          createHandler().handlePacket(ctx.get())
      )
    );
  }

  public interface ClientsideHandler {
    void handlePacket(NetworkEvent.Context context);
  }
}
