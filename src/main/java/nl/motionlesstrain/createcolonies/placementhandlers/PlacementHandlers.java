package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import nl.motionlesstrain.createcolonies.compatibility.Minecolonies;

public class PlacementHandlers {
    private static void addHandler(IPlacementHandler handler) {
        com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.add(handler);
    }
    public static void initialiseHandlers(FMLLoadCompleteEvent event) {
      addHandler(new BeltPlacementHandler());
      addHandler(new ChainConveyorPlacementHandler());
      addHandler(new CopyCatPlacementHandler());
      addHandler(new DeployerPlacementHandler());
      addHandler(new ElevatorPlacementHandler());
      addHandler(new EncasedShaftPlacementHandler());
      addHandler(new EncasedPipePlacementHandler());
      addHandler(new GearPlacementHandler());
      addHandler(new GirderEncasedShaftPlacementHandler());
      addHandler(new TrainBogeyPlacementHandler());
      addHandler(new TrainStationPlacementHandler());
      addHandler(new TrackPlacementHandler());

      // Minecolonies also has a handler for lecterns, which also triggers on Create's lectern
      // This ensures that ours gets added later (and thus has higher priority) than minecolonies',
      // as the enqueued work runs after the event handlers are done
      Minecolonies.runIfInstalledOr(() -> () -> {
        event.enqueueWork(() -> addHandler(new LinkedLecternPlacementHandler()));
      }, () -> () -> addHandler(new LinkedLecternPlacementHandler()));
    }
}
