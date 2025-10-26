package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.common.util.BlockToItemHelper;
import com.ldtteam.structurize.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
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
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        final List<ItemStack> itemList = new ArrayList<>();
        itemList.add(BlockToItemHelper.getItemStack((ServerLevel) level, blockPos));
        if (compoundTag != null && compoundTag.contains("Item", Tag.TAG_COMPOUND)) {
            itemList.add(ItemUtils.stackFromNBT(level, compoundTag.getCompound("Item")));
        }
        return itemList;
    }
}
