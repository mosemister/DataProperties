package org.mose.property.impl.number;

import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.AbstractProperty;
import org.mose.property.impl.ValueSetType;

import java.util.function.Function;

public class WriteNumberPropertyImpl<T, D extends Number> extends AbstractProperty<T, D> implements Property.Write<T, D>, Property.Number<T, D> {

    private final ValueOverrideRule rule;

    public WriteNumberPropertyImpl(Function<T, D> displayMappings, @Nullable T defaultValue) {
        this(ValueOverrideRule.PREFER_NEWEST, displayMappings, defaultValue);
    }

    public WriteNumberPropertyImpl(ValueOverrideRule rule, Function<T, D> displayMappings, @Nullable T defaultValue) {
        super(displayMappings, defaultValue);
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
    protected ReadOnly<T, D> createReadOnly(Function<T, D> displayMappings) {
        return new ReadOnlyNumberPropertyImpl<>(displayMappings, null);
    }

    @Override
    public void setValue(T value) {
        this.onValueChange(value, ValueSetType.SET);
    }

    public static <V extends java.lang.Number> WriteNumberPropertyImpl<V, V> create() {
        return create(null);
    }

    public static <V extends java.lang.Number> WriteNumberPropertyImpl<V, V> create(@Nullable V displayValue) {
        return new WriteNumberPropertyImpl<>(t -> t, displayValue);
    }
}
