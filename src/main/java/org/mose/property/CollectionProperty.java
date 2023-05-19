package org.mose.property;

import org.mose.property.event.CollectionUpdateEvent;

import java.util.Arrays;
import java.util.Collection;

public interface CollectionProperty<T, D extends Collection<?>> extends Property.NeverNull<Collection<T>, D> {

    interface ReadOnly<T, D extends Collection<?>> extends Property.ReadOnly<Collection<T>, D>, CollectionProperty<T, D> {

    }

    interface Write<T, D extends Collection<?>> extends Property.Write<Collection<T>, D>, CollectionProperty<T, D> {

        default boolean add(T value) {
            return this.addAll(value);
        }

        default boolean addAll(T... add) {
            return this.addAll(Arrays.asList(add));
        }

        boolean addAll(Collection<T> collection);

        default boolean remove(T remove) {
            return this.removeAll(remove);
        }

        default boolean removeAll(T... remove) {
            return this.removeAll(Arrays.asList(remove));
        }

        boolean removeAll(Collection<T> collection);

        default void setValue(T... values) {
            this.setValue(Arrays.asList(values));
        }

    }

    void registerCollectionAddEvent(CollectionUpdateEvent.CollectionAddIndexEvent<T> addEvent);

    void registerCollectionRemoveEvent(CollectionUpdateEvent.CollectionRemoveIndexEvent<T> removeEvent);

}
