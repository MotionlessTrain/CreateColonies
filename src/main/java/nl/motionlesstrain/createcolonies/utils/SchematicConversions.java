package nl.motionlesstrain.createcolonies.utils;

import com.ldtteam.structurize.storage.StructurePacks;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;
import nl.motionlesstrain.createcolonies.network.messages.SaveNBTFileMessage;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import org.slf4j.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

import static nl.motionlesstrain.createcolonies.network.MessagesHandler.NETWORK;

public class SchematicConversions {
  private static final DataFixer dataFixer = DataFixers.getDataFixer();
  private static final Logger LOGGER = LogUtils.getLogger();

  public static void createToStructurize(ServerPlayer player, String source, String destination) throws IOException {
    final String playerName = player.getName().getString();
    final Path sourcePath = Path.of(String.format(source, playerName));
    final Path targetPath = Path.of(String.format(destination, playerName.toLowerCase(Locale.US)));

    final CompoundTag oldSchematic = NbtIo.readCompressed(sourcePath.toFile());

    final int version = oldSchematic.getInt("DataVersion");
    final int currentVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

    final CompoundTag schematic = fixStructure(version, currentVersion, oldSchematic);

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

    NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SaveNBTFileMessage(targetPath.toString(), blueprint));
  }

  public static ItemStack structurizeToCreate(ServerPlayer player, String source, String destination) throws IOException {
    final String playerName = player.getName().getString();

    final Path targetPath = Path.of(String.format(destination, playerName));

    final Path sourcePath = Path.of(source);
    final String packId = sourcePath.getName(0).toString();
    final Path subPath = sourcePath.subpath(1, sourcePath.getNameCount());
    final var pack = StructurePacks.getStructurePack(packId);
    if (pack == null) {
      LOGGER.error("Unable to find pack with name {}", packId);
      return new ItemStack(CreateResources.Items.emptySchematic);
    }
    final Path resolved = pack.getPath().resolve(pack.getSubPath(subPath));

    final CompoundTag blueprint = NbtIo.readCompressed(Files.newInputStream(resolved));

    final int dataVersion = blueprint.getInt("mcversion");
    final int currentVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

    final short sizeX = blueprint.getByte("size_x");
    final short sizeY = blueprint.getByte("size_y");
    final short sizeZ = blueprint.getByte("size_z");
    final BlockPos size = new BlockPos(sizeX, sizeY, sizeZ);

    CompoundTag[][][] blockEntities = new CompoundTag[sizeY][sizeZ][sizeX];

    ListTag tileEntities = blueprint.getList("tile_entities", Tag.TAG_COMPOUND);
    for (final Tag tileEntity : tileEntities) {
      if (tileEntity instanceof CompoundTag tag) {
        final short x = tag.getShort("x");
        final short y = tag.getShort("y");
        final short z = tag.getShort("z");
        final CompoundTag newTag = tag.getString("id").startsWith("minecraft:") ?
            fixData(dataVersion, currentVersion, References.BLOCK_ENTITY, tag.copy()) : tag.copy();
        newTag.remove("x");
        newTag.remove("y");
        newTag.remove("z");
        blockEntities[y][z][x] = newTag;
      }
    }

    final int[] blocks = blueprint.getIntArray("blocks");
    final ListTag blocksList = new ListTag();
    int index = 0, shift = 16;
    for (short y = 0; y < sizeY; y++) {
      for (short z = 0; z < sizeZ; z++) {
        for (short x = 0; x < sizeX; x++) {
          short state = (short)(blocks[index] >>> shift);

          if (state != 0) {
            CompoundTag blockInfo = new CompoundTag();
            // - 1, as Structurize adds air as first state, which is not saved in schematic format (hence also the if statement)
            blockInfo.putInt("state", (state & 0xFFFF) - 1);
            blockInfo.put("pos", BlockPosUtil.toNBTList(new BlockPos(x, y, z)));

            if (blockEntities[y][z][x] != null) {
              blockInfo.put("nbt", blockEntities[y][z][x]);
            }
            blocksList.add(blockInfo);
          }

          shift ^= 16;
          if ((shift & 16) != 0) index++;
        }
      }
    }

    final ListTag palette = blueprint.getList("palette", Tag.TAG_COMPOUND);
    final ListTag newPalette = new ListTag();
    // We start with 1, as 0 is air, which is not represented on the palette in the schematics file
    for (int i = 1; i < palette.size(); i++) {
      final CompoundTag blockState = palette.getCompound(i);
      final CompoundTag newBlockState =
        fixData(dataVersion, currentVersion, References.BLOCK_STATE, blockState.copy());
      newPalette.add(newBlockState);
    }

    final ListTag entities = blueprint.getList("entities", Tag.TAG_COMPOUND);
    final ListTag newEntities = new ListTag();
    for (Tag tag : entities) {
      if (tag instanceof CompoundTag entity) {
        final CompoundTag newEntity = entity.getString("id").startsWith("minecraft:") ?
          fixData(dataVersion, currentVersion, References.ENTITY, entity.copy()) : entity.copy();
        final ListTag pos = newEntity.getList("Pos", Tag.TAG_DOUBLE).copy();
        final int posX = newEntity.getInt("TileX");
        final int posY = newEntity.getInt("TileY");
        final int posZ = newEntity.getInt("TileZ");
        final CompoundTag storedData = new CompoundTag();
        storedData.put("pos", pos);
        storedData.put("blockPos", BlockPosUtil.toNBTList(new BlockPos(posX, posY, posZ)));
        storedData.put("nbt", newEntity);
        newEntities.add(storedData);
      }
    }

    final CompoundTag schematic = new CompoundTag();
    schematic.putInt("DataVersion", currentVersion);
    schematic.put("size", BlockPosUtil.toNBTList(size));
    schematic.put("blocks", blocksList);
    schematic.put("palette", newPalette);
    schematic.put("entities", newEntities);

    // At the client, we just need to save schematics/nbt_file, not schematics/uploaded/playerName/nbt_file
    final Path clientPath = Path.of("schematics").resolve(targetPath.subpath(3, targetPath.getNameCount()));

    try {
      NbtIo.writeCompressed(schematic, targetPath.toFile());
    } catch(IOException e) {
      LOGGER.error("Saving of schematic file failed", e);
    }

    NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SaveNBTFileMessage(clientPath.toString(), schematic));

    final String targetName = targetPath.getFileName().toString();
    final ItemStack fullSchematic = new ItemStack(CreateResources.Items.schematic, 1);
    final CompoundTag schematicData = fullSchematic.getOrCreateTag();
    schematicData.put("Anchor", BlockPosUtil.toNBT(BlockPos.ZERO));
    schematicData.put("Bounds", BlockPosUtil.toNBTList(size));
    schematicData.putByte("Deployed", (byte)0);
    schematicData.putString("File", targetName);
    schematicData.putString("Mirror", "NONE");
    schematicData.putString("Owner", playerName);
    schematicData.putString("Rotation", "NONE");

    return fullSchematic;
  }

  private static CompoundTag fixStructure(int fromVersion, int toVersion, CompoundTag data) {
    if (fromVersion < toVersion) {
      DataFixTypes.STRUCTURE.updateToCurrentVersion(dataFixer, data, fromVersion);
    }
    return data;
  }

  private static CompoundTag fixData(int fromVersion, int toVersion, DSL.TypeReference dataType, CompoundTag data) {
    if (fromVersion < toVersion) {
      return (CompoundTag) dataFixer.update(dataType, new Dynamic<>(NbtOps.INSTANCE, data), fromVersion, toVersion).getValue();
    }
    return data;
  }
}
