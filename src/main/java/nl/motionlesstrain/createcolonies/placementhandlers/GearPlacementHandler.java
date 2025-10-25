package nl.motionlesstrain.createcolonies.placementhandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
  private final Map<ResourceLocation, ItemStack> gearRequirements = Map.of(
      andesiteEncasedCogwheel.getId(), ItemUtils.stackFromNullable(cogwheel),
      brassEncasedCogwheel.getId(), ItemUtils.stackFromNullable(cogwheel),
      andesiteEncasedLargeCogwheel.getId(), ItemUtils.stackFromNullable(largeCogwheel),
      brassEncasedLargeCogwheel.getId(), ItemUtils.stackFromNullable(largeCogwheel)
  );

  @Override
  public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
    return gearRequirements.containsKey(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
  }

  @Override
  public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
    final ItemStack requirement = gearRequirements.get(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
    return requirement == null ? List.of() : List.of(requirement);
  }
}
