package org.mose.property.impl.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.impl.ValueSetType;

import java.util.Collection;
import java.util.function.Function;

public class ReadOnlyCollectionProperty<T, D extends Collection<?>> extends AbstractCollectionProperty<T, D>
        implements CollectionProperty.ReadOnly<T, D> {

    public ReadOnlyCollectionProperty(@NotNull Function<Collection<T>, D> displayMappings, @Nullable Collection<T> defaultValue) {
        super(displayMappings, defaultValue);
    }

    @Override
    protected void onElementsAdded(Collection<T> newValues) {
        this.sendElementsAdded(newValues);
    }

    @Override
    protected void onElementsRemoved(Collection<T> removedValues) {
        this.sendElementsRemoved(removedValues);
    }

    @Override
    protected void onValueChange(Collection<T> newValue, ValueSetType type) {
        this.sendValueChange(newValue, type);
    }

    @Override
    public Property.ReadOnly<Collection<T>, D> createBoundReadOnly() {
        ReadOnlyCollectionProperty<T, D> property = new ReadOnlyCollectionProperty<>(this.displayMappings, null);
        property.bindTo(this);
        return property;
    }
}
