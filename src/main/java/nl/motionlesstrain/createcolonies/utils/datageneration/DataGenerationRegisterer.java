package nl.motionlesstrain.createcolonies.utils.datageneration;

import net.minecraftforge.eventbus.api.IEventBus;

public class DataGenerationRegisterer {
  public static void register(IEventBus bus) {
    bus.register(Models.class);
    bus.register(Recipes.class);
  }
}
