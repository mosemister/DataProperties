package org.mose.property.impl.collection.collector;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

public class ReadOnlyCollectorProperty<T, D> extends AbstractCollectorProperty<T, D> {

    public ReadOnlyCollectorProperty(Function<Stream<T>, D> collect, CollectorValue<?, T>... insert) {
        this(collect, Arrays.asList(insert));
    }

    public ReadOnlyCollectorProperty(Function<Stream<T>, D> collect, Collection<CollectorValue<?, T>> insert) {
        super(collect, insert);
    }
}
