package nl.motionlesstrain.createcolonies.placementhandlers;

import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

public class PlacementHandlers {
    private static void addHandler(IPlacementHandler handler) {
        com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.add(handler);
    }
    public static void initialiseHandlers(FMLLoadCompleteEvent ignored) {
        addHandler(new BeltPlacementHandler());
        addHandler(new ChainConveyorPlacementHandler());
        addHandler(new CopyCatPlacementHandler());
        addHandler(new DeployerPlacementHandler());
        addHandler(new EncasedShaftPlacementHandler());
        addHandler(new EncasedPipePlacementHandler());
        addHandler(new GearPlacementHandler());
        addHandler(new TrainBogeyPlacementHandler());
        addHandler(new TrainStationPlacementHandler());
        addHandler(new TrackPlacementHandler());
    }
}
