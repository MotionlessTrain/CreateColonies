package nl.motionlesstrain.createcolonies.utils;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PacketDistributor;
import nl.motionlesstrain.createcolonies.network.messages.SaveNBTFileMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

import static nl.motionlesstrain.createcolonies.network.MessagesHandler.NETWORK;

public class SchematicConversions {
  public static void createToStructurize(ServerPlayer player, String source, String destination) throws IOException {
    final String playerName = player.getName().getString();
    final Path sourcePath = Path.of(String.format(source, playerName));
    final Path targetPath = Path.of(String.format(destination, playerName.toLowerCase(Locale.US)));

    final CompoundTag schematic = NbtIo.readCompressed(sourcePath.toFile());

    System.out.println(schematic);

    final int version = schematic.getInt("DataVersion");
    final int currentVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    if (version < currentVersion) {
      DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), schematic, version);
    }

    final BlockPos size = BlockPosUtil.fromNBT(schematic.getList("size", Tag.TAG_INT));
    short[][][] blocks = new short[size.getY()][size.getZ()][size.getX()];
    CompoundTag[][][] nbt = new CompoundTag[size.getY()][size.getZ()][size.getX()];

    final ListTag blocksInSchematic = schematic.getList("blocks", Tag.TAG_COMPOUND);

    for (int i = 0; i < blocksInSchematic.size(); i++) {
      final CompoundTag block = blocksInSchematic.getCompound(i);
      final BlockPos pos = BlockPosUtil.fromNBT(block.getList("pos", Tag.TAG_INT));
      // Create / minecraft omit air from the block list. Structurize includes it as state 0.
      // Hence, we need to shift the states by one, such that the blocks with state 0 don't become air out of the sudden
      final short state = (short) (block.getInt("state") + 1);
      final @Nullable CompoundTag blockEntityData = block.contains("nbt", Tag.TAG_COMPOUND) ?
          block.getCompound("nbt") : null;
      blocks[pos.getY()][pos.getZ()][pos.getX()] = state;
      if (blockEntityData != null) {
        nbt[pos.getY()][pos.getZ()][pos.getX()] = blockEntityData;
      }
    }
    // + 1, to let it round upwards
    final int[] blockArray = new int[(size.getY() * size.getZ() * size.getX() + 1) / 2];
    int index = 0, shift = 16;
    for (short[][] layer : blocks) {
      for (short[] column : layer) {
        for (short block : column) {
          blockArray[index] |= block << shift;
          shift ^= 16;
          if ((shift & 16) != 0) index++;
        }
      }
    }
    final ListTag newPalette = new ListTag();
    newPalette.add(NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
    final ListTag palette = schematic.getList("palette", Tag.TAG_COMPOUND);
    newPalette.addAll(palette.copy());
    final ListTag seenMods = new ListTag();
    palette.stream().map(CompoundTag.class::cast).map(tag ->
        ResourceLocation.tryParse(tag.getString("Name"))
    ).filter(Objects::nonNull).map(ResourceLocation::getNamespace).filter(namespace ->
        !namespace.equals("minecraft") && !namespace.equals("structurize")
    ).distinct().map(StringTag::valueOf).forEach(seenMods::add);

    final CompoundTag blueprint = new CompoundTag();
    blueprint.putInt("mcversion", currentVersion);
    blueprint.putShort("size_x", (short)size.getX());
    blueprint.putShort("size_y", (short)size.getY());
    blueprint.putShort("size_z", (short)size.getZ());
    blueprint.putString("name", targetPath.getFileName().toString());
    blueprint.putByte("version", (byte) 1);
    blueprint.put("required_mods", seenMods);

    blueprint.putIntArray("blocks", blockArray);
    blueprint.put("palette", newPalette);
    final ListTag newEntities = schematic.getList("entities", Tag.TAG_COMPOUND).copy().stream()
        .map(CompoundTag.class::cast)
        .map(tag -> {
          final ListTag pos = tag.getList("blockPos", Tag.TAG_INT);
          final ListTag exactPos = tag.getList("pos", Tag.TAG_DOUBLE);
          final CompoundTag entityNbt = tag.getCompound("nbt");
          entityNbt.put("TileX", pos.get(0));
          entityNbt.put("TileY", pos.get(1));
          entityNbt.put("TileZ", pos.get(2));
          entityNbt.put("Pos", exactPos);
          return entityNbt;
        }).collect(ListTag::new, ListTag::add, ListTag::addAll);
    blueprint.put("entities", newEntities);
    final ListTag blockEntities = new ListTag();
    int viableAnchors = 0;
    Optional<BlockPos> primaryPos = Optional.empty();
    Optional<CompoundTag> blueprintDataProvider = Optional.empty();
    for (short y = 0; y < nbt.length; y++) {
      for (short z = 0; z < nbt[y].length; z++) {
        for (short x = 0; x < nbt[y][z].length; x++) {
          if (nbt[y][z][x] != null) {
            final CompoundTag newBlockEntity = nbt[y][z][x].copy();
            newBlockEntity.putShort("x", x);
            newBlockEntity.putShort("y", y);
            newBlockEntity.putShort("z", z);
            if (newBlockEntity.contains("blueprintDataProvider", Tag.TAG_COMPOUND)) {
              if (viableAnchors++ == 0) {
                primaryPos = Optional.of(new BlockPos(x, y, z));
                blueprintDataProvider = Optional.of(newBlockEntity.getCompound("blueprintDataProvider"));
              } else {
                primaryPos = Optional.empty();
                blueprintDataProvider = Optional.empty();
              }
            }
            blockEntities.add(newBlockEntity);
          }
        }
      }
    }
    blueprint.put("tile_entities", blockEntities);

    if (primaryPos.isPresent() && blueprintDataProvider.isPresent()) {
      final CompoundTag primaryOffset = new CompoundTag();
      final BlockPos pos = primaryPos.get();
      primaryOffset.putInt("x", pos.getX());
      primaryOffset.putInt("y", pos.getY());
      primaryOffset.putInt("z", pos.getZ());
      final CompoundTag structurizeData = new CompoundTag();
      structurizeData.put("primary_offset", primaryOffset);
      final CompoundTag optionalData = new CompoundTag();
      optionalData.put("structurize", structurizeData);
      blueprint.put("optional_data", optionalData);

      blueprintDataProvider.ifPresent(blueprintData -> {
        BlockPos.MutableBlockPos corner = new BlockPos.MutableBlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
        final CompoundTag corner1 = new CompoundTag();
        corner1.putInt("x", corner.getX());
        corner1.putInt("y", corner.getY());
        corner1.putInt("z", corner.getZ());
        blueprintData.put("corner1", corner1);
        corner.move(size);
        corner.move(-1, -1, -1);
        final CompoundTag corner2 = new CompoundTag();
        corner2.putInt("x", corner.getX());
        corner2.putInt("y", corner.getY());
        corner2.putInt("z", corner.getZ());
        blueprintData.put("corner2", corner2);

        // Remove `blueprint/playerName`
        final Path path = targetPath.subpath(2, targetPath.getNameCount());
        blueprintData.putString("path", path.toString());
        blueprintData.putString("schematicName", targetPath.getFileName().toString().replace(".blueprint", ""));
      });
    }

    // TODO: Send to player, to actually store it
    System.out.println(blueprint);
    System.out.println(targetPath);

    NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SaveNBTFileMessage(targetPath.toString(), blueprint));
  }

  public static void structurizeToCreate(ServerPlayer player, String source, String destination) throws IOException {
    final String playerName = player.getName().getString();
    System.out.println(String.format(source, playerName.toLowerCase(Locale.US)));
    System.out.println(String.format(destination, playerName));
  }
}
