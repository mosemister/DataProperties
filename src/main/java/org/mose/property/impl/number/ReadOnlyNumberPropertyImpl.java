package org.mose.property.impl.number;

import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.impl.AbstractProperty;
import org.mose.property.impl.ValueSetType;

import java.util.function.Function;

public class ReadOnlyNumberPropertyImpl<T, D extends Number> extends AbstractProperty<T, D> implements Property.ReadOnly<T, D>, Property.Number<T, D> {

    public ReadOnlyNumberPropertyImpl(Function<T, D> displayMapping, @Nullable T displayValue) {
        super(displayMapping, displayValue);
    }

    @Override
    public ReadOnly<T, D> createBoundReadOnly() {
        ReadOnlyNumberPropertyImpl<T, D> property = this.createReadOnly(this.displayMappings);
        property.bindTo(this);
        return property;
    }

    @Override
    protected void onValueChange(T newValue, ValueSetType type) {
        if (this.value().map(v -> v.equals(newValue)).orElse(false)) {
            return;
        }
        this.sendValueChange(newValue, type);
    }

    @Override
    protected ReadOnlyNumberPropertyImpl<T, D> createReadOnly(Function<T, D> displayMappings) {
        return new ReadOnlyNumberPropertyImpl<>(displayMappings, null);
    }

    public static <V extends java.lang.Number> ReadOnly<V, V> create() {
        return create(null);
    }

    public static <V extends java.lang.Number> ReadOnly<V, V> create(@Nullable V displayValue) {
        return new ReadOnlyNumberPropertyImpl<>(t -> t, displayValue);
    }
}
