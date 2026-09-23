package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.placement.IPlacementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.BlockPosUtil;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VaultPlacementHandler extends SimplePlacementHandler {

  @Override
  public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
    return blockState.is(CreateResources.Blocks.itemVault);
  }

  @Override
  public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
    final ArrayList<ItemStack> requirements = new ArrayList<>();
    if (compoundTag != null && compoundTag.contains("Inventory", CompoundTag.TAG_COMPOUND)) {
      final ItemStackHandler inventory = new ItemStackHandler();
      inventory.deserializeNBT(level.registryAccess(), compoundTag.getCompound("Inventory"));
      final int size = inventory.getSlots();
      for (int i = 0; i < size; i++) {
        requirements.add(inventory.getStackInSlot(i));
      }
    }
    requirements.add(ItemUtils.stackFromDeferred(CreateResources.Blocks.itemVault));
    return requirements;
  }

  @Override
  public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, IPlacementContext placementContext) {
    if (tileEntityData != null) {
      if (tileEntityData.contains("Controller")) {
        final BlockPos controller = BlockPosUtil.fromNBT(tileEntityData, "Controller");
        final BlockPos lastKnown = BlockPosUtil.fromNBT(tileEntityData, "LastKnownPos");
        final BlockPos offset = controller.subtract(lastKnown);
        final BlockPos rotatedOffset = placementContext.getRotationMirror().applyToPos(offset);
        tileEntityData.put("Controller", BlockPosUtil.toNBT(pos.offset(rotatedOffset)));
      }
      tileEntityData.put("LastKnownPos", BlockPosUtil.toNBT(pos));
    }
    return super.handle(world, pos, blockState, tileEntityData, placementContext);
  }
}
