package org.mose.property.impl.collection;

import org.jetbrains.annotations.NotNull;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.event.CollectionUpdateEvent;
import org.mose.property.impl.BindData;
import org.mose.property.impl.ValueSetType;
import org.mose.property.impl.collection.collector.AbstractCollectorProperty;
import org.mose.property.impl.collection.collector.CollectorPropertyBuilder;
import org.mose.property.impl.collection.collector.WritableCollectorProperty;
import org.mose.property.impl.nevernull.AbstractNeverNullProperty;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractCollectionProperty<T, D extends Collection<?>> extends AbstractNeverNullProperty<Collection<T>, D> implements CollectionProperty<T, D> {

    private final Queue<CollectionUpdateEvent<T>> indexUpdateEvents = new LinkedTransferQueue<>();

    protected AbstractCollectionProperty(@NotNull Function<Collection<T>, D> displayMappings, Collection<T> defaultValue, Supplier<D> defaultSupplier) {
        super(displayMappings, defaultSupplier, defaultValue);
    }

    protected abstract boolean onElementsAdded(Collection<T> newValues);

    protected abstract boolean onElementsRemoved(Collection<T> removedValues);

    @Override
    public void registerCollectionAddEvent(CollectionUpdateEvent.CollectionAddIndexEvent<T> addEvent) {
        this.indexUpdateEvents.offer(addEvent);
    }

    @Override
    public void registerCollectionRemoveEvent(CollectionUpdateEvent.CollectionRemoveIndexEvent<T> removeEvent) {
        this.indexUpdateEvents.offer(removeEvent);
    }

    @Override
    public @NotNull <B> WritableCollectorProperty<B, Collection<B>> createCollectingBind(@NotNull Function<T, Property<?, B>> func) {
        Collection<Property<?, B>> collection = this.valueImpl().orElse(Collections.emptyList()).stream().map(func).collect(Collectors.toList());
        CollectorPropertyBuilder<B, Collection<B>> builder = new CollectorPropertyBuilder<>();
        collection.forEach(builder::addSingle);
        builder.setCollector(stream -> stream.collect(Collectors.toList()));
        builder.setWritable(true);
        return (WritableCollectorProperty<B, Collection<B>>) builder.build();
    }

    @Override
    public @NotNull <B extends Collection<C>, C> WritableCollectorProperty<C, Collection<C>> createFlatCollectingBind(@NotNull Function<T, CollectionProperty<?, Collection<C>>> func) {
        Collection<T> collection = this.valueImpl().orElse(Collections.emptyList());
        List<CollectionProperty<?, Collection<C>>> properties = collection.stream().map(func).collect(Collectors.toList());
        CollectorPropertyBuilder<C, Collection<C>> builder = new CollectorPropertyBuilder<>();
        properties.forEach(builder::addCollection);
        builder.setCollector(cStream -> cStream.collect(Collectors.toList()));
        builder.setWritable(true);
        AbstractCollectorProperty<C, Collection<C>> result = builder.build();
        return (WritableCollectorProperty<C, Collection<C>>) result;
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
        Collection<T> value = this.lastKnownValue;
        if (null == value) {
            value = this.valueImpl().orElseGet(Collections::emptyList);
        }
        final Collection<T> finalValue = value;
        this.bindData.parallelStream().filter(data -> data.to() instanceof AbstractCollectionProperty).forEach(data -> {
            this.sendElementChange(data, added, AbstractCollectionProperty::onElementsAdded);
        });
        this.indexUpdateEvents
                .parallelStream()
                .filter(event -> event instanceof CollectionUpdateEvent.CollectionAddIndexEvent)
                .forEach(event -> event.handle(this, finalValue, added));
        Collection<T> originalValues = new LinkedList<>(value);
        boolean result = finalValue.addAll(added);
        this.changeValueEvents.parallelStream().forEach(event -> event.handle(this, originalValues, ValueSetType.SET, finalValue));
        return result;
    }

    protected boolean sendElementsRemoved(Collection<T> removed) {
        Collection<T> value = this.lastKnownValue;
        if (null == value) {
            value = this.valueImpl().orElseGet(Collections::emptyList);
        }
        final Collection<T> finalValue = value;

        this.bindData.parallelStream().filter(data -> data.to() instanceof AbstractCollectionProperty).forEach(data -> {
            this.sendElementChange(data, removed, AbstractCollectionProperty::onElementsRemoved);
        });
        this.indexUpdateEvents
                .parallelStream()
                .filter(event -> event instanceof CollectionUpdateEvent.CollectionRemoveIndexEvent)
                .forEach(event -> event.handle(this, finalValue, removed));
        Collection<T> originalValues = new LinkedList<>(value);
        boolean result = value.removeAll(removed);
        this.changeValueEvents.parallelStream().forEach(event -> event.handle(this, originalValues, ValueSetType.SET, finalValue));
        return result;
    }

    @Override
    protected ReadOnlyCollectionPropertyImpl<T, D> createReadOnly(Function<Collection<T>, D> displayMappings) {
        return new ReadOnlyCollectionPropertyImpl<>(this.displayMappings, this.defaultSupplier, null);
    }

    @Override
    public ReadOnlyCollectionPropertyImpl<T, D> createBoundReadOnly() {
        return (ReadOnlyCollectionPropertyImpl<T, D>) super.createBoundReadOnly();
    }
}
