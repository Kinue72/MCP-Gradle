package tech.mania.core.features.setting;


import tech.mania.core.types.setting.Setting;
import tech.mania.core.types.setting.Visibility;

import java.util.function.Consumer;

public class ModeSetting extends Setting {

    private static final Consumer<String> EMPTY = v -> {};

    private final String[] option;
    private String value;
    private int index;
    private final Consumer<String> onSetting;
    public boolean expand;

    private ModeSetting(String name, Visibility visibility, String[] option, Consumer<String> onSetting) {
        super(name, visibility);
        this.option = option;
        this.index = 0;
        this.value = option[0];
        this.onSetting = onSetting;
    }

    public String[] getOption() {
        return option;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public void setValue(final String value) {
        this.value = value;
        this.index = indexOf(this.value, this.option);
        this.onSetting.accept(value);
    }

    public void setValue(final int index) {
        this.index = index;
        this.value = this.option[index];
        this.onSetting.accept(this.value);
    }

    public void increment(final boolean positive) {
        if (positive) {
            this.index = this.index < this.option.length - 1 ? this.index + 1 : 0;
        } else {
            this.index = this.index <= 0 ? this.option.length - 1 : this.index - 1;
        }
        this.setValue(this.index);
    }

    public static ModeBuilder build() {
        return new ModeBuilder();
    }

    private static int indexOf(final String a, final String[] b) {
        for (int i = 0; i < b.length; i++) {
            if (b[i].equals(a)) return i;
        }
        return -1;
    }

    public static class ModeBuilder extends Builder<ModeSetting, ModeBuilder> {

        private int index;
        private String value;
        private String[] option;
        private Consumer<String> onSetting;

        private ModeBuilder() {
            this.index = -1;
            this.value = null;
            this.option = new String[0];
            this.onSetting = EMPTY;
        }

        public ModeBuilder value(String value) {
            this.value = value;
            return this;
        }

        public ModeBuilder index(int index) {
            this.index = index;
            return this;
        }

        public ModeBuilder option(final String... option) {
            this.option = option;
            return this;
        }

        public ModeBuilder onSetting(final Consumer<String> onSetting) {
            this.onSetting = onSetting;
            return this;
        }

        @Override
        public ModeSetting end() {
            if (this.index == -1 && this.value == null) {
                this.value = this.option[this.index = 0];
            } else if (this.index != -1) {
                this.value = this.option[this.index];
            } else {
                this.index = indexOf(this.value, this.option);
            }
            return new ModeSetting(
                    this.name,
                    this.visibility,
                    this.option,
                    this.onSetting
            );
        }
    }
}
