package tech.mania.core.features.setting;

import tech.mania.core.types.setting.Setting;
import tech.mania.core.types.setting.Visibility;

import java.util.function.Consumer;

public class BooleanSetting extends Setting {

    private static final Consumer<Boolean> EMPTY = v -> {};

    private boolean value;
    private final Consumer<Boolean> onSetting;

    private BooleanSetting(String name, Visibility visibility, boolean value, Consumer<Boolean> onSetting) {
        super(name, visibility);
        this.onSetting = onSetting;
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public void switchValue() {
        this.value = !this.value;
        this.onSetting.accept(this.value);
    }

    public static BooleanBuilder build() {
        return new BooleanBuilder();
    }

    public static class BooleanBuilder extends Builder<BooleanSetting, BooleanBuilder> {

        private boolean value;
        private Consumer<Boolean> onSetting;

        private BooleanBuilder() {
            super();
            this.onSetting = EMPTY;
            this.value = false;
        }

        public BooleanBuilder value(boolean value) {
            this.value = value;
            return this;
        }

        @Override
        public BooleanSetting end() {
            return new BooleanSetting(
                    this.name,
                    this.visibility,
                    this.value,
                    this.onSetting
            );
        }
    }
}
