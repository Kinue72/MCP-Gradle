package tech.mania.core.types.setting;

public abstract class Setting {

    private static final Visibility VISIBLE = () -> true;

    private final String name;
    private final Visibility visibility;

    protected Setting(final String name, final Visibility visibility) {
        this.name = name;
        this.visibility = visibility;
    }

    public final String getName() {
        return this.name;
    }

    public final boolean isVisible() {
        return this.visibility.isVisible();
    }

    public static abstract class Builder<T extends Setting, E extends Builder> {

        protected String name;
        protected Visibility visibility;

        protected Builder() {
            this.name = "unnamed setting";
            this.visibility = Setting.VISIBLE;
        }

        public E name(final String name) {
            this.name = name;
            return (E) this;
        }

        public E visibility(final Visibility visibility) {
            this.visibility = visibility;
            return (E) this;
        }

        public abstract T end();
    }
}
