package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.api.util.ItemStackUtils;
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
import java.util.List;

// TODO: Not a SimplePlacementHandler, as the belt is a multiblock. Place everything at once, if possible?
public class BeltPlacementHandler extends SimplePlacementHandler {
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
}
