package nl.motionlesstrain.createcolonies.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import nl.motionlesstrain.createcolonies.blockentities.SchematicTableEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Blocks.schematicTable;
import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Menus.schematicTableMenu;

public class SchematicTableMenu extends AbstractContainerMenu {

  private final ContainerLevelAccess access;
  private final DataSlot toggle;
  private final IItemHandler itemHandler;
  private final @Nullable BlockPos schematicTablePos;

  public SchematicTableMenu(int containerId, Inventory playerInv) {
    this(containerId, playerInv, ContainerLevelAccess.NULL, new ItemStackHandler(2), DataSlot.standalone(), null);
  }

  public SchematicTableMenu(int containerId, Inventory playerInv, ContainerLevelAccess access, IItemHandler itemHandler, DataSlot toggle, @Nullable BlockPos pos) {
    super(schematicTableMenu.get(), containerId);
    this.access = access;
    this.toggle = toggle;
    this.itemHandler = itemHandler;
    this.schematicTablePos = pos;

    final int ownInvMiddleX = 88;
    final int spaceBetweenSlots = 44;
    final int ownInvMiddleY = 36;

    addSlot(new SlotItemHandler(itemHandler, 0, ownInvMiddleX - (spaceBetweenSlots >> 1) - 17, ownInvMiddleY));
    addSlot(new SlotItemHandler(itemHandler, 1, ownInvMiddleX + (spaceBetweenSlots >> 1) + 1, ownInvMiddleY));

    final int playerInvXStart = 8;
    final int playerInvYStart = 84;
    final int playerHotbarStart = 142;
    int slotId = 9;

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(playerInv, slotId++, playerInvXStart + x * 18, playerInvYStart + y * 18));
      }
    }
    for (int x = 0; x < 9; x++) {
      // Hotbar is the first 9 slots in a player's inventory (0 - 9, just like x)
      addSlot(new Slot(playerInv, x, playerInvXStart + x * 18, playerHotbarStart));
    }
    addDataSlot(toggle);
  }

  @Override
  public boolean clickMenuButton(@NotNull Player player, int buttonId) {
    switch (buttonId) {
      case 0:
        toggleButton();
        break;
      case 1:
        if (schematicTablePos != null) {
          startConverting(player, schematicTablePos);
        }
        break;
      default:
        return false;
    }
    return true;
  }

  private void startConverting(Player player, BlockPos schematicTablePos) {
    final BlockEntity blockEntity = player.level().getBlockEntity(schematicTablePos);
    if (blockEntity instanceof SchematicTableEntity schematicTableEntity) {
      if (player instanceof ServerPlayer serverPlayer) {
        schematicTableEntity.convert(serverPlayer);
      }
    }
  }

  private void toggleButton() {
    final int newState = (toggle.get() + 1) & 1;
    toggle.set(newState);
  }
  public boolean pointsToBlueprint() {
    return toggle.get() == 0;
  }

  @Override
  public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
    Slot quickMovedSlot = slots.get(slotIndex);

    if (slotIndex < 0 || slotIndex >= slots.size() || !quickMovedSlot.hasItem()) {
      return ItemStack.EMPTY;
    }
    final ItemStack rawStack = quickMovedSlot.getItem();
    final ItemStack quickMovedStack = rawStack.copy();

    final int ownSlots = 2;
    final int hotBarStart = ownSlots + 27;
    final int playerInvEnd = hotBarStart + 9;

    if (slotIndex < ownSlots) {
      if (!moveItemStackTo(rawStack, ownSlots, playerInvEnd, true)) {
        return ItemStack.EMPTY;
      }
    } else if (slotIndex < playerInvEnd) {
      if (!this.moveItemStackTo(rawStack, 0, ownSlots, false)) {
        if (slotIndex < hotBarStart) {
          if (!this.moveItemStackTo(rawStack, hotBarStart, playerInvEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if(!this.moveItemStackTo(rawStack, ownSlots, hotBarStart, false)) {
          return ItemStack.EMPTY;
        }
      }
    }
    if (rawStack.isEmpty()) {
      quickMovedSlot.set(ItemStack.EMPTY);
    } else {
      quickMovedSlot.setChanged();
    }
    return quickMovedStack;
  }

  @Override
  public boolean stillValid(@NotNull Player player) {
    return stillValid(this.access, player, schematicTable.get());
  }

  public IItemHandler getItemHandler() {
    return itemHandler;
  }
}
