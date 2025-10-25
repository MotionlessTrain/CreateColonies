package nl.motionlesstrain.createcolonies.placementhandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static nl.motionlesstrain.createcolonies.resources.CreateResources.Blocks.*;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items.cogwheel;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items.largeCogwheel;

public class GearPlacementHandler extends SimplePlacementHandler {
  private final Map<Block, ItemStack> gearRequirements = Map.of(
      andesiteEncasedCogwheel, ItemUtils.stackFromNullable(cogwheel),
      brassEncasedCogwheel, ItemUtils.stackFromNullable(cogwheel),
      andesiteEncasedLargeCogwheel, ItemUtils.stackFromNullable(largeCogwheel),
      brassEncasedLargeCogwheel, ItemUtils.stackFromNullable(largeCogwheel)
  );

  @Override
  public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
    return gearRequirements.containsKey(blockState.getBlock());
  }

  @Override
  public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
    final ItemStack requirement = gearRequirements.get(blockState.getBlock());
    return requirement == null ? List.of() : List.of(requirement);
  }
}
