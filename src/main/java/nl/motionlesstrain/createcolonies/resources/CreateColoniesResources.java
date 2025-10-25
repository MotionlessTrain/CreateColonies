package nl.motionlesstrain.createcolonies.resources;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nl.motionlesstrain.createcolonies.blockentities.SchematicTableEntity;
import nl.motionlesstrain.createcolonies.blocks.SchematicTableBlock;
import nl.motionlesstrain.createcolonies.gui.SchematicTableMenu;

import java.util.function.Supplier;

import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;
import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;

public class CreateColoniesResources {
  public static class Blocks {
    public final static DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(MODID);
    public static final DeferredHolder<Block, Block> schematicTable = REGISTRY.register("schematic_table", SchematicTableBlock::new);
  }

  public static class Items {
    public final static DeferredRegister.Items REGISTRY = DeferredRegister.createItems(MODID);
    static {
      REGISTRY.registerSimpleBlockItem(Blocks.schematicTable);
    }
  }

  public static class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);

    public static final Supplier<BlockEntityType<SchematicTableEntity>> schematicTableEntity =
        REGISTRY.register("schematic_table", () -> BlockEntityType.Builder.of(SchematicTableEntity::new,
            Blocks.schematicTable.get()).build(null)
        );
  }

  public static class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(CREATIVE_MODE_TAB, MODID);

    public static final Supplier<CreativeModeTab> createColoniesTab = REGISTRY.register("tab", () ->
        CreativeModeTab.builder().title(Component.translatable("item_group." + MODID + ".tab")).icon(() ->
            new ItemStack(Blocks.schematicTable.get())
        ).build());

    public static void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
      if (event.getTab() == createColoniesTab.get()) {
        event.accept(Blocks.schematicTable.get());
      }
    }
  }

  public static class Menus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static final Supplier<MenuType<SchematicTableMenu>> schematicTableMenu =
        REGISTRY.register("schematic_table_menu", () ->
            new MenuType<>(SchematicTableMenu::new, FeatureFlags.DEFAULT_FLAGS));
  }
}
