package org.mose.property.impl;

import org.jetbrains.annotations.NotNull;
import org.mose.property.Property;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;

public abstract class BaseProperty<T, D> implements Property<T, D> {

    protected final Queue<BindData<T, D, ?, ?>> bindData = new LinkedTransferQueue<>();


    protected abstract <C, F> BindData<T, D, C, F> bindFrom(@NotNull BaseProperty<C, F> property, Function<T, C> mappings);

    protected abstract void onValueChange(T newValue, ValueSetType type);

    protected abstract Optional<T> valueImpl();

    protected Optional<T> unmappedValue() {
        return this.valueImpl();
    }
}
