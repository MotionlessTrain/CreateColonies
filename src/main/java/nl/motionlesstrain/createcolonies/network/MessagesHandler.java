package nl.motionlesstrain.createcolonies.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import nl.motionlesstrain.createcolonies.CreateColonies;
import nl.motionlesstrain.createcolonies.network.messages.SaveNBTFileMessage;

import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;

public class MessagesHandler {

  @FunctionalInterface
  interface MessageRegisterer<T extends CustomPacketPayload> {
    void register(CustomPacketPayload.Type<T> type,
                  StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
                  IPayloadHandler<T> handler);
  }

  private static <M extends NetworkMessage> void registerMessage(MessageRegisterer<M> registrar, NetworkMessage.Factory<M> factory) {
    registrar.register(factory.type(), factory.streamCodec(), factory.handler());
  }

  public static void setUpNetwork(final RegisterPayloadHandlersEvent event) {
    final String version = CreateColonies.container.getModInfo().getVersion().toString();

    final PayloadRegistrar registrar = event.registrar(MODID)
        .versioned(version);
    registerMessage(registrar::playToClient, new SaveNBTFileMessage.Factory());
  }
}
