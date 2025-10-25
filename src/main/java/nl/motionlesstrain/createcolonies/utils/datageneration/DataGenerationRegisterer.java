package nl.motionlesstrain.createcolonies.utils.datageneration;

import net.neoforged.bus.api.IEventBus;

public class DataGenerationRegisterer {
  public static void register(IEventBus bus) {
    bus.register(Models.class);
    bus.register(Recipes.class);
  }
}
