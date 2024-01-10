package tech.mania.core.features.module.render;

import tech.mania.Mania;
import tech.mania.core.features.event.Render2DEvent;
import tech.mania.core.types.module.Module;
import tech.mania.core.types.module.ModuleCategory;
import tech.mania.ui.font.TTFFontRenderer;

import java.util.List;

public class ActiveModules extends Module {

    private final TTFFontRenderer font = TTFFontRenderer.of("Roboto-Light", 10);

    public ActiveModules() {
        super("ActiveModules", "Display enabled modules", ModuleCategory.Render);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        final List<Module> modules = Mania.getModuleManager().getModules();
        float currentY = 0;
        for (int i = 0; i < modules.size(); i++) {
            final Module m = modules.get(i);
            if (m.isEnabled()) continue;
        }
        super.onRender2D(event);
    }
}
