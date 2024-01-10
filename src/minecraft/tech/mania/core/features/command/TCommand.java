package tech.mania.core.features.command;


import tech.mania.MCHook;
import tech.mania.Mania;
import tech.mania.core.types.command.Command;

public class TCommand extends Command implements MCHook {

    public TCommand() {
        super("t", ".t <module name>");
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length != 1) {
            return true;
        }

        if (Mania.getModuleManager().getModules().stream()
                .noneMatch(m -> m.getName().equalsIgnoreCase(args[0]))) {
            mc.ingameGUI.getChatGUI().addToSentMessages((String.format("Module '%s' not found", args[0])));
            return false;
        }

        Mania.getModuleManager().getModules().stream()
                .filter(m -> m.getName().equalsIgnoreCase(args[0]))
                .forEach(m -> {
                    m.toggle();
                    mc.ingameGUI.getChatGUI().addToSentMessages((String.format(
                            "Module '%s' has been %s",
                            m.getName(),
                            m.isEnabled() ? "enabled" : "disabled"
                    )));
                });

        return false;
    }
}
