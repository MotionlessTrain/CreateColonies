package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.BlockUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class PlacementHandlers {
    private static void addHandler(IPlacementHandler handler) {
        com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.add(handler);
    }
    public static void initialiseHandlers(FMLLoadCompleteEvent ignored) {
        // TODO: Encased blocks (SimplePlacementHandlers to fix material cost)
        // TODO: Station (doesn't rotate)
        // TODO: Some other fix for curved rails? If there seems to be a solution for belts as well?
        // TODO: (Low Priority) rotating blocks?????
        addHandler(new BeltPlacementHandler());
        addHandler(new TrainBogeyPlacementHandler());
        addHandler(new TrackPlacementHandler());
        addHandler(new DebugPlacementHandler());
    }

    private static class DebugPlacementHandler implements IPlacementHandler {
        private static final Logger LOGGER = LogUtils.getLogger();

        @Override
        public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
            LOGGER.debug("Trying to handle block state {} at position {}, needing items {} if not overridden", blockState, blockPos,
                    BlockUtils.getItemStackFromBlockState(blockState));
            return false;
        }

        @Override
        public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
            return List.of();
        }
    }
}
