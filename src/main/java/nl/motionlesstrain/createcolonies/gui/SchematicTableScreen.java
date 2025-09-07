package nl.motionlesstrain.createcolonies.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static nl.motionlesstrain.createcolonies.CreateColonies.MODID;

public class SchematicTableScreen extends AbstractContainerScreen<SchematicTableMenu> {

  private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(MODID,
      "textures/gui/container/schematic_table_screen.png");

  public SchematicTableScreen(SchematicTableMenu menu, Inventory playerInv, Component title) {
    super(menu, playerInv, title);
  }

  @Override
  protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    guiGraphics.blit(BACKGROUND_LOCATION, leftPos, topPos, 0, 0, imageWidth, imageHeight);
  }
}
