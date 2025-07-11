package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ObjectHolder;

public class CreateResources {

    public static class Blocks {
        @ObjectHolder(registryName = "minecraft:block", value = "create:small_bogey")
        public static Block smallBogey = null;

        @ObjectHolder(registryName = "minecraft:block", value = "create:large_bogey")
        public static Block largeBogey = null;

        @ObjectHolder(registryName = "minecraft:block", value = "create:track")
        public static Block track = null;

        @ObjectHolder(registryName = "minecraft:block", value = "create:fake_track")
        public static Block fakeTrack = null;

        @ObjectHolder(registryName = "minecraft:block", value = "create:belt")
        public static Block belt = null;

        @ObjectHolder(registryName = "minecraft:block", value = "create:andesite_encased_shaft")
        public static Block andesiteEncasedShaft;

        @ObjectHolder(registryName = "minecraft:block", value = "create:brass_encased_shaft")
        public static Block brassEncasedShaft;

        @ObjectHolder(registryName = "minecraft:block", value = "create:encased_fluid_pipe")
        public static Block encasedFluidPipe;

        @ObjectHolder(registryName = "minecraft:block", value = "create:copycat_step")
        public static Block copycatStep;

        @ObjectHolder(registryName = "minecraft:block", value = "create:copycat_panel")
        public static Block copycatPanel;

        @ObjectHolder(registryName = "minecraft:block", value = "create:track_station")
        public static Block trackStation;

        @ObjectHolder(registryName = "minecraft:block", value = "create:track_signal")
        public static Block trackSignal;

        @ObjectHolder(registryName = "minecraft:block", value = "create:track_observer")
        public static Block trackObserver;

        @ObjectHolder(registryName = "minecraft:block", value = "create:deployer")
        public static Block deployer;
    }

    public static class Items {
        @ObjectHolder(registryName = "minecraft:item", value = "create:railway_casing")
        public static Item trainCasing = null;

        @ObjectHolder(registryName = "minecraft:item", value = "create:track")
        public static Item track;

        @ObjectHolder(registryName = "minecraft:item", value = "create:belt_connector")
        public static Item belt = null;

        @ObjectHolder(registryName = "minecraft:item", value = "create:shaft")
        public static Item shaft;

        @ObjectHolder(registryName = "minecraft:item", value = "create:fluid_pipe")
        public static Item fluidPipe;

        @ObjectHolder(registryName = "minecraft:item", value = "create:metal_girder")
        public static Item metalGirder;

        @ObjectHolder(registryName = "minecraft:item", value = "create:deployer")
        public static Item deployer;
    }
}
