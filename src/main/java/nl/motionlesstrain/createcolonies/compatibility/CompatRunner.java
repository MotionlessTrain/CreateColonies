package nl.motionlesstrain.createcolonies.compatibility;

import java.util.function.Supplier;

public interface CompatRunner {
  void run(Supplier<Runnable> function);
  void runOrElse(Supplier<Runnable> ifInstalled, Supplier<Runnable> alternative);
}
