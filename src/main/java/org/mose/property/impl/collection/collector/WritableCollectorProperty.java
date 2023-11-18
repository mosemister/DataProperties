package org.mose.property.impl.collection.collector;

import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.event.PropertyChangeEvent;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WritableCollectorProperty<T, D> extends AbstractCollectorProperty<T, D> {

    private final Map<PropertyChangeEvent<?>, Property<?, ?>> changeEvents = new HashMap<>();


    public WritableCollectorProperty(Function<Stream<T>, D> collect, CollectorValue<?, T>... insert) {
        this(collect, Arrays.asList(insert));
    }

    public WritableCollectorProperty(Function<Stream<T>, D> collect, Collection<CollectorValue<?, T>> insert) {
        super(collect, insert);
        this.changeEvents.putAll(insert
                .stream()
                .collect(Collectors.toMap(value -> (targetProperty, currentValue, type, newValue) -> this.resetCache(),
                        CollectorValue::getProperty)));
    }

    public void addCollectorBind(CollectionProperty<?, Collection<T>> property) {
        this.insert.add(new CollectorValue<>(property, Collection::stream));
        this.changeEvents.put(((targetProperty, currentValue, type, newValue) -> this.resetCache()), property);
    }

    public void addCollectorBind(Property<?, T> property) {
        this.insert.add(new CollectorValue<>(property, Stream::of));
        this.changeEvents.put(((targetProperty, currentValue, type, newValue) -> this.resetCache()), property);
    }

    public void removeCollectorBind(Property<?, ?> property) {
        List<PropertyChangeEvent<?>> entriesToRemove = this
                .changeEvents
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(property))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        entriesToRemove.forEach(event -> {
            this.changeEvents.remove(event);
            property.unregisterValueChangeEvent(event);
        });
    }
}
