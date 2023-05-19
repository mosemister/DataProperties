package org.mose.property.impl.nevernull;

import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.impl.ValueSetType;

import java.util.function.Function;
import java.util.function.Supplier;

public class ReadOnlyNeverNullPropertyImpl<T, D> extends AbstractNeverNullProperty<T, D> implements Property.ReadOnly<T, D> {

    public ReadOnlyNeverNullPropertyImpl(Function<T, D> displayMapping, Supplier<D> defaultSupplier, @Nullable T displayValue) {
        super(displayMapping, defaultSupplier, displayValue);
    }

    @Override
    protected void onValueChange(T newValue, ValueSetType type) {
        if (this.value().map(v -> v.equals(newValue)).orElse(false)) {
            return;
        }
        this.sendValueChange(newValue, type);
    }

    @Override
    public ReadOnly<T, D> createBoundReadOnly() {
        ReadOnlyNeverNullPropertyImpl<T, D> property = new ReadOnlyNeverNullPropertyImpl<>(this.displayMappings, this.defaultSupplier, null);
        property.bindTo(this);
        return property;
    }

    public static <V> ReadOnly<V, V> create(Supplier<V> defaultSupplier) {
        return create(defaultSupplier, null);
    }

    public static <V> ReadOnly<V, V> create(Supplier<V> defaultSupplier, @Nullable V displayValue) {
        return new ReadOnlyNeverNullPropertyImpl<>(t -> t, defaultSupplier, displayValue);
    }

    public static ReadOnly<Boolean, Boolean> bool() {
        return bool(false);
    }

    public static ReadOnly<Boolean, Boolean> bool(boolean defaultValue) {
        return create(() -> false, defaultValue);
    }
}
