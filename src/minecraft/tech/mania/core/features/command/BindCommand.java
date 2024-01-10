package tech.mania.core.features.command;

import org.lwjgl.input.Keyboard;
import tech.mania.MCHook;
import tech.mania.Mania;
import tech.mania.core.types.command.Command;

public class BindCommand extends Command implements MCHook {

    public BindCommand() {
        super("Bind", ".bind <module name> <key name>");
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length != 2) {
            return true;
        }

        if (Mania.getModuleManager().getModules().stream().noneMatch(c -> c.getName().equalsIgnoreCase(args[0]))) {
            mc.ingameGUI.getChatGUI().addToSentMessages((String.format("Module '%S' not found", args[0])));
            return false;
        }

        Mania.getModuleManager().getModules().stream()
                .filter(m -> m.getName().equalsIgnoreCase(args[0]))
                .forEach(m -> {
                    m.keyCode = Keyboard.getKeyIndex(args[1].toLowerCase());
                    mc.ingameGUI.getChatGUI().addToSentMessages((String.format("Module %s is now bound with %s", m.getName(), Keyboard.getKeyName(m.keyCode))));
                });

        return false;
    }
}
