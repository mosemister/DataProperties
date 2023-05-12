package org.mose.property.impl.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.CollectionProperty;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.ValueSetType;

import java.util.Collection;
import java.util.function.Function;

public class WriteCollectionPropertyImpl<T, D extends Collection<?>> extends AbstractCollectionProperty<T, D>
        implements CollectionProperty.Write<T, D> {

    private final ValueOverrideRule rule;

    public WriteCollectionPropertyImpl(Function<Collection<T>, D> displayMappings, @Nullable Collection<T> collection) {
        this(ValueOverrideRule.PREFER_NEWEST, displayMappings, collection);
    }

    public WriteCollectionPropertyImpl(@NotNull ValueOverrideRule rule, @NotNull Function<Collection<T>, D> displayMappings, @Nullable Collection<T> collection) {
        super(displayMappings, collection);
        this.rule = rule;
    }

    public static <V> CollectionProperty.Write<V, Collection<V>> create() {
        return create(null);
    }

    public static <V> CollectionProperty.Write<V, Collection<V>> create(@Nullable Collection<V> displayValue) {
        return new WriteCollectionPropertyImpl<>(t -> t, displayValue);
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
        if (!this.rule.shouldOverride(this.lastKnownValueSetAs, type)) {
            return;
        }
        this.sendValueChange(newValue, type);
    }

    @Override
    public boolean addAll(Collection<T> collection) {
        return this.onElementsAdded(collection);
    }

    @Override
    public boolean removeAll(Collection<T> collection) {
        return this.sendElementsRemoved(collection);
    }

    @Override
    public void setValue(Collection<T> value) {
        this.onValueChange(value, ValueSetType.SET);
    }

    @Override
    public ValueOverrideRule valueOverrideRule() {
        return this.rule;
    }
}
