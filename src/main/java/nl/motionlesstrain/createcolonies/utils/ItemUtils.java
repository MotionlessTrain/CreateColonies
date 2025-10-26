package nl.motionlesstrain.createcolonies.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemUtils {
  public static ItemStack stackFromNullable(DeferredItem<?> item) {
    return stackFromNullable(item, 1);
  }

  public static ItemStack stackFromNullable(DeferredItem<?> item, int count) {
    if (count == 0 || !item.isBound()) return ItemStack.EMPTY;

    return item.toStack(count);
  }

  public static ItemStack stackFromNullable(DeferredBlock<?> block) {
    if (!block.isBound()) return ItemStack.EMPTY;
    return stackFromNullable(block, 1);
  }

  public static ItemStack stackFromNullable(DeferredBlock<?> block, int count) {
    if (!block.isBound()) return ItemStack.EMPTY;

    return block.toStack(count);
  }

  public static ItemStack stackFromNBT(Level level, CompoundTag nbt) {
    final HolderLookup.Provider provider = level.registryAccess();
    return ItemStack.parseOptional(provider, nbt);
  }

}
