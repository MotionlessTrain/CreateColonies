package nl.motionlesstrain.createcolonies.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class BlockPosUtil {
    public static BlockPos fromNBT(CompoundTag tag) {
        final int x = tag.getInt("X");
        final int y = tag.getInt("Y");
        final int z = tag.getInt("Z");
        return new BlockPos(x, y, z);
    }

    public static CompoundTag toNBT(BlockPos pos) {
        final CompoundTag tag = new CompoundTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        return tag;
    }
}
