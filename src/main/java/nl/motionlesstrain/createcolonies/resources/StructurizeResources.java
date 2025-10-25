package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class StructurizeResources extends Resources {
  private static DeferredItem<Item> createItem(String path) {
    return createItem("structurize", path);
  }

  public static class Items {
    public static DeferredItem<Item> buildTool = createItem("sceptergold");

    public static DeferredItem<Item> scanTool = createItem("sceptersteel");
  }
}
