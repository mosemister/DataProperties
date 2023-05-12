package org.mose.property;

import org.jetbrains.annotations.NotNull;
import org.mose.property.event.PropertyChangeEvent;

import java.util.Optional;
import java.util.function.Function;

public interface Property<T, D> {

    <V, E> void bindTo(@NotNull Property<V, E> property, @NotNull Function<V, T> map);

    default void bindTo(@NotNull Property<T, ?> property) {
        this.bindTo(property, t -> t);
    }

    Optional<Property<?, ?>> bound();

    ReadOnly<T, D> createBoundReadOnly();

    default <A> ReadOnly<A, A> createBoundReadOnly(Function<T, A> map) {
        return createBoundReadOnly(map, t -> t);
    }

    <A, B> ReadOnly<A, B> createBoundReadOnly(Function<T, A> map, Function<A, B> displayMapping);

    void registerValueChangeEvent(PropertyChangeEvent<T> event);

    void removeBind();

    Optional<D> value();

    ValueOverrideRule valueOverrideRule();


    interface ReadOnly<T, D> extends Property<T, D> {

        @Override
        default ValueOverrideRule valueOverrideRule() {
            return ValueOverrideRule.PREFER_BOUND;
        }
    }

    interface Write<T, D> extends Property<T, D> {

        void setValue(T value);

    }

}
