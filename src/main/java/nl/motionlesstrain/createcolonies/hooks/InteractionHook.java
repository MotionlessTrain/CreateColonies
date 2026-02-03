package nl.motionlesstrain.createcolonies.hooks;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import nl.motionlesstrain.createcolonies.compatibility.Minecolonies;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static com.minecolonies.core.colony.buildings.modules.BuildingModules.BUILDING_RESOURCES;
import static nl.motionlesstrain.createcolonies.resources.CreateResources.Items.clipboard;
import static nl.motionlesstrain.createcolonies.resources.MinecoloniesResources.Blocks.blockHutBuilder;

public class InteractionHook {

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock evt) {
        final Player player = evt.getEntity();
        if (!player.isShiftKeyDown()) return;

        final Level world = evt.getLevel();
        final BlockPos blockPosClicked = evt.getHitVec().getBlockPos();
        final BlockState blockStateClicked = world.getBlockState(blockPosClicked);
        final ItemStack heldItem = evt.getItemStack();

        if (blockHutBuilder.isPresent() && blockStateClicked.is(blockHutBuilder.get()) && clipboard != null && heldItem.is(clipboard)) {
            evt.setCanceled(true);
            evt.setCancellationResult(InteractionResult.SUCCESS);
            
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

                        List<List<ClipboardEntry>> pages = new ArrayList<>();
                        List<ClipboardEntry> currentEntries = new ArrayList<>();
                        int i = 0;

                        for (final var neededItem : neededResources.values()) {
                            final MutableComponent text = Component.translatable(neededItem.item.getDescriptionId()).setStyle(
                                    Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(neededItem.item)))
                            ).append(
                                    Component.literal("\n x" + neededItem.count).withStyle(ChatFormatting.BLACK)
                            ).append(
                                    Component.literal(" | " + neededItem.count / 64 + "▤ +" + neededItem.count % 64).withStyle(ChatFormatting.GRAY)
                            );

                            ClipboardEntry entry = new ClipboardEntry(neededItem.count == 0, text);
                            entry.displayItem(neededItem.item, neededItem.count);
                            currentEntries.add(entry);

                            if (++i == 7) {
                                currentEntries.add(new ClipboardEntry(false, Component.literal(">>>").withStyle(ChatFormatting.DARK_GRAY)));
                                pages.add(new ArrayList<>(currentEntries));
                                currentEntries.clear();
                                i = 0;
                            }
                        }

                        if (!currentEntries.isEmpty()) {
                            pages.add(new ArrayList<>(currentEntries));
                        }

                        ClipboardContent content = new ClipboardContent(
                            ClipboardOverrides.ClipboardType.WRITTEN, 
                            pages, 
                            true, 
                            0, 
                            Optional.empty()
                        );
                        
                        heldItem.set(AllDataComponents.CLIPBOARD_CONTENT, content);

                        heldItem.set(DataComponents.CUSTOM_NAME,
                                Component.translatable("create.materialChecklist").setStyle(Style.EMPTY.withItalic(Boolean.FALSE)));

                        final Component message = Component.translatable("nl.motionlesstrain.createcolonies.clipboard.registered",
                                Component.translatable(building.getBuildingDisplayName())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
                        player.displayClientMessage(message, false);
                    }
                }
            });
        }
    }

    private record NeededItem(ItemStack item, int count, int totalCount) {}
}
