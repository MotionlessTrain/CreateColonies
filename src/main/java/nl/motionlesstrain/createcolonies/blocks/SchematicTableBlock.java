package nl.motionlesstrain.createcolonies.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import nl.motionlesstrain.createcolonies.blockentities.SchematicTableEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchematicTableBlock extends Block implements EntityBlock {
  public static final VoxelShape SHAPE = Shapes.or(
    Shapes.box(0, 0.75, 0, 1, 1, 1),
    Shapes.box(0, 0, 0, 0.125, 0.75, 0.125),
    Shapes.box(0.875, 0, 0, 1, 0.75, 0.125),
    Shapes.box(0, 0, 0.875, 0.125, 0.75, 1),
    Shapes.box(0.875, 0, 0.875, 1, 0.75, 1)
  );

  public SchematicTableBlock() {
    super(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).dynamicShape());
  }

  @Override
  protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return SHAPE;
  }

  @Override
  protected @Nullable MenuProvider getMenuProvider(@NotNull BlockState any, @NotNull Level world, @NotNull BlockPos pos) {
    final BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof SchematicTableEntity stEntity) {
      return stEntity.getMenuProvider();
    }
    return null;
  }

  @Override
  protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos,
                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
    if (!world.isClientSide()) {
      final BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof SchematicTableEntity stEntity) {
        stEntity.openMenuForPlayer(player);
      } else return InteractionResult.FAIL;
    }
    return InteractionResult.sidedSuccess(world.isClientSide());
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new SchematicTableEntity(blockPos, blockState);
  }
}
