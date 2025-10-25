package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.ObjectHolder;

public class MinecoloniesResources {
  public static class Blocks {
    @ObjectHolder(registryName = "minecraft:block", value = "minecolonies:blockhutbuilder")
    public static Block blockHutBuilder = null;
  }
}
