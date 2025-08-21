package nl.motionlesstrain.createcolonies.compatibility;

import java.util.function.Supplier;

@FunctionalInterface
public interface CompatRunner {
  void run(Supplier<Runnable> function);
}
