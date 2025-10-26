package nl.motionlesstrain.createcolonies.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.items.IItemHandler;
import nl.motionlesstrain.createcolonies.blockentities.SchematicTableEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;

public class SchematicTableScreen extends AbstractContainerScreen<SchematicTableMenu> {

  private static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID,
      "textures/gui/container/schematic_table_screen.png");

  public SchematicTableScreen(SchematicTableMenu menu, Inventory playerInv, Component title) {
    super(menu, playerInv, title);
  }

  private boolean pointsRight = true;
  private static final Component[] BUTTON_LABELS = { Component.literal("<-"), Component.literal("->")};

  private Button toggleButton;
  private Button submitButton;

  @Override
  protected void init() {
    super.init();

    final int buttonX = leftPos + 78;
    final int toggleButtonY = topPos + 31;
    final int submitButtonY = toggleButtonY + 14;

    toggleButton = addRenderableWidget(Button.builder(BUTTON_LABELS[pointsRight ? 1 : 0], button -> {
      final var gameMode = getMinecraft().gameMode;
      if (gameMode != null) {
        gameMode.handleInventoryButtonClick(menu.containerId, 0);
      }
    }).pos(buttonX, toggleButtonY).size(20, 12).build());

    submitButton = Button.builder(Component.translatable("nl.motionlesstrain.createcolonies.gui.convert"), button -> {
      final var gameMode = getMinecraft().gameMode;
      if (gameMode != null) {
        gameMode.handleInventoryButtonClick(menu.containerId, 1);
      }
    }).pos(buttonX - 12, submitButtonY).size(44, 12).build();
  }

  @Override
  protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    guiGraphics.blit(BACKGROUND_LOCATION, leftPos, topPos, 0, 0, imageWidth, imageHeight);

    if (pointsRight) {
      guiGraphics.blit(BACKGROUND_LOCATION, leftPos + 48, topPos + 35, 176, 0, 18, 18);
    } else {
      guiGraphics.blit(BACKGROUND_LOCATION, leftPos + 110, topPos + 35, 176, 18, 18, 18);
    }
  }

  private boolean hasConvertButton = false;

  @Override
  public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(graphics, mouseX, mouseY, partialTick);

    final boolean pointsRight = menu.pointsToBlueprint();
    if (this.pointsRight != pointsRight) {
      this.pointsRight = pointsRight;
      toggleButton.setMessage(BUTTON_LABELS[pointsRight ? 1 : 0]);
    }

    final IItemHandler itemHandler = menu.getItemHandler();

    final boolean canConvert = SchematicTableEntity.canConvert(itemHandler, pointsRight);
    if (canConvert != hasConvertButton) {
      if (canConvert) {
        addRenderableWidget(submitButton);
      } else {
        removeWidget(submitButton);
      }
      hasConvertButton = canConvert;
    }

    super.render(graphics, mouseX, mouseY, partialTick);

    this.renderTooltip(graphics, mouseX, mouseY);

    final boolean createUseDefault = SchematicTableEntity.isFileNameDefaulted(itemHandler, 0, pointsRight);

    if (createUseDefault && pointsRight && isHovering(8, 20, 160, 10, mouseX, mouseY)) {
      graphics.renderTooltip(font, Component.translatable("nl.motionlesstrain.createcolonies.gui.noschematic.desc"), mouseX, mouseY);
    }

    final boolean structurizeUseDefault = SchematicTableEntity.isFileNameDefaulted(itemHandler, 1, pointsRight);

    if (structurizeUseDefault && isHovering(8, 62, 160, 10, mouseX, mouseY)) {
      if (pointsRight) {
        final List<Component> components = new ArrayList<>(3);
        for(int i = 1; i <= 3; i++) {
          components.add(Component.translatable("nl.motionlesstrain.createcolonies.gui.noscantool.desc." + i));
        }
        graphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
      } else {
        graphics.renderTooltip(font, Component.translatable("nl.motionlesstrain.createcolonies.gui.nopreview.desc"), mouseX, mouseY);
      }
    }
  }

  @Override
  protected void renderLabels(@NotNull GuiGraphics graphics, int x, int y) {
    super.renderLabels(graphics, x, y);

    final IItemHandler itemHandler = menu.getItemHandler();
    final @Nullable String createText = SchematicTableEntity.getFileName(itemHandler, pointsRight, 0);
    final @Nullable String structurizeText = SchematicTableEntity.getFileName(itemHandler, pointsRight, 1);
    final boolean createUseDefault = SchematicTableEntity.isFileNameDefaulted(itemHandler, 0, pointsRight);
    final boolean structurizeUseDefault = SchematicTableEntity.isFileNameDefaulted(itemHandler, 1, pointsRight);

    Component createFileName = null, structurizeFileName = null;

    if (createText == null && pointsRight) {
      createFileName = Component.translatable("nl.motionlesstrain.createcolonies.gui.noschematic.short");
    } else if (createText != null) {
      UnaryOperator<MutableComponent> style = createUseDefault ? comp -> comp.withStyle(ChatFormatting.ITALIC) : comp -> comp;
      createFileName = style.apply(Component.literal(createText));
    }

    if (structurizeText == null && !pointsRight) {
      structurizeFileName = Component.translatable("nl.motionlesstrain.createcolonies.gui.nopreview.short");
    } else if (structurizeText != null) {
      UnaryOperator<MutableComponent> style = structurizeUseDefault ? comp -> comp.withStyle(ChatFormatting.ITALIC) : comp -> comp;
      structurizeFileName = style.apply(Component.literal(structurizeText));
    }

    if (createFileName != null) {
      graphics.drawString(font, createFileName, 88 - font.width(createFileName) / 2, 20, 0x404040, false);
    }
    if (structurizeFileName != null) {
      graphics.drawString(font, structurizeFileName, 88 - font.width(structurizeFileName) / 2, 62, 0x404040, false);
    }
  }
}
