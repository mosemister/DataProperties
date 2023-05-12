package org.mose.property.impl;

import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;

import java.util.function.Function;

public class ReadOnlyPropertyImpl<T, D> extends AbstractProperty<T, D> implements Property.ReadOnly<T, D> {

    public ReadOnlyPropertyImpl(Function<T, D> displayMapping, @Nullable T displayValue) {
        super(displayMapping, displayValue);
    }

    @Override
    protected void onValueChange(T newValue, ValueSetType type) {
        this.sendValueChange(newValue, type);
    }

    @Override
    public ReadOnly<T, D> createBoundReadOnly() {
        ReadOnlyPropertyImpl<T, D> property = new ReadOnlyPropertyImpl<>(this.displayMappings, null);
        property.bindTo(this);
        return property;
    }
}
