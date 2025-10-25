package nl.motionlesstrain.createcolonies.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import net.neoforged.neoforgespi.language.IModInfo;
import nl.motionlesstrain.createcolonies.network.messages.SaveNBTFileMessage;

import java.util.function.Function;

import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;

public class MessagesHandler {
  private static int messageId = 0;

  public static SimpleChannel NETWORK;

  private static <M extends NetworkMessage> void registerMessage(
      Class<M> messageClass, Function<FriendlyByteBuf, M> decode) {
    NETWORK.registerMessage(messageId++, messageClass, NetworkMessage::encode, decode, NetworkMessage::handle);
  }

  public static void setUpNetwork(FMLCommonSetupEvent event) {
    IModInfo info = ModLoadingContext.get().getActiveContainer().getModInfo();
    final String version = info.getVersion().toString();

    NETWORK = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"),
        () -> version,
        version::equals,
        version::equals
    );

    registerMessage(SaveNBTFileMessage.class, SaveNBTFileMessage::new);
  }
}
