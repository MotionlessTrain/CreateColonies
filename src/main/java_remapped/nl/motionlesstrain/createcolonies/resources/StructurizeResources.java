package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.ObjectHolder;

public class StructurizeResources {
  public static class Items {
    @ObjectHolder(registryName = "minecraft:item", value = "structurize:sceptergold")
    public static Item buildTool;

    @ObjectHolder(registryName = "minecraft:item", value = "structurize:sceptersteel")
    public static Item scanTool;
  }
}
