package tech.mania.core.types.module;

import org.lwjgl.input.Keyboard;
import tech.mania.MCHook;
import tech.mania.Mania;
import tech.mania.core.types.event.EventListener;
import tech.mania.core.types.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module implements MCHook, EventListener {

    private final String name, description;
    private final ModuleCategory category;
    private final List<Setting> settings;
    private boolean toggled;
    public int keyCode;

    protected Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.settings = new ArrayList<>();
        this.keyCode = Keyboard.KEY_NONE;
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public final void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            this.onEnable();
            Mania.getEventManager().register(this);
        } else {
            this.onDisable();
            Mania.getEventManager().unregister(this);
        }
    }

    public final boolean isEnabled() {
        return this.toggled;
    }

    public final ModuleCategory getCategory() {
        return this.category;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    public final List<Setting> getSettings() {
        return this.settings;
    }
}
