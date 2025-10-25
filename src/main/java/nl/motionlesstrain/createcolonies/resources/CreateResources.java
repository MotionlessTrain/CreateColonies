package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class CreateResources extends Resources {
  private static DeferredBlock<Block> createBlock(String path) {
    return createBlock("create", path);
  }

  private static DeferredItem<Item> createItem(String path) {
    return createItem("create", path);
  }

  public static class Blocks {
    public static DeferredBlock<Block> smallBogey = createBlock("small_bogey");
    public static DeferredBlock<Block> largeBogey = createBlock("large_bogey");

    public static DeferredBlock<Block> track = createBlock("track");

    public static DeferredBlock<Block> belt = createBlock("belt");

    public static DeferredBlock<Block> andesiteEncasedShaft = createBlock("andesite_encased_shaft");
    public static DeferredBlock<Block> brassEncasedShaft = createBlock("brass_encased_shaft");
    public static DeferredBlock<Block> encasedFluidPipe = createBlock("encased_fluid_pipe");

    public static DeferredBlock<Block> copycatStep = createBlock("copycat_step");
    public static DeferredBlock<Block> copycatPanel = createBlock("copycat_panel");

    public static DeferredBlock<Block> trackStation = createBlock("track_station");
    public static DeferredBlock<Block> trackSignal = createBlock("track_signal");
    public static DeferredBlock<Block> trackObserver = createBlock("track_observer");

    public static DeferredBlock<Block> deployer = createBlock("deployer");

    public static DeferredBlock<Block> andesiteEncasedCogwheel = createBlock("andesite_encased_cogwheel");
    public static DeferredBlock<Block> brassEncasedCogwheel = createBlock("brass_encased_cogwheel");
    public static DeferredBlock<Block> andesiteEncasedLargeCogwheel = createBlock("andesite_encased_large_cogwheel");
    public static DeferredBlock<Block> brassEncasedLargeCogwheel = createBlock("brass_encased_large_cogwheel");

    public static DeferredBlock<Block> chainConveyor = createBlock("chain_conveyor");
  }

  public static class Items {
    public static DeferredItem<Item> trainCasing = createItem("railway_casing");

    public static DeferredItem<Item> shaft = createItem("shaft");

    public static DeferredItem<Item> metalGirder = createItem("metal_girder");

    public static DeferredItem<Item> clipboard = createItem("clipboard");

    public static DeferredItem<Item> cogwheel = createItem("cogwheel");
    public static DeferredItem<Item> largeCogwheel = createItem("large_cogwheel");

    public static DeferredItem<Item> emptySchematic = createItem("empty_schematic");
    public static DeferredItem<Item> schematic = createItem("schematic");

    public static DeferredItem<Item> andesiteCasing = createItem("andesite_casing");
  }
}
