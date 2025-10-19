package nl.motionlesstrain.createcolonies.utils.datageneration;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;
import static nl.motionlesstrain.createcolonies.resources.CreateColoniesResources.Blocks.schematicTable;

public class Models extends BlockStateProvider {
  @SubscribeEvent
  public static void gatherModelData(GatherDataEvent event) {
    final DataGenerator generator = event.getGenerator();
    final ExistingFileHelper efh = event.getExistingFileHelper();

    generator.addProvider(event.includeClient(), (Factory<Models>) output -> new Models(output, efh));
  }

  private Models(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, MODID, existingFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlockWithItem(schematicTable.get(), models().getExistingFile(schematicTable.getId()));
  }
}
