package nl.motionlesstrain.createcolonies.placementhandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinkedLecternPlacementHandler extends SimplePlacementHandler {
  private final boolean requireBook;
  public LinkedLecternPlacementHandler() {
    this(true);
  }
  public LinkedLecternPlacementHandler(boolean requireBook) {
    this.requireBook = requireBook;
  }
  @Override
  public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
    return blockState.is(CreateResources.Blocks.lecternController);
  }

  @Override
  public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
    final List<ItemStack> neededItems = new ArrayList<>(Arrays.asList(new ItemStack(Items.LECTERN), ItemUtils.stackFromNullable(CreateResources.Items.linkedController)));
    if (requireBook && compoundTag != null) {
      final BlockEntity tileEntity = BlockEntity.loadStatic(blockPos, blockState, compoundTag);
      if (tileEntity instanceof LecternBlockEntity lecternBlockEntity) {
        if (lecternBlockEntity.hasBook()) {
          neededItems.add(new ItemStack(Items.BOOK));
        }
      }
    }
    return neededItems;
  }
}
