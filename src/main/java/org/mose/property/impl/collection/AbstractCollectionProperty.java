package org.mose.property.impl.collection;

import org.jetbrains.annotations.NotNull;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.event.CollectionUpdateEvent;
import org.mose.property.impl.AbstractProperty;
import org.mose.property.impl.BindData;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractCollectionProperty<T, D extends Collection<?>> extends AbstractProperty<Collection<T>, D> implements CollectionProperty<T, D> {

    private final Queue<CollectionUpdateEvent<T>> indexUpdateEvents = new LinkedTransferQueue<>();

    protected AbstractCollectionProperty(@NotNull Function<Collection<T>, D> displayMappings, Collection<T> defaultValue) {
        super(displayMappings, defaultValue);
    }

    protected abstract boolean onElementsAdded(Collection<T> newValues);

    protected abstract boolean onElementsRemoved(Collection<T> removedValues);

    @Override
    public void registerCollectionAddEvent(CollectionUpdateEvent.CollectionAddIndexEvent<T> addEvent) {
        this.indexUpdateEvents.offer(addEvent);
    }

    @Override
    public void registerCollectionRemoveEvent(CollectionUpdateEvent.CollectionRemoveIndexEvent<T> removeEvent) {
        this.indexUpdateEvents.remove(removeEvent);
    }

    private <X, Z extends Collection<?>> void sendElementChange(BindData<Collection<T>, D, ?, ?> bindData,
                                                                Collection<T> values,
                                                                BiConsumer<AbstractCollectionProperty<X, Z>, Collection<X>> on) {
        BindData<Collection<T>, D, Collection<X>, Z> fixedBindData = (BindData<Collection<T>, D, Collection<X>, Z>) bindData;
        AbstractCollectionProperty<X, Z> to = (AbstractCollectionProperty<X, Z>) bindData.to();
        Collection<X> mapped = fixedBindData.mapping().apply(values);
        on.accept(to, mapped);
    }

    protected boolean sendElementsAdded(Collection<T> added) {
        this.bindData.parallelStream().filter(data -> data.to() instanceof AbstractCollectionProperty).forEach(data -> {
            this.sendElementChange(data, added, AbstractCollectionProperty::onElementsAdded);
        });
        this.indexUpdateEvents
                .parallelStream()
                .filter(event -> event instanceof CollectionUpdateEvent.CollectionAddIndexEvent)
                .forEach(event -> event.handle(this, this.lastKnownValue, added));
        return this.lastKnownValue.addAll(added);
    }

    protected boolean sendElementsRemoved(Collection<T> removed) {
        this.bindData.parallelStream().filter(data -> data.to() instanceof AbstractCollectionProperty).forEach(data -> {
            this.sendElementChange(data, removed, AbstractCollectionProperty::onElementsRemoved);
        });
        this.indexUpdateEvents
                .parallelStream()
                .filter(event -> event instanceof CollectionUpdateEvent.CollectionRemoveIndexEvent)
                .forEach(event -> event.handle(this, this.lastKnownValue, removed));
        return this.lastKnownValue.removeAll(removed);
    }

    @Override
    public Property.ReadOnly<Collection<T>, D> createBoundReadOnly() {
        ReadOnlyCollectionPropertyImpl<T, D> property = new ReadOnlyCollectionPropertyImpl<>(this.displayMappings, null);
        property.bindTo(this);
        return property;
    }
}
