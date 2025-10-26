package nl.motionlesstrain.createcolonies.blockentities;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.storage.rendering.RenderingCache;
import com.ldtteam.structurize.storage.rendering.types.BlueprintPreviewData;
import com.ldtteam.structurize.util.ScanToolData;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import nl.motionlesstrain.createcolonies.gui.SchematicTableMenu;
import nl.motionlesstrain.createcolonies.resources.CreateColoniesResources;
import nl.motionlesstrain.createcolonies.utils.SchematicConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

import static nl.motionlesstrain.createcolonies.resources.CreateResources.DataComponentTypes.schematicFile;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items.emptySchematic;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items.schematic;
import static nl.motionlesstrain.createcolonies.resources.StructurizeResources.Items.buildTool;
import static nl.motionlesstrain.createcolonies.resources.StructurizeResources.Items.scanTool;

public class SchematicTableEntity extends BlockEntity {
  private static final Logger LOGGER = LogUtils.getLogger();

  public SchematicTableEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(CreateColoniesResources.BlockEntities.schematicTableEntity.get(), p_155229_, p_155230_);
  }
  private @NotNull ItemStack createBlueprint = ItemStack.EMPTY;
  private @NotNull ItemStack structurizeTool = ItemStack.EMPTY;

  private boolean toBlueprint = true;

  public @NotNull ItemStack getCreateBlueprint() {
    return createBlueprint;
  }

  private void setCreateBlueprint(@NotNull ItemStack createBlueprint) {
    this.createBlueprint = createBlueprint;
    setChanged();
  }

  public @NotNull ItemStack getStructurizeTool() {
    return structurizeTool;
  }

  private void setStructurizeTool(@NotNull ItemStack structurizeTool) {
    this.structurizeTool = structurizeTool;
    setChanged();
  }

  @Override
  protected void saveAdditional(@NotNull final CompoundTag tag, @NotNull final HolderLookup.Provider registryAccess) {
    super.saveAdditional(tag, registryAccess);

    tag.put("createBlueprint", createBlueprint.saveOptional(registryAccess));
    tag.put("structurizeTool", structurizeTool.saveOptional(registryAccess));
    tag.putBoolean("toBlueprint", toBlueprint);
  }

  @Override
  public void loadAdditional(@NotNull final CompoundTag tag, final HolderLookup.@NotNull Provider registryAccess) {
    super.loadAdditional(tag, registryAccess);
    createBlueprint = ItemStack.parseOptional(registryAccess, tag.getCompound("createBlueprint"));
    structurizeTool = ItemStack.parseOptional(registryAccess, tag.getCompound("structurizeTool"));
    toBlueprint = !tag.contains("toBlueprint", CompoundTag.TAG_BYTE) || tag.getBoolean("toBlueprint");
  }

  private BlockCapabilityCache<IItemHandler, @Nullable Direction> itemHandlerCache;
  public @Nullable MenuProvider getMenuProvider() {
    if (level != null) {
      if (itemHandlerCache == null && level instanceof ServerLevel serverLevel) {
        itemHandlerCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, worldPosition, Direction.UP);
      }
      final IItemHandler itemHandler = itemHandlerCache == null ? null : itemHandlerCache.getCapability();
      return new SimpleMenuProvider(
          (containerId, playerInventory, ignored) ->
              new SchematicTableMenu(containerId, playerInventory, ContainerLevelAccess.create(level, worldPosition),
                  itemHandler,
                  getToggle(), worldPosition
              ),
          Component.translatable("menu.title.createcolonies.schematic_table_menu")
      );
    }
    return null;
  }

  public void openMenuForPlayer(@NotNull Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.openMenu(getMenuProvider());
    }
  }

  public void convert(ServerPlayer player) {
    try {
      if (toBlueprint) {
        final String source = getFileName(0);
        final String target = getFileName(1);
        SchematicConversions.createToStructurize(player, source, target);
      } else {
        final String source = getFileName(1);
        final String target = getFileName(0);
        final ItemStack fullSchematic = SchematicConversions.structurizeToCreate(player, source, target);

        setCreateBlueprint(fullSchematic);
      }
    } catch(IOException e) {
      LOGGER.error("Could not convert {} to {}", toBlueprint ? "schematic" : "blueprint", toBlueprint ? "blueprint" : "schematic", e);
    }
  }

  private class ToggleSlot extends DataSlot {
    @Override
    public int get() {
      return toBlueprint ? 0 : 1;
    }

    @Override
    public void set(int i) {
      toBlueprint = i == 0;
      setChanged();
    }
  }

  public DataSlot getToggle() {
    return new ToggleSlot();
  }

  public class ItemHandler implements IItemHandlerModifiable {

    @Override
    public int getSlots() {
      return 2;
    }

    private void validateIndex(int i) {
      if (i < 0 || i >= 2) throw new IllegalArgumentException("i out of bounds");
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
      validateIndex(i);
      return i == 0 ? createBlueprint : structurizeTool;
    }

    @Override
    public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
      validateIndex(i);

      if (i == 0) {
        setCreateBlueprint(itemStack);
      } else {
        setStructurizeTool(itemStack);
      }
    }


    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean simulate) {
      if (itemStack.isEmpty() || !isItemValid(i, itemStack)) {
        return itemStack;
      }
      validateIndex(i);
      if (getStackInSlot(i).isEmpty()) {
        final ItemStack newStack = itemStack.copyWithCount(1);
        if (!simulate) {
          setStackInSlot(i, newStack);
        }
        if (itemStack.getCount() > 1) {
          return itemStack.copyWithCount(itemStack.getCount() - 1);
        }
        return ItemStack.EMPTY;
      }
      return itemStack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
      final ItemStack itemStack = getStackInSlot(slot);
      if (!simulate) setStackInSlot(slot, ItemStack.EMPTY);
      return itemStack;
    }

    @Override
    public int getSlotLimit(int i) {
      return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack itemStack) {
      return switch (slot) {
        case 0 -> itemStack.isEmpty() || (toBlueprint ? itemStack.is(schematic) : itemStack.is(emptySchematic));
        case 1 -> itemStack.isEmpty() || (toBlueprint ? itemStack.is(scanTool) : itemStack.is(buildTool));
        default -> false;
      };
    }
  }

  public static String getFileName(@NotNull IItemHandler inventory, boolean toBlueprint, final int slot) {

    ItemStack createItem = inventory.getStackInSlot(0);
    ItemStack structurizeItem = inventory.getStackInSlot(1);

    if (!createItem.isEmpty() && !createItem.is(schematic) && !createItem.is(emptySchematic)) {
      // Invalid item
      if (slot == 0) return null;
      createItem = ItemStack.EMPTY;
    }

    if (!structurizeItem.isEmpty() && !structurizeItem.is(scanTool) && !structurizeItem.is(buildTool)) {
      // Invalid item
      if (slot == 1) return null;
      structurizeItem = ItemStack.EMPTY;
    }

    return getFileName(createItem, structurizeItem, toBlueprint, slot, false);

  }

  public String getFileName(final int slot) {
    final String path = getFileName(createBlueprint, structurizeTool, toBlueprint, slot, true);
    return path == null || path.isEmpty() ? null : path;
  }

  private static String getFileName(final ItemStack createItem, final ItemStack structurizeItem, final boolean toBlueprint, final int slot, boolean fullName) {
    if (toBlueprint) {
      if (!createItem.is(schematic)) {
        return "";
      }
      if (slot == 1 && structurizeItem.is(scanTool)) {
        final ScanToolData scanToolData = ScanToolData.readFromItemStack(structurizeItem);
        final String scanName = scanToolData.currentSlot().name();
        if (!scanName.isEmpty()) {
          if (fullName) return Path.of("blueprints", "%s", "scans", scanName + ".blueprint").toString();
          return scanName.substring(scanName.lastIndexOf('/') + 1) + ".blueprint";
        }
      } else if (slot == 1) return "";

      String createFileName = createItem.getOrDefault(schematicFile, "");

      if (createFileName.isEmpty()) return null;
      if (slot == 0) {
        if (fullName) return Path.of("schematics", "uploaded", "%s", createFileName).toString();
        return createFileName;
      }
      if (fullName) return Path.of("blueprints", "%s", "scans", createFileName.replace(".nbt", ".blueprint")).toString();
      return createFileName.replace(".nbt", ".blueprint");
    } else {
      if (!structurizeItem.is(buildTool)) {
        return "";
      }
      String structurizeFileName = null;

      BlueprintPreviewData renderedBlueprint = RenderingCache.getBlueprintPreviewData("blueprint");
      Blueprint blueprint = renderedBlueprint == null || renderedBlueprint.isEmpty() ? null : renderedBlueprint.getBlueprint();
      if (blueprint != null) {
        structurizeFileName = blueprint.getFileName();
      }
      if (slot == 0) {
        if (!createItem.is(emptySchematic)) return "";
        if (fullName) {
          return structurizeFileName == null ? null : Path.of("schematics", "uploaded", "%s", structurizeFileName + ".nbt").toString();
        }
        return structurizeFileName == null ? null : structurizeFileName + ".nbt";
      }
      if (fullName) {
        return blueprint == null ? null : Path.of(blueprint.getPackName()).resolve(blueprint.getFilePath().toString()).resolve(structurizeFileName + ".blueprint").toString();
      }
      return structurizeFileName == null ? null : structurizeFileName + ".blueprint";
    }

  }

  public static boolean isFileNameDefaulted(@NotNull IItemHandler inventory, int slot, boolean toBlueprint) {
    String name = getFileName(inventory, toBlueprint, slot);
    if (name == null) {
      if (toBlueprint ? slot == 0 : slot == 1) return true;
    }
    if (toBlueprint && slot == 1) {
      final ItemStack structurizeItem = inventory.getStackInSlot(1);
      if (structurizeItem.is(scanTool)) {
        final ScanToolData scanToolData = ScanToolData.readFromItemStack(structurizeItem);
        return scanToolData.currentSlot().name().isEmpty();
      }
      return true;
    } else return !toBlueprint && slot == 0;
  }

  public static boolean canConvert(IItemHandler itemHandler, boolean toBlueprint) {
    final String createName = getFileName(itemHandler, toBlueprint, 0);
    final String structurizeName = getFileName(itemHandler, toBlueprint, 1);
    return createName != null && !createName.isEmpty() && structurizeName != null && !structurizeName.isEmpty();
  }

}
