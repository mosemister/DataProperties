package org.mose.property.impl;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BindData<A, B, C, D> {

    private final BaseProperty<C, D> to;
    private final BaseProperty<A, B> from;
    private final Function<A, C> mapping;


    public BindData(@NotNull BaseProperty<A, B> from, @NotNull BaseProperty<C, D> to, @NotNull Function<A, C> mapping) {
        this.from = from;
        this.to = to;
        this.mapping = mapping;
    }

    public BaseProperty<A, B> from() {
        return this.from;
    }

    public Function<A, C> mapping() {
        return this.mapping;
    }

    public void sendValueTo(A value, ValueSetType type) {
        this.to.onValueChange(this.mapping.apply(value), type);
    }

    public BaseProperty<C, D> to() {
        return this.to;
    }

}
