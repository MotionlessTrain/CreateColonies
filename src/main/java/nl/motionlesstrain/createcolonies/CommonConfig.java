package nl.motionlesstrain.createcolonies;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class CommonConfig {
  private static final ForgeConfigSpec SPEC;

  private static final ForgeConfigSpec.BooleanValue DEBUG_LOG;
  public static boolean debugLog;

  static {
    final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    DEBUG_LOG = builder.comment(
        "Enable the debug log for items needed during the build (only recommended for development)"
    ).define("debugLog", false);

    SPEC = builder.build();
  }

  public static void registerConfig() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
  }
  public static void loadSettings(final ModConfigEvent event) {
    if (event.getConfig().getSpec() == SPEC) {
      debugLog = DEBUG_LOG.get();
      System.out.println("debugLog = " + debugLog);
    }
  }
}
