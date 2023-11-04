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

public abstract class AbstractProperty<T, D> extends BaseProperty<T, D> implements Property<T, D> {

    protected final Function<T, D> displayMappings;
    protected final Queue<PropertyChangeEvent<T>> changeValueEvents = new LinkedTransferQueue<>();
    protected T lastKnownValue;
    protected @NotNull ValueSetType lastKnownValueSetAs;
    @Nullable
    private BindData<?, ?, T, D> boundTo;
    private boolean isLocked;

    protected AbstractProperty(@NotNull Function<T, D> displayMappings, @Nullable T defaultValue) {
        this.displayMappings = displayMappings;
        this.lastKnownValue = defaultValue;
        this.lastKnownValueSetAs = ValueSetType.SET;
    }

    @Override
    public <C, F> BindData<T, D, C, F> bindFrom(@NotNull BaseProperty<C, F> property, Function<T, C> mappings) {
        BindData<T, D, C, F> bindData = new BindData<>(this, property, mappings);
        this.bindData.offer(bindData);
        return bindData;
    }

    @Override
    protected Optional<T> valueImpl() {
        if (null == this.boundTo) {
            return Optional.ofNullable(this.lastKnownValue);
        }
        ValueOverrideRule valueOverrideRule = this.valueOverrideRule();
        if ((valueOverrideRule == ValueOverrideRule.PREFER_SET) && (this.lastKnownValueSetAs == ValueSetType.SET)) {
            return Optional.ofNullable(this.lastKnownValue);
        }
        return this.boundValue();
    }

    @Override
    protected Optional<T> unmappedValue() {
        return this.valueImpl();
    }

    @Override
    public <V, E> void bindTo(@NotNull Property<V, E> property, @NotNull Function<V, T> map) {
        if (this.isLocked) {
            throw new IllegalStateException("Cannot modify bind, locked");
        }
        if (null != this.boundTo) {
            this.removeBind();
        }
        if (!(property instanceof BaseProperty)) {
            throw new RuntimeException("Property must be of BaseProperty");
        }
        BaseProperty<V, ?> aProperty = (BaseProperty<V, ?>) property;
        this.boundTo = aProperty.bindFrom(this, map);
    }

    @Override
    public boolean canBind() {
        return !this.isLocked;
    }

    @Override
    public boolean isBindLocked() {
        return this.isLocked;
    }

    @Override
    public void lockBind() {
        this.isLocked = true;
    }

    @Override
    public Optional<Property<?, ?>> bound() {
        return Optional.ofNullable(this.boundTo).map(BindData::from);
    }

    @Override
    public ReadOnly<T, D> createBoundReadOnly() {
        ReadOnly<T, D> property = createReadOnly(this.displayMappings);
        property.bindTo(this);
        return property;
    }

    @Override
    public void registerValueChangeEvent(PropertyChangeEvent<T> event) {
        this.changeValueEvents.offer(event);
    }

    @Override
    public void removeBind() {
        if (this.isLocked) {
            throw new IllegalStateException("Cannot modify bind, locked");
        }
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

    protected void sendValueChange(T newValue, ValueSetType type) {
        this.bindData.parallelStream().forEach(bindData -> bindData.sendValueTo(newValue, ValueSetType.BOUND));
        this.changeValueEvents.parallelStream().forEach(event -> event.handle(this, this.lastKnownValue, type, newValue));
        this.lastKnownValue = newValue;
    }

    private <C> Optional<T> boundValue() {
        BindData<C, ?, T, ?> bindData = (BindData<C, ?, T, ?>) this.boundTo;
        if (bindData == null) {
            throw new RuntimeException("BoundValue called without checking if boundto is not null");
        }
        Optional<C> value = bindData.from().unmappedValue();
        return value.map(bindData.mapping());
    }

    protected abstract ReadOnly<T, D> createReadOnly(Function<T, D> displayMappings);
}
