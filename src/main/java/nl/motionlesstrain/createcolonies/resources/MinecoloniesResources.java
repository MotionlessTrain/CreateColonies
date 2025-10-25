package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MinecoloniesResources extends Resources {
  private static DeferredBlock<Block> createBlock(String path) {
    return createBlock("minecolonies", path);
  }
  public static class Blocks {
    public static DeferredBlock<Block> blockHutBuilder = createBlock("blockhutbuilder");
  }
}
