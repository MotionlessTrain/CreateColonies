package nl.motionlesstrain.createcolonies.placementhandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CopyCatPlacementHandler extends SimplePlacementHandler {
    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return blockState.is(CreateResources.Blocks.copycatPanel) ||
                blockState.is(CreateResources.Blocks.copycatStep);
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
        final List<ItemStack> itemList = new ArrayList<>();

        if (blockState.is(CreateResources.Blocks.copycatPanel)) {
          itemList.add(CreateResources.Blocks.copycatPanel.toStack(1));
        } else {
          itemList.add(CreateResources.Blocks.copycatStep.toStack(1));
        }

        if (compoundTag != null && compoundTag.contains("Item", Tag.TAG_COMPOUND)) {
          itemList.add(ItemUtils.stackFromNBT(level, compoundTag.getCompound("Item")));
        }
        return itemList;
    }
}
