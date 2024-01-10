package tech.mania.core.types.command;


import tech.mania.MCHook;
import tech.mania.core.features.command.*;

import java.util.Arrays;
import java.util.List;

public class CommandManager implements MCHook {

    private static final String PREFIX = ".";

    private final List<Command> commands;

    public CommandManager() {
        commands = Arrays.asList(
                new BindCommand(),
                new HelpCommand(),
                new TCommand(),
                new ToggleCommand()
        );
    }

    public boolean execute(String message) {
        if (!message.startsWith(PREFIX)) {
            return false;
        }

        String[] sp = message.substring(PREFIX.length()).split(" ");

        if (commands.stream().noneMatch(c -> c.getName().equalsIgnoreCase(sp[0]))) {
            mc.ingameGUI.getChatGUI().addToSentMessages((String.format("Command '%s' not found", sp[0])));
            return true;
        }

        if (sp.length == 1) {
            commands.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(sp[0]))
                    .forEach(c -> {
                        if (c.execute(new String[] {})) {
                            mc.ingameGUI.getChatGUI().addToSentMessages((c.getHint()));
                        }
                    });
        } else {
            String[] args = new String[sp.length - 1];
            System.arraycopy(sp, 1, args, 0, sp.length - 1);

            commands.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(sp[0]))
                    .forEach(c -> {
                        if (c.execute(args)) {
                            mc.ingameGUI.getChatGUI().addToSentMessages((c.getHint()));
                        }
                    });
        }

        return true;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
