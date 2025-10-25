package nl.motionlesstrain.createcolonies;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForgeConfigSpec;

public class CommonConfig {
  private static final NeoForgeConfigSpec SPEC;

  private static final NeoForgeConfigSpec.BooleanValue DEBUG_LOG;
  public static boolean debugLog;

  static {
    final NeoForgeConfigSpec.Builder builder = new NeoForgeConfigSpec.Builder();
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
    }
  }
}
