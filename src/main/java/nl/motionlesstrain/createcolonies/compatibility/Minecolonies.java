package nl.motionlesstrain.createcolonies.compatibility;

import java.util.function.Supplier;
import net.neoforged.fml.ModList;

public class Minecolonies {
  private static class MinecoloniesNotInstalled implements CompatRunner {
    @Override
    public void run(Supplier<Runnable> ignored) {
      // Do nothing, as Minecolonies is not installed
    }
  }

  private static class MinecoloniesInstalled implements CompatRunner {
    @Override
    public void run(Supplier<Runnable> suppl) {
      final Runnable function = suppl.get();
      function.run();
    }
  }

  private static CompatRunner minecoloniesRunner;
  public static void initialiseCompat() {
    if (ModList.get().isLoaded("minecolonies")) {
      minecoloniesRunner = new MinecoloniesInstalled();
    } else {
      minecoloniesRunner = new MinecoloniesNotInstalled();
    }
  }

  public static void runIfInstalled(Supplier<Runnable> code) {
    minecoloniesRunner.run(code);
  }
}
