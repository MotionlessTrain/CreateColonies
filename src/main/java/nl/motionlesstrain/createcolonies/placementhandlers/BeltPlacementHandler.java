package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.api.util.constant.Constants;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;

public class BeltPlacementHandler implements IPlacementHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return blockState.is(CreateResources.Blocks.belt);
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        final List<ItemStack> requiredItems = new ArrayList<>();

        if (blockState.hasProperty(BeltBlock.CASING) && blockState.getValue(BeltBlock.CASING)) {
            requiredItems.add(ItemUtils.stackFromNullable(CreateResources.Items.andesiteCasing));
        }
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
                            LOGGER.debug("\t\t{}\n\t\t{}", itemEntityTag, itemTag);
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

    private final Map<BlockPos, List<BeltInfo>> beltParts = new HashMap<>();
    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, @SuppressWarnings("removal") PlacementSettings settings) {
        if (tileEntityData == null) return ActionProcessingResult.DENY;

        final var controller = tileEntityData.getCompound("Controller");
        final int x = controller.getInt("X");
        final int y = controller.getInt("Y");
        final int z = controller.getInt("Z");
        final var controllerPos = new BlockPos(x, y, z);

        final var length = tileEntityData.getInt("Length");

        final var knownBeltParts = beltParts.computeIfAbsent(controllerPos, ignored -> new ArrayList<>());
        knownBeltParts.add(new BeltInfo(pos, blockState, tileEntityData));

        if (knownBeltParts.size() == length) {
            for (final var lit = knownBeltParts.listIterator(); lit.hasNext();) {
                final var info = lit.next();
                if(!world.setBlock(info.pos(), info.state(), Constants.UPDATE_FLAG)) {
                    while (lit.hasPrevious()) {
                        final var alreadyPlaced = lit.previous();
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
