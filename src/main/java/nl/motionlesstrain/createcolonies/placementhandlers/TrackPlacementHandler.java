package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.api.util.constant.Constants;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TrackPlacementHandler extends SimplePlacementHandler {

    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return blockState.is(CreateResources.Blocks.track) || blockState.is(CreateResources.Blocks.fakeTrack);
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        return List.of(ItemUtils.stackFromNullable(CreateResources.Items.track));
    }

    private Map<BlockPos, List<Runnable>> dependents = new HashMap<>();
    public void addDependent(BlockPos pos, Runnable action) {
        dependents.computeIfAbsent(pos, ignored -> new LinkedList<>()).add(action);
    }

    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, PlacementSettings settings) {
        ActionProcessingResult result =  super.handle(world, pos, blockState, tileEntityData, complete, centerPos, settings);

        if (result != ActionProcessingResult.DENY) {
            final var actions = dependents.remove(pos);
            if (actions != null) actions.forEach(Runnable::run);
        }
        return result;
    }
}
