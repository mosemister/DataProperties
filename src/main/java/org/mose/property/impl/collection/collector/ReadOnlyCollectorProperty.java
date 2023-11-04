package org.mose.property.impl.collection.collector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.event.PropertyChangeEvent;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;
import java.util.stream.Stream;

public class ReadOnlyCollectorProperty<T, D> implements Property.ReadOnly<D, D> {

    private final Queue<PropertyChangeEvent<D>> changeValueEvents = new LinkedTransferQueue<>();
    private final LinkedTransferQueue<CollectorValue<?, T>> insert = new LinkedTransferQueue<>();
    private final Function<Stream<T>, D> collect;
    @Nullable
    private D lastKnown;

    public ReadOnlyCollectorProperty(Function<Stream<T>, D> collect, CollectorValue<?, T>... insert) {
        this(collect, Arrays.asList(insert));
    }

    public ReadOnlyCollectorProperty(Function<Stream<T>, D> collect, Collection<CollectorValue<?, T>> insert) {
        this.collect = collect;
        this.insert.addAll(insert);
        this.insert.forEach(value -> value.getProperty().registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> this.resetCache()));
    }


    @Override
    public <V, E> void bindTo(@NotNull Property<V, E> property, @NotNull Function<V, D> map) {
    }

    @Override
    public boolean canBind() {
        return false;
    }

    @Override
    public boolean isBindLocked() {
        return true;
    }

    @Override
    public void lockBind() {

    }

    @Override
    public Optional<Property<?, ?>> bound() {
        return Optional.empty();
    }

    @Override
    public ReadOnly<D, D> createBoundReadOnly() {
        return new ReadOnlyCollectorProperty<>(this.collect, new ArrayList<>(this.insert));
    }

    @Override
    public void registerValueChangeEvent(PropertyChangeEvent<D> event) {
        this.changeValueEvents.add(event);
    }

    @Override
    public void removeBind() {

    }

    @Override
    public Optional<D> value() {
        if (null != this.lastKnown) {
            return Optional.of(this.lastKnown);
        }
        Stream<T> stream = Stream.empty();
        for (CollectorValue<?, T> entry : this.insert) {
            stream = this.concat(entry, stream);
        }
        this.lastKnown = this.collect.apply(stream);
        return Optional.ofNullable(this.lastKnown);
    }

    public void resetCache() {
        this.lastKnown = null;
    }

    private <F> Stream<T> concat(CollectorValue<F, T> entry, Stream<T> current) {
        Optional<F> opValue = entry.getProperty().value();
        return opValue.map(f -> Stream.concat(current, entry.toStream(f))).orElse(current);
    }

    public static <C, X> CollectorPropertyBuilder<C, X> builder() {
        return new CollectorPropertyBuilder<>();
    }
}
