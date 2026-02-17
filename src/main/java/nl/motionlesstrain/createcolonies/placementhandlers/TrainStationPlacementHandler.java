package nl.motionlesstrain.createcolonies.placementhandlers;


import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.BlockPosUtil;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrainStationPlacementHandler extends PlacementHandlers.GeneralBlockPlacementHandler {
  @Override
  public boolean canHandle(Level world, BlockPos pos, BlockState blockState) {
    return blockState.is(CreateResources.Blocks.trackStation) || blockState.is(CreateResources.Blocks.trackSignal)
        || blockState.is(CreateResources.Blocks.trackObserver);
  }

  @Override
  public List<ItemStack> getRequiredItems(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, IPlacementContext placementContext) {
    return blockState.is(CreateResources.Blocks.trackStation) ? List.of(ItemUtils.stackFromDeferred(CreateResources.Blocks.trackStation)) :
        blockState.is(CreateResources.Blocks.trackSignal) ? List.of(ItemUtils.stackFromDeferred(CreateResources.Blocks.trackSignal)) :
        blockState.is(CreateResources.Blocks.trackObserver) ? List.of(ItemUtils.stackFromDeferred(CreateResources.Blocks.trackObserver)) : List.of();
  }

  @Override
  public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, IPlacementContext placementContext) {
    final RotationMirror rotationMirror = placementContext.getRotationMirror();
    final Blueprint blueprint = placementContext.getBluePrint();
    if (tileEntityData != null && tileEntityData.contains("TargetTrack")) {
      BlockPos targetTrackPos = BlockPosUtil.fromNBT(tileEntityData, "TargetTrack");

      final BlockPos newTargetTrack = rotationMirror.applyToPos(targetTrackPos);
      tileEntityData.put("TargetTrack", BlockPosUtil.toNBT(newTargetTrack));

      final BlockPos bottomLeftCorner = placementContext.getCenterPos().subtract(blueprint.getPrimaryBlockOffset());
      final BlockPos blueprintPos = pos.subtract(bottomLeftCorner);
      final BlockPos trackPos = blueprintPos.offset(newTargetTrack);
      final BlockState trackState = blueprint.getBlockState(trackPos);

      if (trackState.hasProperty(TrackBlock.SHAPE)) {
        final TrackShape shape = trackState.getValue(TrackBlock.SHAPE);

        final Axis currentTrackAxis = shape == TrackShape.XO ? Axis.X : Axis.Z;
        final Axis originalTrackAxis = switch (rotationMirror.rotation()) {
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
        final byte encodedDirection = (byte) ((tileEntityData.getByte("TargetDirection") != 0 ? 2 : 0) |
            (originalTrackAxis == Axis.Z ? 1 : 0));
        final byte mirroredDirection = rotationMirror.isMirrored() ? (byte) ((2 - encodedDirection) % 4) : encodedDirection;
        final byte rotatedDirection = (byte) ((mirroredDirection + rotationMirror.rotation().ordinal()) % 4);
        final byte newTargetDirection = (byte) ((rotatedDirection & 2) >> 1);
        tileEntityData.putByte("TargetDirection", newTargetDirection);
      }

    }

    return super.handle(world, pos, blockState, tileEntityData, placementContext);
  }
}
