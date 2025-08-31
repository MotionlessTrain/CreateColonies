package nl.motionlesstrain.createcolonies;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.motionlesstrain.createcolonies.compatibility.Minecolonies;
import nl.motionlesstrain.createcolonies.hooks.HooksInitialiser;
import nl.motionlesstrain.createcolonies.placementhandlers.PlacementHandlers;
import nl.motionlesstrain.createcolonies.resources.CreateColoniesResources;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateColonies.MODID)
public class CreateColonies {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "createcolonies";

  public CreateColonies() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    registerRegistries(modEventBus);

    modEventBus.addListener(CreateColoniesResources.CreativeTab::fillCreativeTab);
    modEventBus.addListener(PlacementHandlers::initialiseHandlers);

    // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
    CommonConfig.registerConfig();
    modEventBus.addListener(CommonConfig::loadSettings);

    // Register the event listeners, e.g. for items outside our control
    HooksInitialiser.registerHooks();
    // And initialise the compatibility code, for non-required dependencies
    Minecolonies.initialiseCompat();
  }

  private void registerRegistries(IEventBus bus) {
      CreateColoniesResources.Blocks.REGISTRY.register(bus);
      CreateColoniesResources.Items.REGISTRY.register(bus);
      CreateColoniesResources.CreativeTab.REGISTRY.register(bus);
      CreateColoniesResources.BlockEntities.REGISTRY.register(bus);
    }
}
