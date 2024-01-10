package tech.mania.core.features.module.player;

import tech.mania.core.features.event.PreUpdateEvent;
import tech.mania.core.types.module.Module;
import tech.mania.core.types.module.ModuleCategory;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", "Makes you sprinting", ModuleCategory.Player);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        mc.gameSettings.keyBindSprint.setPressed(true);
        super.onPreUpdate(event);
    }
}
