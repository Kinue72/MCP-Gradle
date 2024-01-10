package tech.mania.core.features.setting;

import net.minecraft.util.MathHelper;
import tech.mania.core.types.setting.Setting;
import tech.mania.core.types.setting.Visibility;

import java.util.function.Consumer;

public class DoubleSetting extends Setting {

    private static final Consumer<Double> EMPTY = v -> {};

    private final double min, max, increment;
    private final String unit;

    private double value;

    private final Consumer<Double> onSetting;

    private DoubleSetting(String name, Visibility visibility, double min, double max, double increment, double value, String unit, Consumer<Double> onSetting) {
        super(name, visibility);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.unit = unit;
        this.value = value;
        this.onSetting = onSetting;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getUnit() {
        return unit;
    }

    public double getPercentage() {
        return (value - min) / (max - min);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = this.checkValue(value);
        this.onSetting.accept(this.value);
    }

    public void incrementValue(boolean positive) {
        this.setValue(this.value + (positive ? 1d : -1d) * this.increment);
    }

    public void setValue(float posX, float width, float mouseX) {
        this.setValue((mouseX - posX) * (this.max - this.min) / width + this.min);
    }

    private double checkValue(double value) {
        double precision = 1 / increment;
        return Math.round(MathHelper.clamp_double(value, this.min, this.max) * precision) / precision;
    }

    public static DoubleBuilder build() {
        return new DoubleBuilder();
    }

    public static class DoubleBuilder extends Builder<DoubleSetting, DoubleBuilder> {
        private double min, max, increment, value;
        private String unit;
        private Consumer<Double> onSetting;

        private DoubleBuilder() {
            this.min = 0;
            this.max = 10;
            this.increment = 0.1;
            this.unit = "";
            this.value = 5;
            this.onSetting = EMPTY;
        }

        public DoubleBuilder range(double min, double max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public DoubleBuilder increment(double increment) {
            this.increment = increment;
            return this;
        }

        public DoubleBuilder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public DoubleBuilder value(double value) {
            this.value = value;
            return this;
        }

        public DoubleBuilder onSetting(Consumer<Double> onSetting) {
            this.onSetting = onSetting;
            return this;
        }

        @Override
        public DoubleSetting end() {
            return new DoubleSetting(
                    this.name,
                    this.visibility,
                    this.min,
                    this.max,
                    this.increment,
                    this.value,
                    this.unit,
                    this.onSetting
            );
        }
    }
}
