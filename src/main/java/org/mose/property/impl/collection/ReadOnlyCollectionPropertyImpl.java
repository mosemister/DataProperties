package org.mose.property.impl.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.CollectionProperty;
import org.mose.property.impl.ValueSetType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReadOnlyCollectionPropertyImpl<T, D extends Collection<?>> extends AbstractCollectionProperty<T, D>
        implements CollectionProperty.ReadOnly<T, D> {

    public ReadOnlyCollectionPropertyImpl(@NotNull Function<Collection<T>, D> displayMappings, Supplier<D> defaultSupplier, @Nullable Collection<T> defaultValue) {
        super(displayMappings, defaultValue, defaultSupplier);
    }

    @Override
    protected boolean onElementsAdded(Collection<T> newValues) {
        if (newValues.isEmpty()) {
            return false;
        }
        return this.sendElementsAdded(newValues);
    }

    @Override
    protected boolean onElementsRemoved(Collection<T> removedValues) {
        if (removedValues.isEmpty()) {
            return false;
        }
        return this.sendElementsRemoved(removedValues);
    }

    @Override
    protected void onValueChange(Collection<T> newValue, ValueSetType type) {
        if (this.value().map(v -> v.equals(newValue)).orElse(false)) {
            return;
        }
        this.sendValueChange(newValue, type);
    }

    public static <V> CollectionProperty.ReadOnly<V, Collection<V>> create() {
        return create(null);
    }

    public static <V> CollectionProperty.ReadOnly<V, Collection<V>> create(@Nullable Collection<V> displayValue) {
        return new ReadOnlyCollectionPropertyImpl<>(t -> t, ArrayList::new, displayValue);
    }
}
