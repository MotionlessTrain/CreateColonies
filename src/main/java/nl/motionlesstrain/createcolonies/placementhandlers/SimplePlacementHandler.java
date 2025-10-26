package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Helper class for the placement helpers which just place a simple block in the world, where only the required items would be different */
public abstract class SimplePlacementHandler extends PlacementHandlers.GeneralBlockPlacementHandler {
    @Override
    public abstract boolean canHandle(Level level, BlockPos blockPos, BlockState blockState);

    @Override
    public abstract List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b);
}
