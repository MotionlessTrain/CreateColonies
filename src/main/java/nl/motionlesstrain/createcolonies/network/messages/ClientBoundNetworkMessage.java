package nl.motionlesstrain.createcolonies.network.messages;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import nl.motionlesstrain.createcolonies.network.NetworkMessage;

import java.util.function.Supplier;

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
