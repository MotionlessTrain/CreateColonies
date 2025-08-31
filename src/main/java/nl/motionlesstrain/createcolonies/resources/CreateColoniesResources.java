package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.motionlesstrain.createcolonies.blockentities.SchematicTableEntity;
import nl.motionlesstrain.createcolonies.blocks.SchematicTableBlock;

import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;
import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;

public class CreateColoniesResources {
  public static class Blocks {
    public final static DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> schematicTable = REGISTRY.register("schematic_table", SchematicTableBlock::new);
  }

  public static class Items {
    public final static DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    static {
      REGISTRY.register("schematic_table", () -> new BlockItem(Blocks.schematicTable.get(), new Item.Properties()));
    }
  }

  public static class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<SchematicTableEntity>> schematicTableEntity =
        REGISTRY.register("schematic_table", () -> BlockEntityType.Builder.of(SchematicTableEntity::new,
            Blocks.schematicTable.get()).build(null)
        );
  }

  public static class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> createColoniesTab = REGISTRY.register("tab", () ->
        CreativeModeTab.builder().title(Component.translatable("item_group." + MODID + ".tab")).icon(() ->
            new ItemStack(Blocks.schematicTable.get())
        ).build());

    public static void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
      if (event.getTab() == createColoniesTab.get()) {
        event.accept(Blocks.schematicTable.get());
      }
    }
  }
}
