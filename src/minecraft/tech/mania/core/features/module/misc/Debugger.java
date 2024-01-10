package tech.mania.core.features.module.misc;

import tech.mania.core.features.event.PreRender3DEvent;
import tech.mania.core.types.module.Module;
import tech.mania.core.types.module.ModuleCategory;

public class Debugger extends Module {

    protected Debugger() {
        super("Debugger", "For debug", ModuleCategory.Misc);
    }

    @Override
    public void onPreRender3D(PreRender3DEvent event) {
        super.onPreRender3D(event);
    }
}
