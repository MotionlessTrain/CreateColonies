package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.util.PlacementSettings;
import com.ldtteam.structurize.util.RotationMirror;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources;
import nl.motionlesstrain.createcolonies.utils.BlockPosUtil;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static nl.motionlesstrain.createcolonies.utils.BlockPosUtil.DoubleBlockPos;

public class TrackPlacementHandler extends SimplePlacementHandler {

  @Override
  public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
    return blockState.is(CreateResources.Blocks.track);
  }

  @Override
  public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {

    final List<ItemStack> neededItems = new ArrayList<>();
    neededItems.add(ItemUtils.stackFromNullable(CreateResources.Items.track));

    if (blockState.is(CreateResources.Blocks.track) &&
        compoundTag != null && compoundTag.contains("Connections")) {
      final ListTag connections = compoundTag.getList("Connections", Tag.TAG_COMPOUND);
      for (int i = 0; i < connections.size(); i++) {
        final var connection = connections.getCompound(i);
        if (connection.getByte("Primary") != 0 &&
            connection.contains("Positions", Tag.TAG_LIST)) {
          final ListTag positionInfo = connection.getList("Positions", Tag.TAG_COMPOUND);
          BlockPos[] positions = new BlockPos[positionInfo.size()];
          for (int j = 0; j < positionInfo.size(); j++) {
            final var positionObj = positionInfo.getCompound(j);
            BlockPos position = BlockPosUtil.fromNBT(positionObj);
            positions[j] = position;
          }
          if (positions.length == 2) {
            final int deltaX = Math.abs(positions[0].getX() - positions[1].getX());
            final int deltaZ = Math.abs(positions[0].getZ() - positions[1].getZ());
            final int trackAmount = (deltaX == 0 || deltaZ == 0) ? deltaX + deltaZ : deltaX * 3/2;
            neededItems.add(ItemUtils.stackFromNullable(CreateResources.Items.track, trackAmount));

            if (connection.getByte("Girder") != 0) {
              neededItems.add(ItemUtils.stackFromNullable(CreateResources.Items.metalGirder, trackAmount * 2));
            }
          }
        }
      }
    }
    return neededItems;
  }

  private final String[] VECTOR_TYPES = {"Starts", "Normals", "Axes"};
  private final DoubleBlockPos[] STARTS_OFFSETS = {
      new DoubleBlockPos(0, 0, 0),
      new DoubleBlockPos(1, 0, 0),
      new DoubleBlockPos(1, 0, 1),
      new DoubleBlockPos(0, 0, 1),
      new DoubleBlockPos(1, 0, 0),
      new DoubleBlockPos(1, 0, 1),
      new DoubleBlockPos(0, 0, 1),
      new DoubleBlockPos(0, 0, 0),
  };

  @Override
  public ActionProcessingResult handle(Blueprint blueprint, Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, PlacementSettings settings) {
    final RotationMirror blueprintRotation = blueprint.getRotationMirror();

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
                DoubleBlockPos newBlockPos = blockPos;
                if (blueprintRotation.isMirrored())
                  newBlockPos = new DoubleBlockPos(-blockPos.x(), blockPos.y(), blockPos.z());
                newBlockPos = newBlockPos.rotate(blueprintRotation.rotation());
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
            BlockPos position = BlockPosUtil.fromNBT(positionObj);
            if (blueprintRotation.isMirrored())
              position = new BlockPos(-position.getX(), position.getY(), position.getZ());
            final BlockPos rotated = position.rotate(blueprintRotation.rotation());
            final var newPositionObj = BlockPosUtil.toNBT(rotated);
            positionInfo.set(j, newPositionObj);
          }
        }
      }
    }

    return super.handle(world, pos, blockState, tileEntityData, complete, centerPos, settings);
  }
}
