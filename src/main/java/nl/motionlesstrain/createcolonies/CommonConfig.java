package nl.motionlesstrain.createcolonies;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {
  private static final ModConfigSpec SPEC;

  private static final ModConfigSpec.BooleanValue DEBUG_LOG;
  public static boolean debugLog;

  static {
    final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    DEBUG_LOG = builder.comment(
        "Enable the debug log for items needed during the build (only recommended for development)"
    ).define("debugLog", false);

    SPEC = builder.build();
  }

  public static void registerConfig(ModContainer container) {
    container.registerConfig(ModConfig.Type.COMMON, SPEC);
  }
  public static void loadSettings(final ModConfigEvent event) {
    if (event.getConfig().getSpec() == SPEC) {
      debugLog = DEBUG_LOG.get();
    }
  }
}
