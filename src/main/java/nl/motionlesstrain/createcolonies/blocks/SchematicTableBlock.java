package nl.motionlesstrain.createcolonies.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import nl.motionlesstrain.createcolonies.blockentities.SchematicTableEntity;
import nl.motionlesstrain.createcolonies.gui.SchematicTableMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchematicTableBlock extends Block implements EntityBlock {
  public SchematicTableBlock() {
    super(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
  }

  @Override
  @SuppressWarnings("deprecation")
  public @Nullable MenuProvider getMenuProvider(@NotNull BlockState any, @NotNull Level world, @NotNull BlockPos pos) {
    final BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof SchematicTableEntity stEntity) {
      final LazyOptional<IItemHandler> inventory = stEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
      if (inventory.isPresent()) {
        return new SimpleMenuProvider(
            (containerId, playerInventory, ignored) ->
                new SchematicTableMenu(containerId, playerInventory, ContainerLevelAccess.create(world, pos),
                    inventory.orElseThrow(() -> new IllegalStateException("The schematic table lost its inventory somehow"))),
            Component.translatable("menu.title.createcolonies.schematic_table_menu")
        );
      }
    }
    return null;
  }

  @Override
  @SuppressWarnings("deprecation")
  public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos,
                                        @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
    if (!world.isClientSide()) {
      player.openMenu(state.getMenuProvider(world, pos));
    }
    return InteractionResult.sidedSuccess(world.isClientSide());
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new SchematicTableEntity(blockPos, blockState);
  }
}
