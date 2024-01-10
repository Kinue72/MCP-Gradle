package tech.mania;

import tech.mania.core.types.command.CommandManager;
import tech.mania.core.types.event.EventManager;
import tech.mania.core.types.module.ModuleManager;
import tech.mania.ui.click.ClickGui;

public class Mania {

    private static final Mania instance = new Mania();

    private Mania() {
    }

    private EventManager eventManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ClickGui clickGui;

    public static void init() {
        instance.eventManager = new EventManager();
        instance.moduleManager = new ModuleManager();
        instance.commandManager = new CommandManager();
        instance.clickGui = new ClickGui();
    }

    public static void shutdown() {

    }

    public static ClickGui getClickGui() {
        return instance.clickGui;
    }

    public static EventManager getEventManager() {
        return instance.eventManager;
    }

    public static ModuleManager getModuleManager() {
        return instance.moduleManager;
    }

    public static CommandManager getCommandManager() {
        return instance.commandManager;
    }
}
