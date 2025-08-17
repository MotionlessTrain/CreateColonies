package nl.motionlesstrain.createcolonies.hooks;

import net.minecraftforge.common.MinecraftForge;

public class HooksInitialiser {
  public static void registerHooks() {
    MinecraftForge.EVENT_BUS.register(InteractionHook.class);
  }
}
