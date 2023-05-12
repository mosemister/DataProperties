package org.mose.property.impl.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.ValueSetType;

import java.util.Collection;
import java.util.function.Function;

public class WriteCollectionProperty<T, D extends Collection<?>> extends AbstractCollectionProperty<T, D>
        implements CollectionProperty.Write<T, D> {

    private final ValueOverrideRule rule;

    public WriteCollectionProperty(Function<Collection<T>, D> displayMappings, @Nullable Collection<T> collection) {
        this(ValueOverrideRule.PREFER_NEWEST, displayMappings, collection);
    }

    public WriteCollectionProperty(@NotNull ValueOverrideRule rule, @NotNull Function<Collection<T>, D> displayMappings, @Nullable Collection<T> collection) {
        super(displayMappings, collection);
        this.rule = rule;
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
        if (!this.rule.shouldOverride(this.lastKnownValueSetAs, type)) {
            return;
        }
        this.sendValueChange(newValue, type);
    }

    @Override
    public Property.ReadOnly<Collection<T>, D> createBoundReadOnly() {
        ReadOnlyCollectionProperty<T, D> property = new ReadOnlyCollectionProperty<>(this.displayMappings, null);
        property.bindTo(this);
        return property;
    }

    @Override
    public boolean addAll(Collection<T> collection) {
        return this.sendElementsAdded(collection);
    }

    @Override
    public boolean removeAll(Collection<T> collection) {
        return this.sendElementsRemoved(collection);
    }

    @Override
    public void setValue(Collection<T> value) {
        this.sendValueChange(value, ValueSetType.SET);
    }

    @Override
    public ValueOverrideRule valueOverrideRule() {
        return this.rule;
    }
}
