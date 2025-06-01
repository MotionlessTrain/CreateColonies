package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.api.util.constant.Constants;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;

public class BeltPlacementHandler implements IPlacementHandler {
    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return blockState.is(CreateResources.Blocks.belt);
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        final List<ItemStack> requiredItems = new ArrayList<>();

        if (blockState.hasProperty(BeltBlock.PART)) {
            if (blockState.getValue(BeltBlock.PART) != BeltPart.MIDDLE) {
                requiredItems.add(ItemUtils.stackFromNullable(CreateResources.Items.shaft));
            }
            if (blockState.getValue(BeltBlock.PART) == BeltPart.START) {
                requiredItems.add(ItemUtils.stackFromNullable(CreateResources.Items.belt));
                if (compoundTag != null && compoundTag.contains("Inventory", Tag.TAG_COMPOUND)) {

                    final var inventoryCompound = compoundTag.getCompound("Inventory");
                    final var itemEntities = inventoryCompound.getList("Items", Tag.TAG_COMPOUND);
                    for (final var itemEntity : itemEntities) {
                        if (itemEntity instanceof CompoundTag itemEntityTag) {
                            var itemTag = itemEntityTag.getCompound("Item");
                            requiredItems.add(ItemStack.of(itemTag));
                        }
                    }
                }
            }
        }
        // The start one seems to have an inventory
        return requiredItems;
    }

    private record BeltInfo(BlockPos pos, BlockState state, @Nullable CompoundTag tag) {}

    private final Map<BlockPos, SortedMap<BlockPos, BeltInfo>> beltParts = new HashMap<>();
    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, @SuppressWarnings("removal") PlacementSettings settings) {
        if (tileEntityData == null) return ActionProcessingResult.DENY;

        final var controller = tileEntityData.getCompound("Controller");
        final int x = controller.getInt("X");
        final int y = controller.getInt("Y");
        final int z = controller.getInt("Z");
        final var controllerPos = new BlockPos(x, y, z);

        final var length = tileEntityData.getInt("Length");

        final var knownBeltParts = beltParts.computeIfAbsent(controllerPos, ignored -> new TreeMap<>());
        knownBeltParts.put(pos, new BeltInfo(pos, blockState, tileEntityData));

        if (knownBeltParts.size() == length) {
            for (final Map.Entry<BlockPos, BeltInfo> entry : knownBeltParts.entrySet()) {
                final var info = entry.getValue();
                if (!world.setBlock(info.pos(), info.state(), Constants.UPDATE_FLAG)) {
                    for (final var alreadyPlaced : knownBeltParts.headMap(entry.getKey()).values()) {
                        world.removeBlock(alreadyPlaced.pos(), false);
                    }
                    return ActionProcessingResult.DENY;
                }
                handleTileEntityPlacement(info.tag(), world, info.pos(), settings);
            }
            beltParts.remove(controllerPos);
        }

        return ActionProcessingResult.SUCCESS;
    }
}
