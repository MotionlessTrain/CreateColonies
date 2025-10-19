package nl.motionlesstrain.createcolonies.network.messages;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;

public class SaveNBTFileMessage extends ClientBoundNetworkMessage {
  private static final Logger LOGGER = LogUtils.getLogger();
  private final String filePath;
  private final CompoundTag fileContents;

  public SaveNBTFileMessage(String filePath, CompoundTag contents) {
    this.filePath = filePath;
    fileContents = contents;
  }

  public SaveNBTFileMessage(FriendlyByteBuf buffer) {
    filePath = buffer.readUtf();
    CompoundTag contents = null;
    try (ByteBufInputStream input = new ByteBufInputStream(buffer)){
      contents = NbtIo.readCompressed(input);
    } catch (IOException e) {
      LOGGER.error("Could not read file contents", e);
    }
    fileContents = contents;
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeUtf(filePath);
    try (ByteBufOutputStream output = new ByteBufOutputStream(buffer)) {
      NbtIo.writeCompressed(fileContents, output);
    } catch (IOException e) {
      LOGGER.error("Could not write file contents", e);
    }
  }

  @Override
  protected ClientsideHandler createHandler() {
    return new ClientNBTSaver();
  }

  private class ClientNBTSaver implements ClientsideHandler {

    @Override
    public void handlePacket(NetworkEvent.Context context) {
      final Path gamePath = Minecraft.getInstance().gameDirectory.toPath();
      final Path saveFile = gamePath.resolve(filePath);
      try {
        NbtIo.writeCompressed(fileContents, saveFile.toFile());
        final @Nullable Player player = Minecraft.getInstance().player;
        if (player != null) {
          player.displayClientMessage(Component.translatable("nl.motionlesstrain.createcolonies.convert.confirm"), false);
        }
      } catch (IOException e) {
        LOGGER.error("Could not save file {}", filePath, e);
      }
    }
  }
}
