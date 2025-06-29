package nl.motionlesstrain.createcolonies.placementhandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static nl.motionlesstrain.createcolonies.resources.CreateResources.Blocks;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items;

public class DeployerPlacementHandler extends SimplePlacementHandler {
  @Override
  public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
    return blockState.is(Blocks.deployer);
  }

  @Override
  public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
    final List<ItemStack> neededItems = new ArrayList<>();
    neededItems.add(ItemUtils.stackFromNullable(Items.deployer));
    if (compoundTag != null && compoundTag.contains("Inventory", Tag.TAG_LIST)) {
      final var inventory = compoundTag.getList("Inventory", Tag.TAG_COMPOUND);
      for (int i = 0; i < inventory.size(); i++) {
        final CompoundTag item = inventory.getCompound(i);
        neededItems.add(ItemStack.of(item));
      }
    }
    return neededItems;
  }
}
