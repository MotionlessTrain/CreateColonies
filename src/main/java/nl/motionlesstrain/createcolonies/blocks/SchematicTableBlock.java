package nl.motionlesstrain.createcolonies.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import nl.motionlesstrain.createcolonies.blockentities.SchematicTableEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchematicTableBlock extends Block implements EntityBlock {
  public SchematicTableBlock() {
    super(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
  }

  @Override
  @SuppressWarnings("deprecation")
  public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos,
                                        @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
    if (!world.isClientSide()) {
      final BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof SchematicTableEntity schematicTableEntity) {
        if (world.hasNeighborSignal(pos)) {
          schematicTableEntity.setStructurizeTool(player.getItemInHand(hand));
        } else {
          schematicTableEntity.setCreateBlueprint(player.getItemInHand(hand));
        }

        System.out.println("Test! " + schematicTableEntity.getCreateBlueprint() + ", " + schematicTableEntity.getStructurizeTool());
      } else {
        System.out.println("Wrong block entity?" + blockEntity);
      }
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    return new SchematicTableEntity(blockPos, blockState);
  }
}
