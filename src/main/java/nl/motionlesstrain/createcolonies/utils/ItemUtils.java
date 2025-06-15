package nl.motionlesstrain.createcolonies.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemUtils {
    public static ItemStack stackFromNullable(@Nullable Item item) {
        return stackFromNullable(item, 1, null);
    }
    public static ItemStack stackFromNullable(@Nullable Item item, int count) {
        return stackFromNullable(item, count, null);
    }
    public static ItemStack stackFromNullable(@Nullable Item item, int count, CompoundTag tag) {
        if (item == null || count == 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, count, tag);
    }

}
