package tech.mania.core.features.command;

import tech.mania.MCHook;
import tech.mania.Mania;
import tech.mania.core.types.command.Command;

public class HelpCommand extends Command implements MCHook {
    public HelpCommand() {
        super("help", ".help");
    }

    @Override
    public boolean execute(String[] args) {
        Mania.getCommandManager().getCommands().forEach(c -> {
            mc.ingameGUI.getChatGUI().addToSentMessages((c.getHint()));
        });

        return false;
    }
}
