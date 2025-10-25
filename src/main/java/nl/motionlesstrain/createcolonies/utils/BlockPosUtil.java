package nl.motionlesstrain.createcolonies.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.Rotation;

public class BlockPosUtil {
    public static BlockPos fromNBT(CompoundTag tag) {
        final int x = tag.getInt("X");
        final int y = tag.getInt("Y");
        final int z = tag.getInt("Z");
        return new BlockPos(x, y, z);
    }

    public static BlockPos fromNBT(ListTag tag) {
      if (tag.getElementType() != Tag.TAG_INT) {
        return BlockPos.ZERO;
      }
      final int x = tag.getInt(0);
      final int y = tag.getInt(1);
      final int z = tag.getInt(2);
      return new BlockPos(x, y, z);
    }

    public static CompoundTag toNBT(BlockPos pos) {
        final CompoundTag tag = new CompoundTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        return tag;
    }

    public static ListTag toNBTList(BlockPos pos) {
      final ListTag list = new ListTag();
      list.add(IntTag.valueOf(pos.getX()));
      list.add(IntTag.valueOf(pos.getY()));
      list.add(IntTag.valueOf(pos.getZ()));
      return list;
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
