package nl.motionlesstrain.createcolonies.placementhandlers;


import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers;
import com.ldtteam.structurize.util.PlacementSettings;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.BlockPosUtil;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Direction.Axis;

public class TrainStationPlacementHandler extends PlacementHandlers.GeneralBlockPlacementHandler {
    @Override
    public boolean canHandle(Level world, BlockPos pos, BlockState blockState) {
        return blockState.is(CreateResources.Blocks.trackStation) || blockState.is(CreateResources.Blocks.trackSignal)
                || blockState.is(CreateResources.Blocks.trackObserver);
    }

    private Tuple<BlockPos, CompoundTag> fixTargetTrack(CompoundTag targetTrack, Rotation rotation) {
        final BlockPos targetTrackPos = BlockPosUtil.fromNBT(targetTrack);
        final BlockPos newTargetTrack = targetTrackPos.rotate(rotation);
        final CompoundTag newTargetTrackData = BlockPosUtil.toNBT(newTargetTrack);
        return new Tuple<>(newTargetTrack, newTargetTrackData);
    }

    @Override
    public ActionProcessingResult handle(Blueprint blueprint, Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, PlacementSettings settings) {
        if (tileEntityData != null && tileEntityData.contains("TargetTrack")) {
            @SuppressWarnings("removal") final Rotation blueprintRotation = settings.getRotation();
            final var newData = fixTargetTrack(tileEntityData.getCompound("TargetTrack"), blueprintRotation);

            final BlockPos bottomLeftCorner = centerPos.subtract(blueprint.getPrimaryBlockOffset());
            final BlockPos blueprintPos = pos.subtract(bottomLeftCorner);
            final BlockPos trackPos = blueprintPos.offset(newData.getA());
            final BlockState trackState = blueprint.getBlockState(trackPos);

            if (trackState.hasProperty(TrackBlock.SHAPE)) {
                final TrackShape shape = trackState.getValue(TrackBlock.SHAPE);

                final Axis currentTrackAxis = shape == TrackShape.XO ? Axis.X : Axis.Z;
                final Axis originalTrackAxis = switch(blueprintRotation) {
                    case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> currentTrackAxis == Axis.X ? Axis.Z : Axis.X;
                    case CLOCKWISE_180, NONE -> currentTrackAxis;
                };

                // East:  TargetDirection 0b, originalTrackAxis X, encoded 00 (0 in binary)
                // South: TargetDirection 0b, originalTrackAxis Z, encoded 01 (1 in binary)
                // West:  TargetDirection 1b, originalTrackAxis X, encoded 10 (2 in binary)
                // North: TargetDirection 1b, originalTrackAxis Z, encoded 11 (3 in binary)
                // Note that rotating clockwise is the same as adding one to the encoded value!
                // The ordinals of Rotation happen to count the amount of time the schematic is rotated clockwise
                // We use that to find out what the new TargetDirection should be
                final byte encodedDirection = (byte)((tileEntityData.getByte("TargetDirection") != 0 ? 2 : 0) |
                        (originalTrackAxis == Axis.Z ? 1 : 0));
                final byte rotatedDirection = (byte)((encodedDirection + blueprintRotation.ordinal()) % 4);
                final byte newTargetDirection = (byte)((rotatedDirection & 2) >> 1);
                tileEntityData.putByte("TargetDirection", newTargetDirection);
            }

            tileEntityData.put("TargetTrack", newData.getB());
        }

        return super.handle(world, pos, blockState, tileEntityData, complete, centerPos, settings);
    }

    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, @SuppressWarnings("removal") PlacementSettings settings) {
        if (tileEntityData != null && tileEntityData.contains("TargetTrack")) {
            @SuppressWarnings("removal")
            final Rotation blueprintRotation = settings.getRotation();
            final var newData = fixTargetTrack(tileEntityData.getCompound("TargetTrack"), blueprintRotation);
            tileEntityData.put("TargetTrack", newData.getB());
        }
        return super.handle(world, pos, blockState, tileEntityData, complete, centerPos, settings);
    }
}
