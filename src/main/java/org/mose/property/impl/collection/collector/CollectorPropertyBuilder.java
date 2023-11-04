package org.mose.property.impl.collection.collector;

import org.jetbrains.annotations.NotNull;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;
import java.util.stream.Stream;

public class CollectorPropertyBuilder<T, D> {

    private final Collection<CollectorValue<?, T>> queue = new LinkedTransferQueue<>();
    private Function<Stream<T>, D> collector;

    public Function<Stream<T>, D> collector() {
        return this.collector;
    }

    public CollectorPropertyBuilder<T, D> setCollector(@NotNull Function<Stream<T>, D> collector) {
        this.collector = collector;
        return this;
    }

    public Collection<CollectorValue<?, T>> queue() {
        return this.queue;
    }

    public CollectorPropertyBuilder<T, D> setQueue(Collection<CollectorValue<?, T>> value) {
        this.queue.clear();
        this.queue.addAll(value);
        return this;
    }

    public ReadOnlyCollectorProperty<T, D> build() {
        return new ReadOnlyCollectorProperty<>(this.collector, this.queue);
    }


    @SafeVarargs
    public final CollectorPropertyBuilder<T, D> setQueue(CollectorValue<?, T>... value) {
        return this.setQueue(Arrays.asList(value));
    }

    public CollectorPropertyBuilder<T, D> addQueue(Collection<CollectorValue<?, T>> value) {
        this.queue.addAll(value);
        return this;
    }

    @SafeVarargs
    public final CollectorPropertyBuilder<T, D> addQueue(CollectorValue<?, T>... value) {
        return this.addQueue(Arrays.asList(value));
    }

    public CollectorPropertyBuilder<T, D> addSingle(Property<?, T> property) {
        return this.addQueue(new CollectorValue<T, T>(property, Stream::of));
    }

    public <C extends Collection<T>> CollectorPropertyBuilder<T, D> addCollection(CollectionProperty<T, C> collection) {
        return this.addQueue(new CollectorValue<>(collection, Collection::stream));
    }


}
