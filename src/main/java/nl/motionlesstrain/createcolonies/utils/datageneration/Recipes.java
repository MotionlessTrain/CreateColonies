package nl.motionlesstrain.createcolonies.utils.datageneration;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Blocks.schematicTable;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items.*;
import static nl.motionlesstrain.createcolonies.resources.StructurizeResources.Items.buildTool;

public class Recipes extends RecipeProvider {
  @SubscribeEvent
  public static void gatherRecipes(GatherDataEvent event) {
    final DataGenerator generator = event.getGenerator();
    final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
    generator.addProvider(event.includeServer(), (Factory<Recipes>)output -> new Recipes(output, lookupProvider));
  }

  public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registryAccess) {
    super(output, registryAccess);
  }

  @Override
  protected void buildRecipes(@NotNull RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, schematicTable.get())
      .pattern("PBP")
      .pattern("PCP")
      .pattern("PPP")
      .define('P', ItemTags.PLANKS)
      .define('B', buildTool)
      .define('C', andesiteCasing)
      .unlockedBy("has_items",
        InventoryChangeTrigger.TriggerInstance.hasItems(emptySchematic)
      ).save(output);
  }
}
