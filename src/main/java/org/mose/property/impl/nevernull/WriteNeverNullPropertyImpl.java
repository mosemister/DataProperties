package org.mose.property.impl.nevernull;

import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.ValueSetType;

import java.util.function.Function;
import java.util.function.Supplier;

public class WriteNeverNullPropertyImpl<T, D> extends AbstractNeverNullProperty<T, D> implements Property.Write<T, D> {

    private final ValueOverrideRule rule;

    public WriteNeverNullPropertyImpl(Function<T, D> displayMappings, Supplier<D> defaultSupplier, @Nullable T defaultValue) {
        this(ValueOverrideRule.PREFER_NEWEST, displayMappings, defaultSupplier, defaultValue);
    }

    public WriteNeverNullPropertyImpl(ValueOverrideRule rule, Function<T, D> displayMappings, Supplier<D> defaultSupplier, @Nullable T defaultValue) {
        super(displayMappings, defaultSupplier, defaultValue);
        this.rule = rule;
    }

    @Override
    public ValueOverrideRule valueOverrideRule() {
        return this.rule;
    }

    @Override
    protected void onValueChange(T newValue, ValueSetType type) {
        if (this.value().map(v -> v.equals(newValue)).orElse(false)) {
            return;
        }
        if (!this.rule.shouldOverride(this.lastKnownValueSetAs, type)) {
            return;
        }
        this.sendValueChange(newValue, type);
    }

    @Override
    public void setValue(T value) {
        this.onValueChange(value, ValueSetType.SET);
    }

    public static <V> WriteNeverNullPropertyImpl<V, V> create(Supplier<V> defaultSupplier) {
        return create(defaultSupplier, null);
    }

    public static <V> WriteNeverNullPropertyImpl<V, V> create(Supplier<V> defaultSupplier, @Nullable V displayValue) {
        return new WriteNeverNullPropertyImpl<>(t -> t, defaultSupplier, displayValue);
    }

    public static WriteNeverNullPropertyImpl<Boolean, Boolean> bool() {
        return bool(false);
    }

    public static WriteNeverNullPropertyImpl<Boolean, Boolean> bool(boolean defaultValue) {
        return create(() -> false, defaultValue);
    }
}
