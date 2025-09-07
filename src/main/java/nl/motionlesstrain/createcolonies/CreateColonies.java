package nl.motionlesstrain.createcolonies;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.motionlesstrain.createcolonies.compatibility.Minecolonies;
import nl.motionlesstrain.createcolonies.gui.SchematicTableScreen;
import nl.motionlesstrain.createcolonies.hooks.HooksInitialiser;
import nl.motionlesstrain.createcolonies.placementhandlers.PlacementHandlers;
import nl.motionlesstrain.createcolonies.resources.CreateColoniesResources;

import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Menus.schematicTableMenu;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateColonies.MODID)
public class CreateColonies {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "createcolonies";

  public CreateColonies() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    // Register the DeferredRegisters that we use to register our own resources in
    registerRegistries(modEventBus);

    // Register the other common event handlers
    modEventBus.addListener(CreateColoniesResources.CreativeTab::fillCreativeTab);
    modEventBus.addListener(PlacementHandlers::initialiseHandlers);

    // Physical client only registries
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSide::clientSetup));

    // Register our config
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
    CreateColoniesResources.Menus.REGISTRY.register(bus);
  }

  static class ClientSide {
    private static void clientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(() ->
          MenuScreens.register(schematicTableMenu.get(), SchematicTableScreen::new));
    }
  }
}
