package nl.motionlesstrain.createcolonies.hooks;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import nl.motionlesstrain.createcolonies.compatibility.Minecolonies;
import org.slf4j.Logger;

import java.util.TreeMap;

import static com.minecolonies.core.colony.buildings.modules.BuildingModules.BUILDING_RESOURCES;
import static nl.motionlesstrain.createcolonies.resources.MinecoloniesResources.Blocks.blockHutBuilder;

public class InteractionHook {
  private static final Logger LOGGER = LogUtils.getLogger();

  @SubscribeEvent
  public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock evt) {
    final Player player = evt.getEntity();
    if (!player.isShiftKeyDown()) return;

    final Level world = evt.getLevel();
    final BlockPos blockPosClicked = evt.getHitVec().getBlockPos();
    final BlockState blockStateClicked = world.getBlockState(blockPosClicked);

    if (blockHutBuilder != null && blockStateClicked.is(blockHutBuilder)) {
      evt.setCanceled(true);
      evt.setResult(Event.Result.DENY);
      if (evt.getSide().isClient()) return;

      if (player instanceof FakePlayer) return;

      Minecolonies.runIfInstalled(() -> () -> {
        final BlockEntity blockEntity = world.getBlockEntity(blockPosClicked);
        if (blockEntity instanceof AbstractTileEntityColonyBuilding buildingEntity) {
          final IBuilding building = buildingEntity.getBuilding();
          final var resourcesModule = building.getModule(BUILDING_RESOURCES);
          if (resourcesModule != null) {
            final var allResources = resourcesModule.getNeededResources();
            final TreeMap<String, NeededItem> neededResources = new TreeMap<>();
            allResources.values().forEach(resource -> {
              final int entireAmount = resource.getAmount(),
                  availableAmount = resource.getAvailable(),
                  deliveringAmount = resource.getAmountInDelivery(),
                  neededAmount = entireAmount - availableAmount - deliveringAmount;
              final ItemStack itemStack = resource.getItemStack();
              if (neededAmount > 0) {
                neededResources.put(itemStack.getItem().getName(itemStack).getString(),
                    new NeededItem(itemStack, neededAmount, entireAmount));
              }
            });
            neededResources.forEach((name, neededItem) -> {
              System.out.println(name + ": " + neededItem.count() + "(" + neededItem.totalCount() + ")");
            });
            // TODO: fill clipboard with this information
          }
        }
      });
    }
  }

  private record NeededItem(ItemStack item, int count, int totalCount) {}
}
