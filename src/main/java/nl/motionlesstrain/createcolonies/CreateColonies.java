package nl.motionlesstrain.createcolonies;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import nl.motionlesstrain.createcolonies.compatibility.Minecolonies;
import nl.motionlesstrain.createcolonies.gui.SchematicTableScreen;
import nl.motionlesstrain.createcolonies.hooks.HooksInitialiser;
import nl.motionlesstrain.createcolonies.network.MessagesHandler;
import nl.motionlesstrain.createcolonies.placementhandlers.PlacementHandlers;
import nl.motionlesstrain.createcolonies.resources.CreateColoniesResources;
import nl.motionlesstrain.createcolonies.utils.datageneration.DataGenerationRegisterer;

import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Menus.schematicTableMenu;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateColonies.MODID)
public class CreateColonies {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "createcolonies";

  // The mod container of CreateColonies
  public static ModContainer container;

  public CreateColonies(final IEventBus modEventBus, final ModContainer container) {
    CreateColonies.container = container;
    // Register the DeferredRegisters that we use to register our own resources in
    registerRegistries(modEventBus);

    // Register the capability providers that are used by the mod
    modEventBus.addListener(this::registerCapabilities);

    // Register the other common event handlers
    modEventBus.addListener(CreateColoniesResources.CreativeTab::fillCreativeTab);
    modEventBus.addListener(PlacementHandlers::initialiseHandlers);

    // Register our config
    CommonConfig.registerConfig(container);
    modEventBus.addListener(CommonConfig::loadSettings);

    // Network message registration
    modEventBus.addListener(MessagesHandler::setUpNetwork);

    // Data generators
    DataGenerationRegisterer.register(modEventBus);

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

  private void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
      CreateColoniesResources.BlockEntities.schematicTableEntity.get(),
      (schematicTableEntity, side) -> schematicTableEntity.new ItemHandler()
    );
  }

  @Mod(value=MODID, dist=Dist.CLIENT)
  public static class ClientSide {
    public ClientSide(final IEventBus modEventBus) {
      modEventBus.addListener(this::registerScreen);
    }

    private void registerScreen(RegisterMenuScreensEvent event) {
      event.register(schematicTableMenu.get(), SchematicTableScreen::new);
    }
  }
}
