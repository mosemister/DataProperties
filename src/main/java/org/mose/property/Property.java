package org.mose.property;

import org.jetbrains.annotations.NotNull;
import org.mose.property.event.PropertyChangeEvent;
import org.mose.property.impl.ReadOnlyPropertyImpl;
import org.mose.property.impl.WritePropertyImpl;
import org.mose.property.impl.collection.ReadOnlyCollectionPropertyImpl;
import org.mose.property.impl.collection.WriteCollectionPropertyImpl;
import org.mose.property.impl.nevernull.ReadOnlyNeverNullPropertyImpl;
import org.mose.property.impl.nevernull.WriteNeverNullPropertyImpl;
import org.mose.property.impl.number.ReadOnlyNumberPropertyImpl;
import org.mose.property.impl.number.WriteNumberPropertyImpl;
import org.mose.property.utils.ClassUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Function;

public interface Property<T, D> {

    interface Number<T, D extends java.lang.Number> extends Property<T, D> {
        default @NotNull OptionalInt asInt() {
            return this.value().map(n -> OptionalInt.of(n.intValue())).orElse(OptionalInt.empty());
        }

        default @NotNull OptionalDouble asDouble() {
            return this.value().map(n -> OptionalDouble.of(n.doubleValue())).orElse(OptionalDouble.empty());
        }
    }

    interface NeverNull<T, D> extends Property<T, D> {

        @NotNull
        D safeValue();

        @Override
        @NotNull
        Optional<D> value();
    }

    interface ReadOnly<T, D> extends Property<T, D> {

        @Override
        default @NotNull ValueOverrideRule valueOverrideRule() {
            return ValueOverrideRule.PREFER_BOUND;
        }
    }

    interface Write<T, D> extends Property<T, D> {

        void setValue(T value);

    }

    <V, E> void bindTo(@NotNull Property<V, E> property, @NotNull Function<V, T> map);

    default void bindTo(@NotNull Property<T, ?> property) {
        this.bindTo(property, t -> t);
    }

    boolean canBind();

    boolean isBindLocked();

    void lockBind();

    @NotNull
    Optional<Property<?, ?>> bound();

    @NotNull
    ReadOnly<T, D> createBoundReadOnly();

    void registerValueChangeEvent(@NotNull PropertyChangeEvent<T> event);

    void unregisterValueChangeEvent(@NotNull PropertyChangeEvent<?> event);

    void removeBind();

    @NotNull
    Optional<D> value();

    @NotNull
    ValueOverrideRule valueOverrideRule();

    static @NotNull Class<? extends Property.ReadOnly> getReadOnlyPreferedClass(@NotNull Class<?> clazz) {
        if (clazz.isPrimitive()) {
            clazz = ClassUtils.fromPrimitive(clazz);
        }
        if (java.lang.Number.class.isAssignableFrom(clazz)) {
            return ReadOnlyNumberPropertyImpl.class;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return ReadOnlyCollectionPropertyImpl.class;
        }
        if (Boolean.class.equals(clazz)) {
            return ReadOnlyNeverNullPropertyImpl.class;
        }
        return ReadOnlyPropertyImpl.class;
    }

    static @NotNull Class<? extends Property.Write> getWritePreferedClass(@NotNull Class<?> clazz) {
        if (clazz.isPrimitive()) {
            clazz = ClassUtils.fromPrimitive(clazz);
        }
        if (java.lang.Number.class.isAssignableFrom(clazz)) {
            return WriteNumberPropertyImpl.class;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return WriteCollectionPropertyImpl.class;
        }
        if (Boolean.class.equals(clazz)) {
            return WriteNeverNullPropertyImpl.class;
        }
        return WritePropertyImpl.class;
    }

}
