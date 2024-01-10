package tech.mania.ui.click;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import tech.mania.core.types.module.ModuleCategory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {

    private final List<ClickGuiWindow> windows = new ArrayList<>();

    public ClickGui() {
        float currentX = 50;
        for (ModuleCategory c : ModuleCategory.values()) {
            windows.add(new ClickGuiWindow(currentX, 30, c));
            currentX += 150;
        }
    }

    @Override
    public void initGui() {
        windows.forEach(ClickGuiWindow::init);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        windows.forEach(m -> m.render(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(m -> m.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        windows.forEach(m -> m.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }
}
