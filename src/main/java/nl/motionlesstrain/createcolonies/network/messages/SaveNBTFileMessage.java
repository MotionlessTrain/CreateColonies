package nl.motionlesstrain.createcolonies.network.messages;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import nl.motionlesstrain.createcolonies.network.NetworkMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;

import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;

public class SaveNBTFileMessage extends ClientBoundNetworkMessage {
  private static final CustomPacketPayload.Type<SaveNBTFileMessage> TYPE = new CustomPacketPayload.Type<>(
      ResourceLocation.fromNamespaceAndPath(MODID, "save_nbt_file")
  );

  private final String filePath;
  private final CompoundTag fileContents;

  public SaveNBTFileMessage(final String filePath, final CompoundTag fileContents) {
    this.filePath = filePath;
    this.fileContents = fileContents;
  }

  public String filePath() { return filePath; }
  public CompoundTag fileContents() { return fileContents; }

  @Override
  public @NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
  public static final StreamCodec<ByteBuf, SaveNBTFileMessage> STREAM_CODEC = StreamCodec.composite(
    ByteBufCodecs.STRING_UTF8,
    SaveNBTFileMessage::filePath,
    ByteBufCodecs.compoundTagCodec(NbtAccounter::unlimitedHeap),
    SaveNBTFileMessage::fileContents,
    SaveNBTFileMessage::new
  );

  private static final Logger LOGGER = LogUtils.getLogger();

  @Override
  ClientSideHandler getHandler() {
    return new ClientNBTSaver();
  }

  private class ClientNBTSaver implements ClientBoundNetworkMessage.ClientSideHandler {
    @Override
    public void handleMessage(@NotNull IPayloadContext context) {
      final Path gamePath = Minecraft.getInstance().gameDirectory.toPath();
      final Path saveFile = gamePath.resolve(filePath);
      try {
        NbtIo.writeCompressed(fileContents, saveFile);
        final @Nullable Player player = Minecraft.getInstance().player;
        if (player != null) {
          player.displayClientMessage(Component.translatable("nl.motionlesstrain.createcolonies.convert.confirm"), false);
        }
      } catch (IOException e) {
        LOGGER.error("Could not save file {}", filePath, e);
      }
    }
  }

  public static class Factory extends ClientBoundNetworkMessage.Factory<SaveNBTFileMessage> {

    @Override
    public Type<SaveNBTFileMessage> type() {
      return TYPE;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SaveNBTFileMessage> streamCodec() {
      return STREAM_CODEC;
    }
  }
}
