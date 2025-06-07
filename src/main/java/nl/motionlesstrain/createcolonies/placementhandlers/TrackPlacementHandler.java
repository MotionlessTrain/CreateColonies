package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.util.PlacementSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.BlockPosUtil;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static nl.motionlesstrain.createcolonies.utils.BlockPosUtil.DoubleBlockPos;

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

    private final String[] VECTOR_TYPES = {"Starts", "Normals", "Axes"};
    private final DoubleBlockPos[] STARTS_OFFSETS = {
            new DoubleBlockPos(0, 0, 0),
            new DoubleBlockPos(1, 0, 0),
            new DoubleBlockPos(1, 0, 1),
            new DoubleBlockPos(0, 0, 1)
    };

    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, PlacementSettings settings) {

        @SuppressWarnings("removal") final Rotation blueprintRotation = settings.getRotation();

        if (tileEntityData != null && tileEntityData.contains("Connections", Tag.TAG_LIST)) {
            final ListTag connections = tileEntityData.getList("Connections", Tag.TAG_COMPOUND);
            for (int i = 0; i < connections.size(); i++) {
                final var connection = connections.getCompound(i);
                for (final String vectorType : VECTOR_TYPES) {
                    if (connection.contains(vectorType, Tag.TAG_LIST)) {
                        final ListTag vectorInfo = connection.getList(vectorType, Tag.TAG_COMPOUND);
                        for (int j = 0; j < vectorInfo.size(); j++) {
                            final var vectorObj = vectorInfo.getCompound(j);
                            if (vectorObj.contains("V", Tag.TAG_LIST)) {
                                final var vector = vectorObj.getList("V", Tag.TAG_DOUBLE);
                                final DoubleBlockPos blockPos = new DoubleBlockPos(vector);
                                DoubleBlockPos newBlockPos = blockPos.rotate(blueprintRotation);
                                if (vectorType.equals("Starts")) {
                                    newBlockPos = newBlockPos.add(STARTS_OFFSETS[blueprintRotation.ordinal()]);
                                }
                                final var newVector = newBlockPos.toNBT();
                                vectorObj.put("V", newVector);
                            }
                        }
                    }
                }
                if (connection.contains("Positions", Tag.TAG_LIST)) {
                    final ListTag positionInfo = connection.getList("Positions", Tag.TAG_COMPOUND);
                    for (int j = 0; j < positionInfo.size(); j++) {
                        final var positionObj = positionInfo.getCompound(j);
                        final BlockPos position = BlockPosUtil.fromNBT(positionObj);
                        final BlockPos rotated = position.rotate(blueprintRotation);
                        final var newPositionObj = BlockPosUtil.toNBT(rotated);
                        positionInfo.set(j, newPositionObj);
                    }
                }
            }
        }

        ActionProcessingResult result = super.handle(world, pos, blockState, tileEntityData, complete, centerPos, settings);

        if (result != ActionProcessingResult.DENY) {
            final var actions = dependents.remove(pos);
            if (actions != null) actions.forEach(Runnable::run);
        }
        return result;
    }
}
