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

import java.util.List;

public class TrackPlacementHandler implements IPlacementHandler {

    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return blockState.is(CreateResources.Blocks.fakeTrack);
    }

    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, @SuppressWarnings("removal") PlacementSettings settings) {
        world.setBlock(pos, blockState, Constants.UPDATE_FLAG);
        return ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        return List.of(ItemUtils.stackFromNullable(CreateResources.Items.track));
    }
}
