package org.mose.property.impl;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BindData<A, B, C, D> {

    private final AbstractProperty<C, D> to;
    private final AbstractProperty<A, B> from;
    private final Function<A, C> mapping;

    public BindData(@NotNull AbstractProperty<A, B> from, @NotNull AbstractProperty<C, D> to, @NotNull Function<A, C> mapping) {
        this.from = from;
        this.to = to;
        this.mapping = mapping;
    }

    public AbstractProperty<A, B> from() {
        return this.from;
    }

    public Function<A, C> mapping() {
        return this.mapping;
    }

    public void sendValueTo(A value, ValueSetType type) {
        this.to.onValueChange(this.mapping.apply(value), type);
    }

    public AbstractProperty<C, D> to() {
        return this.to;
    }

}
