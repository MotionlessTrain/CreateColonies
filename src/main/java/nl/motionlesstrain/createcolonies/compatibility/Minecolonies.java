package nl.motionlesstrain.createcolonies.compatibility;

import java.util.function.Supplier;
import net.neoforged.fml.ModList;

public class Minecolonies {
  private static class MinecoloniesNotInstalled implements CompatRunner {
    @Override
    public void run(Supplier<Runnable> ignored) {
      // Do nothing, as Minecolonies is not installed
    }

    @Override
    public void runOrElse(Supplier<Runnable> ifInstalled, Supplier<Runnable> alternative) {
      alternative.get().run();
    }
  }

  private static class MinecoloniesInstalled implements CompatRunner {
    @Override
    public void run(Supplier<Runnable> suppl) {
      final Runnable function = suppl.get();
      function.run();
    }

    @Override
    public void runOrElse(Supplier<Runnable> ifInstalled, Supplier<Runnable> alternative) {
      ifInstalled.get().run();
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
  public static void runIfInstalledOr(Supplier<Runnable> ifInstalled, Supplier<Runnable> alternative) {
    minecoloniesRunner.runOrElse(ifInstalled, alternative);
  }
}
