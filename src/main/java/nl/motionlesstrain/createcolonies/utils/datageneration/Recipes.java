package nl.motionlesstrain.createcolonies.utils.datageneration;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Blocks.schematicTable;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items.*;
import static nl.motionlesstrain.createcolonies.resources.StructurizeResources.Items.buildTool;

public class Recipes extends RecipeProvider {
  @SubscribeEvent
  public static void gatherRecipes(GatherDataEvent event) {
    final DataGenerator generator = event.getGenerator();
    generator.addProvider(event.includeServer(), (Factory<Recipes>)Recipes::new);
  }

  public Recipes(PackOutput output) {
    super(output);
  }

  @Override
  protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, schematicTable.get())
      .pattern("PBP")
      .pattern("PCP")
      .pattern("PPP")
      .define('P', ItemTags.PLANKS)
      .define('B', buildTool)
      .define('C', andesiteCasing)
      .unlockedBy("has_items",
        InventoryChangeTrigger.TriggerInstance.hasItems(emptySchematic)
      ).save(consumer);
  }
}
