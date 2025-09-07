package nl.motionlesstrain.createcolonies.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Blocks.schematicTable;
import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Menus.schematicTableMenu;

public class SchematicTableMenu extends AbstractContainerMenu {

  private final ContainerLevelAccess access;

  private final int ownSlots = 2;
  private final int hotBarStart = ownSlots + 27;
  private final int playerInvEnd = hotBarStart + 9;

  public SchematicTableMenu(int containerId, Inventory playerInv) {
    this(containerId, playerInv, ContainerLevelAccess.NULL, new ItemStackHandler(2));
  }

  public SchematicTableMenu(int containerId, Inventory playerInv, ContainerLevelAccess access, IItemHandler itemHandler) {
    super(schematicTableMenu.get(), containerId);
    this.access = access;

    final int ownInvMiddleX = 76;
    final int spaceBetweenSlots = 44;
    final int ownInvMiddleY = 35;

    addSlot(new SlotItemHandler(itemHandler, 0, ownInvMiddleX - (spaceBetweenSlots >> 1) - 18, ownInvMiddleY));
    addSlot(new SlotItemHandler(itemHandler, 1, ownInvMiddleX + (spaceBetweenSlots >> 1), ownInvMiddleY));

    final int playerInvXStart = 7;
    final int playerInvYStart = 82;
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
  }

  @Override
  public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
    Slot quickMovedSlot = slots.get(slotIndex);

    if (slotIndex < 0 || slotIndex >= slots.size() || !quickMovedSlot.hasItem()) {
      return ItemStack.EMPTY;
    }
    final ItemStack rawStack = quickMovedSlot.getItem();
    final ItemStack quickMovedStack = rawStack.copy();
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
}
