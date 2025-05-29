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

        @ObjectHolder(registryName = "minecraft:block", value = "create:fake_track")
        public static Block fakeTrack = null;

        @ObjectHolder(registryName = "minecraft:block", value = "create:belt")
        public static Block belt = null;
    }

    public static class Items {
        @ObjectHolder(registryName = "minecraft:item", value = "create:railway_casing")
        public static Item trainCasing = null;

        @ObjectHolder(registryName = "minecraft:item", value = "create:track")
        public static Item track;

        @ObjectHolder(registryName = "minecraft:item", value = "create:belt_connector")
        public static Item belt = null;

        @ObjectHolder(registryName = "minecraft:item", value = "create:andesite_casing")
        public static Item andesiteCasing = null;

        @ObjectHolder(registryName = "minecraft:item", value = "create:shaft")
        public static Item shaft;
    }
}
