package org.mose.property.impl;

import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;

import java.util.function.Function;

public class WritePropertyImpl<T, D> extends AbstractProperty<T, D> implements Property.Write<T, D> {

    private final ValueOverrideRule rule;

    public WritePropertyImpl(Function<T, D> displayMappings, @Nullable T defaultValue) {
        this(ValueOverrideRule.PREFER_NEWEST, displayMappings, defaultValue);
    }

    public WritePropertyImpl(ValueOverrideRule rule, Function<T, D> displayMappings, @Nullable T defaultValue) {
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
    public void setValue(T value) {
        this.onValueChange(value, ValueSetType.SET);
    }
}
