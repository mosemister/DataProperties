package org.mose.property;

import org.jetbrains.annotations.NotNull;
import org.mose.property.event.CollectionUpdateEvent;
import org.mose.property.impl.collection.collector.WritableCollectorProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public interface CollectionProperty<T, D extends Collection<?>> extends Property.NeverNull<Collection<T>, D> {

    interface ReadOnly<T, D extends Collection<?>> extends Property.ReadOnly<Collection<T>, D>, CollectionProperty<T, D> {

    }

    interface Write<T, D extends Collection<?>> extends Property.Write<Collection<T>, D>, CollectionProperty<T, D> {

        default boolean add(@NotNull T value) {
            return this.addAll(value);
        }

        default boolean addAll(@NotNull T... add) {
            return this.addAll(Arrays.asList(add));
        }

        boolean addAll(@NotNull Collection<T> collection);

        default boolean remove(@NotNull T remove) {
            return this.removeAll(remove);
        }

        default boolean removeAll(@NotNull T... remove) {
            return this.removeAll(Arrays.asList(remove));
        }

        boolean removeAll(@NotNull Collection<T> collection);

        default void setValue(T... values) {
            this.setValue(Arrays.asList(values));
        }

    }

    void registerCollectionAddEvent(CollectionUpdateEvent.CollectionAddIndexEvent<T> addEvent);

    void registerCollectionRemoveEvent(CollectionUpdateEvent.CollectionRemoveIndexEvent<T> removeEvent);

    @NotNull
    @Override
    CollectionProperty.ReadOnly<T, D> createBoundReadOnly();

    <B> @NotNull WritableCollectorProperty<B, Collection<B>> createCollectingBind(@NotNull Function<T, Property<?, B>> func);

    <B extends Collection<C>, C> @NotNull WritableCollectorProperty<C, Collection<C>> createFlatCollectingBind(@NotNull Function<T, CollectionProperty<?, Collection<C>>> func);


}
