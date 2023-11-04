package org.mose.property.impl.collection.collector;

import org.jetbrains.annotations.NotNull;
import org.mose.property.Property;

import java.util.function.Function;
import java.util.stream.Stream;

public class CollectorValue<E, T> {

    private final @NotNull Property<?, E> property;
    private final @NotNull Function<E, Stream<T>> toStream;

    public CollectorValue(@NotNull Property<?, E> property, Function<E, Stream<T>> toStream) {
        this.property = property;
        this.toStream = toStream;
    }

    @NotNull
    Property<?, E> getProperty() {
        return this.property;
    }

    @NotNull
    protected Stream<T> toStream(E value){
        return this.toStream.apply(value);
    }
}
