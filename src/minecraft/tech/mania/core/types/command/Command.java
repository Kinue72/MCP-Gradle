package tech.mania.core.types.command;

public abstract class Command {

    private final String name, hint;

    public Command(String name, String hint) {
        this.hint = hint;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getHint() {
        return hint;
    }

    public abstract boolean execute(String[] args);
}
