package nl.motionlesstrain.createcolonies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.motionlesstrain.createcolonies.compatibility.Minecolonies;
import nl.motionlesstrain.createcolonies.hooks.HooksInitialiser;
import nl.motionlesstrain.createcolonies.placementhandlers.PlacementHandlers;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateColonies.MODID)
public class CreateColonies {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "createcolonies";

    public CreateColonies() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(PlacementHandlers::initialiseHandlers);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us

        // Register the event listeners, e.g. for items outside our control
        HooksInitialiser.registerHooks();
        // And initialise the compatibility code, for non-required dependencies
        Minecolonies.initialiseCompat();
    }
}
