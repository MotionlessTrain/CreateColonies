package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.util.PlacementSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import nl.motionlesstrain.createcolonies.resources.CreateResources.Items;
import nl.motionlesstrain.createcolonies.utils.BlockPosUtil;
import nl.motionlesstrain.createcolonies.utils.ItemUtils;
import org.jetbrains.annotations.Nullable;
import record;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.handleTileEntityPlacement;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Blocks.chainConveyor;

public class ChainConveyorPlacementHandler extends SimplePlacementHandler {
  @Override
  public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
    return blockState.is(chainConveyor);
  }

  @Override
  public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
    if (compoundTag != null) {
      final ListTag connections = compoundTag.getList("Connections", Tag.TAG_COMPOUND);
      final int neededChains = connections.stream().map(CompoundTag.class::cast).map(BlockPosUtil::fromNBT).filter(connectionPos ->
        connectionPos.getX() == 0 ? connectionPos.getZ() > 0 : connectionPos.getX() > 0
      ).mapToDouble(pos -> Math.sqrt(pos.distSqr(BlockPos.ZERO))).mapToInt(distance -> (int)(distance / 2)).sum();
      if (neededChains > 0) {
        return List.of(ItemUtils.stackFromNullable(Items.chainConveyor), new ItemStack(Blocks.CHAIN, neededChains));
      }
    }
    return List.of(ItemUtils.stackFromNullable(Items.chainConveyor));
  }

  private record ConveyorInfo(BlockPos pos, BlockPos newBlockPos, CompoundTag blockEntity)  {}
  private Map<BlockPos, Map<BlockPos, ConveyorInfo>> connections = new HashMap<>();

  @Override
  public ActionProcessingResult handle(Blueprint blueprint, Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, PlacementSettings settings) {
    if (tileEntityData != null) {
      final ListTag connections = tileEntityData.getList("Connections", Tag.TAG_COMPOUND);
      final ListTag newConnections = new ListTag();

      final Map<BlockPos, ConveyorInfo> existingConnections = this.connections.getOrDefault(pos, Map.of());

      for (int i = 0; i < connections.size(); i++) {
        final CompoundTag connection = connections.getCompound(i);
        final BlockPos blockPos = BlockPosUtil.fromNBT(connection);
        final BlockPos newBlockPos = blockPos.rotate(blueprint.getRotationMirror().rotation());

        if (existingConnections.containsKey(newBlockPos)) {
          final ConveyorInfo info = existingConnections.remove(newBlockPos);
          final ListTag infoConnections = info.blockEntity().getList("Connections", Tag.TAG_COMPOUND);
          infoConnections.add(BlockPosUtil.toNBT(info.newBlockPos()));
          handleTileEntityPlacement(info.blockEntity(), world, info.pos());

          newConnections.add(BlockPosUtil.toNBT(newBlockPos));
        } else {
          final Map<BlockPos, ConveyorInfo> newConnections2 = this.connections.computeIfAbsent(pos.offset(newBlockPos), ignored -> new HashMap<>());
          newConnections2.put(newBlockPos.multiply(-1), new ConveyorInfo(pos, newBlockPos, tileEntityData));
        }

      }
      tileEntityData.put("Connections", newConnections);
    }
    return super.handle(blueprint, world, pos, blockState, tileEntityData, complete, centerPos, settings);
  }
}
