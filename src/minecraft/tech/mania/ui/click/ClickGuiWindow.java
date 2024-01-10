package tech.mania.ui.click;

import org.lwjgl.input.Keyboard;
import tech.mania.MCHook;
import tech.mania.Mania;
import tech.mania.core.features.setting.*;
import tech.mania.core.types.module.Module;
import tech.mania.core.types.module.ModuleCategory;
import tech.mania.core.types.setting.Setting;
import tech.mania.ui.font.TTFFontRenderer;
import tech.mania.ui.util.ClickUtil;
import tech.mania.ui.util.Render2DUtil;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ClickGuiWindow implements MCHook {

    private DoubleSetting doubleSetting;
    private static final Color accentColor = new Color(0xff2C508A);
    private static final Color backColor = new Color(0xff373737);
    private static final Color outlineColor1 = new Color(0xff202020);
    private static final Color outlineColor2 = new Color(0xff313131);
    private static final int settingTextColor = 0xffd0d0d0;

    private static final TTFFontRenderer font = TTFFontRenderer.of("Roboto-Regular", 16);

    private float x, y, lastX, lastY;
    private boolean dragging = false, expand = true;

    private final ModuleCategory category;
    private final List<Module> modules;
    private final boolean[] mExpand;

    public ClickGuiWindow(float x, float y, ModuleCategory category) {
        this.x = x;
        this.y = y;
        this.category = category;
        modules = Mania.getModuleManager()
                .getModules()
                .stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
        mExpand = new boolean[modules.size()];
    }

    public void init() {
    }

    private void rectangle(Color c, float x, float y, float x2, float y2) {
        Render2DUtil.rectangle(x, y, x2 - x, y2 - y, c.getRGB());
    }

    public void render(int mouseX, int mouseY, float delta) {
        if (doubleSetting != null) {
            doubleSetting.setValue(x, 120, mouseX);
        }

        if (dragging) {
            x = mouseX + lastX;
            y = mouseY + lastY;
        }
        rectangle(accentColor, x - 2, y - 2, x + 122, y + 20);
        rectangle(outlineColor1, x - 1, y - 1, x + 121, y + 19);
        rectangle(new Color(0xff262626), x, y, x + 120, y + 18);
        rectangle(outlineColor1, x - 1, y + 17, x + 121, y + 18);
        font.drawStringWithShadow(category.name(), x + 4, y + 4+ 2, -1);

        if (!expand) {
            return;
        }

        float currentY = y + 18;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            rectangle(accentColor, x - 2, currentY, x + 122, currentY + 20);
            rectangle(outlineColor1, x - 1, currentY, x + 121, currentY + 19);
            rectangle(m.isEnabled() ? accentColor : backColor, x, currentY, x + 120, currentY + 18);
            if (m.keyCode == Keyboard.KEY_NONE) {
                font.drawStringWithShadow(m.getName(), x + 116 - font.width(m.getName()), currentY + 4+ 2, -1);
            } else {
                String displayKeyCode = String.format("%s [%s]", m.getName(), Keyboard.getKeyName(m.keyCode).toUpperCase());
                font.drawStringWithShadow(displayKeyCode, x + 116 - font.width(displayKeyCode), currentY + 4+ 2, -1);
            }

            currentY += 18;

            if (!mExpand[i]) {
                continue;
            }

            for (int j = 0; j < m.getSettings().size(); j++) {
                final Setting s = m.getSettings().get(j);
                rectangle(accentColor, x - 2, currentY, x + 122, currentY + 20);
                rectangle(outlineColor1, x - 1, currentY, x + 121, currentY + 19);
                if (s instanceof DoubleSetting) {
                    final DoubleSetting ds = (DoubleSetting) s;
                    final String v = String.valueOf(ds.getValue());
                    rectangle(outlineColor2, x, currentY, x + 120, currentY + 18);
                    rectangle(accentColor, x, currentY + 2, (float) (x + ds.getPercentage() * 120), currentY + 16);
                    font.drawStringWithShadow(s.getName(), x + 4, currentY + 4 + 2, settingTextColor);
                    font.drawStringWithShadow(v, x + 116 - font.width(v), currentY + 4+ 2, -1);
                } else if (s instanceof ModeSetting) {
                    final ModeSetting ms = (ModeSetting) s;
                    rectangle(outlineColor2, x, currentY, x + 120, currentY + 18);
                    font.drawStringWithShadow(s.getName(), x + 4, currentY + 4+ 2, settingTextColor);
                    font.drawStringWithShadow(ms.getValue(), x + 116 - font.width(ms.getValue()), currentY + 4+ 2, -1);
                    if (ms.expand) {
                        for (String o : ms.getOption()) {
                            currentY += 18;
                            rectangle(accentColor, x - 2, currentY, x + 122, currentY + 20);
                            rectangle(outlineColor1, x - 1, currentY, x + 121, currentY + 19);
                            rectangle(outlineColor2, x, currentY, x + 120, currentY + 18);
                            font.drawStringWithShadow(o, x + 4, currentY + 4+ 2,  ms.getValue().equals(o) ? -1 : settingTextColor);
                            //currentY += 18;
                        }
                    }
                } else if (s instanceof BooleanSetting) {
                    final BooleanSetting bs = (BooleanSetting) s;
                    rectangle(bs.getValue() ? accentColor : outlineColor2, x, currentY, x + 120, currentY + 18);
                    font.drawStringWithShadow(s.getName(), x + 4, currentY + 4+ 2, settingTextColor);
                }
                currentY += 18;
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (ClickUtil.isHovered(x, y, 140, 18, mouseX, mouseY)) {
            if (button == 0) {
                lastX = (float) (x - mouseX);
                lastY = (float) (y - mouseY);
                dragging = true;
            } else {
                expand = !expand;
            }
            return;
        }

        if (!expand) {
            return;
        }

        double currentY = y + 18;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            if (ClickUtil.isHovered2(x - 2, currentY, x + 122, currentY + 20, mouseX, mouseY)) {
                if (button == 0) {
                    m.toggle();
                } else {
                    mExpand[i] = !mExpand[i];
                }
                return;
            }
            currentY += 18;

            if (!mExpand[i]) {
                continue;
            }

            for (int j = 0; j < m.getSettings().size(); j++) {
                final Setting s = m.getSettings().get(j);
                if (s instanceof DoubleSetting) {
                    if (ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18, mouseX, mouseY)) {
                        doubleSetting = (DoubleSetting) s;
                        return;
                    }
                } else if (s instanceof ModeSetting) {
                    final ModeSetting ms = (ModeSetting) s;
                    if (ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18, mouseX, mouseY)) {
                        if (button == 0) {
                            ms.increment(true);
                        } else {
                            ms.expand = !ms.expand;
                        }
                        return;
                    }
                    if (ms.expand) {
                        for (String o : ms.getOption()) {
                            currentY += 18;
                            if (ClickUtil.isHovered2(x - 2, currentY, x + 122, currentY + 20, mouseX, mouseY)) {
                                ms.setValue(o);
                                return;
                            }
                        }
                    }
                } else if (s instanceof BooleanSetting) {
                    final BooleanSetting bs = (BooleanSetting) s;
                    if (ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18, mouseX, mouseY)) {
                        bs.switchValue();
                        return;
                    }
                }
                currentY += 18;
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        doubleSetting = null;
    }
}
