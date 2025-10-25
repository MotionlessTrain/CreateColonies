package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class Resources {
  protected static DeferredBlock<Block> createBlock(String namespace, String path) {
    return DeferredBlock.createBlock(ResourceLocation.fromNamespaceAndPath(namespace, path));
  }

  static DeferredItem<Item> createItem(String namespace, String path) {
    return DeferredItem.createItem(ResourceLocation.fromNamespaceAndPath(namespace, path));
  }
}
