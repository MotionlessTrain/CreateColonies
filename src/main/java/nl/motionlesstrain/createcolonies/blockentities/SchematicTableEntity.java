package nl.motionlesstrain.createcolonies.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import nl.motionlesstrain.createcolonies.resources.CreateColoniesResources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SchematicTableEntity extends BlockEntity {
  public SchematicTableEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(CreateColoniesResources.BlockEntities.schematicTableEntity.get(), p_155229_, p_155230_);
  }
  private @NotNull ItemStack createBlueprint = ItemStack.EMPTY;
  private @NotNull ItemStack structurizeTool = ItemStack.EMPTY;

  public @NotNull ItemStack getCreateBlueprint() {
    return createBlueprint;
  }

  public void setCreateBlueprint(@NotNull ItemStack createBlueprint) {
    this.createBlueprint = createBlueprint;
    setChanged();
  }

  public @NotNull ItemStack getStructurizeTool() {
    return structurizeTool;
  }

  public void setStructurizeTool(@NotNull ItemStack structurizeTool) {
    this.structurizeTool = structurizeTool;
    setChanged();
  }

  @Override
  protected void saveAdditional(@NotNull CompoundTag tag) {
    super.saveAdditional(tag);

    tag.put("createBlueprint", createBlueprint.save(new CompoundTag()));
    tag.put("structurizeTool", structurizeTool.save(new CompoundTag()));
  }

  @Override
  public void load(@NotNull CompoundTag tag) {
    super.load(tag);
    createBlueprint = Optional.ofNullable(tag.get("createBlueprint")).map(itemTag ->
        (CompoundTag)itemTag).map(ItemStack::of).orElse(ItemStack.EMPTY);
    structurizeTool = Optional.ofNullable(tag.get("structurizeTool")).map(itemTag ->
        (CompoundTag)itemTag).map(ItemStack::of).orElse(ItemStack.EMPTY);
  }

  private class ItemHandler implements IItemHandlerModifiable {

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
    public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
      // TODO. Also in the other setters
      return true;
    }
  }


  private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(ItemHandler::new);

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) {
      return itemHandler.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    itemHandler.invalidate();
    super.invalidateCaps();
  }
}
