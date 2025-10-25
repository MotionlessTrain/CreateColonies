package nl.motionlesstrain.createcolonies.hooks;

import net.neoforged.neoforge.common.NeoForge;

public class HooksInitialiser {
  public static void registerHooks() {
    NeoForge.EVENT_BUS.register(InteractionHook.class);
  }
}
