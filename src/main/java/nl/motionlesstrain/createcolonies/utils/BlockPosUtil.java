package nl.motionlesstrain.createcolonies.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.Rotation;

import java.util.ArrayList;
import java.util.List;

public class BlockPosUtil {
  public static BlockPos fromNBT(final CompoundTag parent, final String key) {
    final Tag blockTag = parent.get(key);
    if (blockTag == null) return BlockPos.ZERO;

    if (blockTag instanceof CompoundTag compound) {
      return fromNBT(compound);
    }
    return fromNBT(blockTag);
  }

  public static List<BlockPos> getList(final CompoundTag parent, final String key) {
    final Tag blockTag = parent.get(key);
    if (!(blockTag instanceof ListTag list)) return List.of();
    final List<BlockPos> decoded = new ArrayList<>();
    for (Tag posTag : list) {
      if (posTag instanceof CompoundTag compoundTag) {
        decoded.add(BlockPosUtil.fromNBT(compoundTag));
      } else {
        decoded.add(BlockPosUtil.fromNBT(posTag));
      }
    }
    return decoded;
  }

  private static BlockPos fromNBT(CompoundTag tag) {
    final int x = tag.getInt("X");
    final int y = tag.getInt("Y");
    final int z = tag.getInt("Z");
    return new BlockPos(x, y, z);
  }

  private static BlockPos fromNBT(Tag tag) {
    return BlockPos.CODEC.parse(NbtOps.INSTANCE, tag).mapOrElse(pos -> pos, err -> BlockPos.ZERO);
  }

  public static Tag toNBT(BlockPos pos) {
    return BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).getOrThrow();
  }

  public static ListTag toNBTList(BlockPos pos) {
    final ListTag result = new ListTag();
    result.add(IntTag.valueOf(pos.getX()));
    result.add(IntTag.valueOf(pos.getY()));
    result.add(IntTag.valueOf(pos.getZ()));
    return result;
  }

  public record DoubleBlockPos(double x, double y, double z) {
    public DoubleBlockPos(ListTag list) {
      this(list.getDouble(0), list.getDouble(1), list.getDouble(2));
    }

    public ListTag toNBT() {
      final ListTag list = new ListTag();
      list.add(DoubleTag.valueOf(x));
      list.add(DoubleTag.valueOf(y));
      list.add(DoubleTag.valueOf(z));
      return list;
    }

    public DoubleBlockPos rotate(Rotation rotation) {
      return switch (rotation) {
        case CLOCKWISE_90 -> new DoubleBlockPos(-z, y, x);
        case CLOCKWISE_180 -> new DoubleBlockPos(-x, y, -z);
        case COUNTERCLOCKWISE_90 -> new DoubleBlockPos(z, y, -x);
        default -> this;
      };
    }

    public DoubleBlockPos add(DoubleBlockPos rhs) {
      return new DoubleBlockPos(x + rhs.x, y + rhs.y, z + rhs.z);
    }
  }
}
