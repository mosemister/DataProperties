package org.mose.property.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.event.PropertyChangeEvent;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;

public abstract class AbstractProperty<T, D> implements Property<T, D> {

    protected final Queue<BindData<T, D, ?, ?>> bindData = new LinkedTransferQueue<>();
    protected final Function<T, D> displayMappings;
    private final Queue<PropertyChangeEvent<T>> changeValueEvents = new LinkedTransferQueue<>();
    protected T lastKnownValue;
    protected @NotNull ValueSetType lastKnownValueSetAs;
    @Nullable
    private BindData<?, ?, T, D> boundTo;

    protected AbstractProperty(@NotNull Function<T, D> displayMappings, @Nullable T defaultValue) {
        this.displayMappings = displayMappings;
        this.lastKnownValue = defaultValue;
        this.lastKnownValueSetAs = ValueSetType.SET;
    }

    private <C, F> BindData<T, D, C, F> bindFrom(@NotNull AbstractProperty<C, F> property, Function<T, C> mappings) {
        BindData<T, D, C, F> bindData = new BindData<>(this, property, mappings);
        this.bindData.offer(bindData);
        return bindData;
    }

    @Override
    public <V, E> void bindTo(@NotNull Property<V, E> property, @NotNull Function<V, T> map) {
        if (null != this.boundTo) {
            this.removeBind();
        }
        if (!(property instanceof AbstractProperty)) {
            throw new RuntimeException("Property must be of AbstractProperty");
        }
        AbstractProperty<V, ?> aProperty = (AbstractProperty<V, ?>) property;
        this.boundTo = aProperty.bindFrom(this, map);
    }

    @Override
    public Optional<Property<?, ?>> bound() {
        return Optional.ofNullable(this.boundTo).map(BindData::from);
    }

    @Override
    public void registerValueChangeEvent(PropertyChangeEvent<T> event) {
        this.changeValueEvents.offer(event);
    }

    @Override
    public void removeBind() {
        if (null == this.boundTo) {
            return;
        }
        this.boundTo.from().bindData.remove(this.boundTo);
        this.boundTo = null;
    }

    @Override
    public Optional<D> value() {
        return this.valueImpl().map(this.displayMappings);
    }

    protected abstract void onValueChange(T newValue, ValueSetType type);

    protected void sendValueChange(T newValue, ValueSetType type) {
        this.bindData.parallelStream().forEach(bindData -> bindData.sendValueTo(newValue, ValueSetType.BOUND));
        this.changeValueEvents.parallelStream().forEach(event -> event.handle(this, this.lastKnownValue, type, newValue));
        this.lastKnownValue = newValue;
    }

    private Optional<T> unmappedValue() {
        return this.valueImpl();
    }

    private <C> Optional<T> valueImpl() {
        if (null == this.boundTo) {
            return Optional.ofNullable(this.lastKnownValue);
        }
        ValueOverrideRule valueOverrideRule = this.valueOverrideRule();
        if (valueOverrideRule == ValueOverrideRule.PREFER_SET && this.lastKnownValueSetAs == ValueSetType.SET) {
            return Optional.ofNullable(this.lastKnownValue);
        }
        return boundValue();
    }

    private <C> Optional<T> boundValue() {
        BindData<C, ?, T, ?> bindData = (BindData<C, ?, T, ?>) this.boundTo;
        if (bindData == null) {
            throw new RuntimeException("BoundValue called without checking if boundto is not null");
        }
        Optional<C> value = bindData.from().unmappedValue();
        return value.map(bindData.mapping());
    }

    @Override
    public ReadOnly<T, D> createBoundReadOnly() {
        ReadOnlyPropertyImpl<T, D> property = new ReadOnlyPropertyImpl<>(this.displayMappings, null);
        property.bindTo(this);
        return property;
    }

    @Override
    public <A, B> ReadOnly<A, B> createBoundReadOnly(Function<T, A> map, Function<A, B> displayMapping) {
        ReadOnlyPropertyImpl<A, B> property = new ReadOnlyPropertyImpl<>(displayMapping, null);
        property.bindTo(this, map);
        return property;
    }
}
