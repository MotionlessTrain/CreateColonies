package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.api.util.constant.Constants;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG;

/** Helper class for the placement helpers which just place a simple block, without nbt, in the world */
public abstract class SimplePlacementHandler implements IPlacementHandler {
    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, @SuppressWarnings("removal") PlacementSettings settings) {
        if (world.getBlockState(pos).equals(blockState))
        {
            world.removeBlock(pos, false);
            world.setBlock(pos, blockState, UPDATE_FLAG);

            return ActionProcessingResult.PASS;
        }

        if (!world.setBlock(pos, blockState, UPDATE_FLAG))
        {
            return ActionProcessingResult.DENY;
        }

        return ActionProcessingResult.SUCCESS;
    }
}
